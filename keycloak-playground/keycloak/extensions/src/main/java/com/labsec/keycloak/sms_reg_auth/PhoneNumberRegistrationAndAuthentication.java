package com.labsec.keycloak.sms_reg_auth;

import java.util.Locale;
import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.validation.Validation;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.theme.Theme;

import com.labsec.keycloak.sms_reg_auth.gateway.SmsService;
import com.labsec.keycloak.sms_reg_auth.gateway.SmsServiceFactory;
import com.labsec.keycloak.utils.PhoneNumberChecker;
import com.labsec.keycloak.utils.PlaygroundConstants;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response.Status;

public class PhoneNumberRegistrationAndAuthentication implements Authenticator {
    private static final Logger LOG = Logger.getLogger(PhoneNumberRegistrationAndAuthentication.class);

    private static final String REGISTRATION_FTL = "phone-number-registration.ftl";
    private static final String VALIDATION_FTL = "phone-number-validation.ftl";

    /**
     * Verifica se o celular fornecido já está em uso por outro usuário no sistema.
     *
     * @param session     A sessão do Keycloak.
     * @param authSession A sessão da autenticação atual.
     * @param realm       O realm.
     * @param user        O usuário atual (pode ser null).
     * @param phone       O celular a ser verificado (pode ser vazio).
     * @return {@code true} se o celular já estiver em uso por outro usuário, {@code false} caso contrário.
     */
    private static boolean isPhoneDuplicated(KeycloakSession session, AuthenticationSessionModel authSession, RealmModel realm, UserModel user, String phone) {
        if (Validation.isBlank(phone))
            return false;

        return session.users().searchForUserByUserAttributeStream(realm, PlaygroundConstants.SMS_ATTR_PHONE_NUMBER, phone.replaceAll("\\D", ""))
            .filter(u -> !u.equals(user)).count() != 0;
    }

    /**
     * Exibe o formulário para cadastro do número de telefone
     * 
     * @param context
     */
    public static void updateMobileNumberForm(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        LOG.infof("[%s | %s] - Realizando cadastro do número de celular...",
            userCpf, authSession.getParentSession().getId());

        // Renderiza a página que solicita o número de celular para o usuário
        String mobileNumberUserAttribute = (user == null) ? "" : user.getFirstAttribute(PlaygroundConstants.SMS_ATTR_PHONE_NUMBER);
        String mobileNumberAuthNote = authSession.getAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE);

        String mobileNumberField = "";
        if (!Validation.isBlank(mobileNumberAuthNote))
            mobileNumberField = mobileNumberAuthNote;
        else if (!Validation.isBlank(mobileNumberUserAttribute))
            mobileNumberField = mobileNumberUserAttribute;

        // Renderiza a página de registro do número de celular
        context.form().setAttribute("mobileNumberField", mobileNumberField);
        context.challenge(context.form().createForm(REGISTRATION_FTL));
    }

    /**
     * Exibe o formulário para validação do número de telefone
     * 
     * @param context
     * @param sameCode
     */
    public static void validateMobileNumberForm(AuthenticationFlowContext context, boolean sameCode) throws Exception {
        UserModel user = context.getUser();
        KeycloakSession session = context.getSession();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        LOG.infof("[%s | %s] - Realizando validação do número de celular...",
            userCpf, authSession.getParentSession().getId());

        // Recuperando o tipo do fluxo atual
        String flowPath = authSession.getAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH);

        // Recuperando o número de celular do usuário e o possível novo celular cadastrado
        String mobileNumber = (user == null) ? "" : user.getFirstAttribute(PlaygroundConstants.SMS_ATTR_PHONE_NUMBER);
        String newMobileNumber = authSession.getAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE);

        if (LoginActionsService.REGISTRATION_PATH.equals(flowPath) && Validation.isBlank(newMobileNumber)) {
            // O usuário está em um fluxo de registro, mas o celular não foi encontrado nas notas de sessão
            LOG.errorf("[%s | %s] - Número de celular não cadastrado!",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página de cadastrar o celular com erro de que não foi encontrado
            context.form().addError(new FormMessage("mobileNumberNotFound"));
            updateMobileNumberForm(context);
            return;
        }

        // Atualizando notas de sessão conforme o fluxo atual
        Boolean redefinitionAllowed = false;
        if (LoginActionsService.REGISTRATION_PATH.equals(flowPath)) {
            authSession.setAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE, newMobileNumber);
            redefinitionAllowed = true;
        } else {
            authSession.setAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE, mobileNumber);
            redefinitionAllowed = false;
        }

        int length = Integer.parseInt(config.get(PhoneNumberRegistrationAndAuthenticationFactory.OTP_LENGTH));
        int ttl = Integer.parseInt(config.get(PhoneNumberRegistrationAndAuthenticationFactory.OTP_TTL));
        String senderId = config.get(PhoneNumberRegistrationAndAuthenticationFactory.SENDER_ID);
        String accessKey = config.get(PhoneNumberRegistrationAndAuthenticationFactory.ACCESS_KEY);
        String secretAccessKey = config.get(PhoneNumberRegistrationAndAuthenticationFactory.SECRET_ACCESS_KEY);
        String region = config.get(PhoneNumberRegistrationAndAuthenticationFactory.REGION);
        Boolean simulation = Boolean.parseBoolean(config.get(PhoneNumberRegistrationAndAuthenticationFactory.SIMULATION));

        String code = "";
        if (sameCode) {
            // Caso seja necessário utilizar o mesmo código já gerado

            authSession.getAuthNote(PlaygroundConstants.SMS_OTP_CODE_AUTH_NOTE);
        }

        if (!sameCode || Validation.isBlank(code)) {
            // Caso seja necessário gerar um código novo ou o código não seja encontrado nas notas de sessão

            code = SecretGenerator.getInstance().randomString(length, SecretGenerator.DIGITS);
            authSession.setAuthNote(PlaygroundConstants.SMS_OTP_CODE_AUTH_NOTE, code);
        }

        // Atualizando o tempo de expiração nas notas de sessão
        authSession.setAuthNote(PlaygroundConstants.SMS_CODE_EXPIRATION_AUTH_NOTE, Long.toString(System.currentTimeMillis() + (ttl * 1000L)));

        try {
            LOG.infof("[%s | %s] - Enviando código %s para o celular %s, válido por %d minuto(s)...",
                userCpf, authSession.getParentSession().getId(), code, authSession.getAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE), Math.floorDiv(ttl, 60));

            // Recuperando o tema e linguagem para criar o SMS com código OTP
            Theme theme = session.theme().getTheme(Theme.Type.LOGIN);
            Locale locale = (user == null) ? Locale.of("pt", "BR") : session.getContext().resolveLocale(user);

            // Recuperando atributos do SMS
            String smsAuthText = theme.getMessages(locale).getProperty("smsAuthText");
            String smsText = String.format(smsAuthText, code, Math.floorDiv(ttl, 60));

            // Enviando o SMS para o número cadastrado
            SmsService smsService = SmsServiceFactory.get(simulation, senderId, accessKey, secretAccessKey, region);
            smsService.send(authSession.getAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE), smsText);

            // Atualizando a página de validação com o celular do usuário
            context.form().setAttribute("phoneNumber", authSession.getAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE));
            context.form().setAttribute("redefinitionAllowed", redefinitionAllowed);

            LOG.infof("[%s | %s] - SMS enviado com sucesso!",
                userCpf, authSession.getParentSession().getId());

            context.challenge(context.form().createForm(VALIDATION_FTL));
        } catch (Exception e) { // Caso não seja possível enviar o SMS
            e.printStackTrace();

            LOG.errorf("[%s | %s] - Não foi possível enviar o SMS para o usuário: " + e.getMessage(),
                userCpf, authSession.getParentSession().getId());

            // Passamos o erro para frente, para ser tratado pelo Authenticator ou RequiredAction
            throw new Exception("smsAuthSmsNotSent");
        }
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        String userCpf = (context.getUser() == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : context.getUser().getUsername();

        // Recuperando o tipo do fluxo atual
        String flowPath = authSession.getAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH);

        try {
            if (LoginActionsService.REGISTRATION_PATH.equals(flowPath)) {
                // Se o fluxo atual for de registro, redirecionamos para o cadastro/atualização do número de telefone

                updateMobileNumberForm(context);
            } else {
                // Se o fluxo atual não for de registro, redirecionamos o usuário para validação do número de telefone

                validateMobileNumberForm(context, false);
            }
        } catch (Exception e) {
            LOG.errorf(e, "[%s | %s] - Algo deu errado durante a autenticação via SMS: " + e.getMessage(),
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página de erro no servidor
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                context.form().setError(!Validation.isBlank(e.getMessage()) ? e.getMessage() : "smsAuthDefaultError")
                    .createErrorPage(Status.BAD_REQUEST));

            // Se o fluxo é condicional ou alternativo, passamos para frente
            if (context.getExecution().isConditional() || context.getExecution().isAlternative())
                context.attempted();
        }
    }

    /**
     * Ação para registrar o número de celular
     * 
     * @param context
     */
    public static void updateMobileNumberAction(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();
        KeycloakSession session = context.getSession();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        LoginFormsProvider form = context.form();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        // Recupera os dados enviados no form
        HttpRequest httpRequest = context.getHttpRequest();
        MultivaluedMap<String, String> formData = httpRequest.getDecodedFormParameters();

        // Recupera o número de celular inserido
        String mobileNumber = formData.getFirst("full_number").replaceAll("[\\s\\()-]", "");
        String country = formData.getFirst("country").toUpperCase();

        // Salva o número de celular nas notas de sessão, para ser utilizado posteriormente
        authSession.setAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE, mobileNumber);

        // Verifica se o número é válido a partir do continente escolhido
        if (!PhoneNumberChecker.check(mobileNumber, country)) {
            LOG.errorf("[%s | %s] - Número de celular inválido! Renderizando página de cadastro do número novamente...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o número de celular para o usuário indicando erro
            form.addError(new FormMessage("updateMobileInvalidInput"));
            updateMobileNumberForm(context);
            return;
        }

        if (isPhoneDuplicated(session, authSession, realm, user, mobileNumber)) {
            // Se o email já foi cadastrado e o realmo não permite duplicatas

            LOG.errorf("[%s | %s] - Celular já cadastrado! Renderizando página de registro de celular novamente...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o número de celular para o usuário indicando erro
            form.addError(new FormMessage("updateMobileAlreadyRegistered"));
            updateMobileNumberForm(context);
            return;
        }

        LOG.infof("[%s | %s] - Número de celular salvo nas notas de sessão.",
            userCpf, authSession.getParentSession().getId());

        // Exibe o formulário de validação do número de celular
        validateMobileNumberForm(context, false);
    }

    /**
     * Ação para validar o número de celular
     * 
     * @param context
     */
    public static void validateMobileNumberAction(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        // Recupera os dados enviados no form
        HttpRequest httpRequest = (HttpRequest) context.getClass().getMethod("getHttpRequest").invoke(context);
        MultivaluedMap<String, String> formData = httpRequest.getDecodedFormParameters();

        // Recupera o código inserido
        String enteredCode = formData.getFirst("code");

        // Recuperando o tipo do fluxo atual
        String flowPath = authSession.getAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH);

        // Recuperando dados das notas de sessão
        String code = authSession.getAuthNote(PlaygroundConstants.SMS_OTP_CODE_AUTH_NOTE);
        String ttl = authSession.getAuthNote(PlaygroundConstants.SMS_CODE_EXPIRATION_AUTH_NOTE);

        String phoneNumber = authSession.getAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE);
        if (Validation.isBlank(phoneNumber)) { // Isso não deveria acontecer
            LOG.errorf("[%s | %s] - Não foi possível recuperar o número de celular nas notas de sessão!",
                userCpf, authSession.getParentSession().getId());

            // Passamos o erro para frente, para ser tratado pelo Authenticator ou RequiredAction
            throw new Exception("updateMobileDefaultError");
        }

        // Caso o usuário tenha selecionado a opção de enviar novamente
        if (formData.containsKey("sendAgain")) {
            LOG.infof("[%s | %s] - Enviando código novamente...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o código enviado via SMS
            validateMobileNumberForm(context, true);
            return;
        }

        // Caso o usuário tenha selecionado a opção de redefinir celular e esteja em um fluxo de cadastro
        if (LoginActionsService.REGISTRATION_PATH.equals(flowPath) && formData.containsKey("redefineNumber")) {
            LOG.infof("[%s | %s] - Redirecionando o usuário para redefinir o número de celular...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o código enviado via SMS
            updateMobileNumberForm(context);
            return;
        }

        // Caso o código ou ttl não sejam encontrados
        if (Validation.isBlank(code) || Validation.isBlank(ttl)) {
            LOG.errorf("[%s | %s] - Código ou time-to-live não encontrados!",
                userCpf, authSession.getParentSession().getId());

            // Passamos o erro para frente, para ser tratado pelo Authenticator ou RequiredAction
            throw new Exception("updateMobileDefaultError");
        }

        // Caso o código digitado esteja incorreto
        if (!enteredCode.trim().equals(code)) {
            LOG.errorf("[%s | %s] - Código inválido! Código digitado: " + enteredCode,
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o código enviado ao celular indicando erro
            context.form().addError(new FormMessage("smsAuthCodeInvalid"));
            context.form().setAttribute("phoneNumber", phoneNumber);
            context.form().setAttribute("redefinitionAllowed", LoginActionsService.REGISTRATION_PATH.equals(flowPath));
            context.challenge(context.form().createForm(VALIDATION_FTL));
            return;
        }

        // Se o código estiver expirado
        if (Long.parseLong(ttl) < System.currentTimeMillis()) {
            LOG.errorf("[%s | %s] - O usuário inseriu um código que expirou! Enviando novo código...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o código enviado ao celular indicando erro
            context.form().addError(new FormMessage("smsAuthCodeExpired"));
            validateMobileNumberForm(context, false);
            return;
        }

        // Se o usuário já está cadastrado na base de dados
        if (user != null) {
            LOG.infof("[%s | %s] - Adicionando atributo \"celular\" ao usuário...",
                userCpf, authSession.getParentSession().getId());

            user.setSingleAttribute(PlaygroundConstants.SMS_ATTR_PHONE_NUMBER, phoneNumber.replaceAll("\\D", ""));
        } else if (LoginActionsService.REGISTRATION_PATH.equals(flowPath)) {
            LOG.infof("[%s | %s] - Usuário ainda não cadastrado no IdRC. Atualizando notas de sessão para indicar que o celular foi informado e validado...",
                userCpf, authSession.getParentSession().getId());

            authSession.setAuthNote(PlaygroundConstants.SMS_REGISTRATION_AUTH_NOTE, "true");
        }

        LOG.infof("[%s | %s] - Número de celular validado com sucesso! Autenticação via SMS bem sucedida!",
            userCpf, authSession.getParentSession().getId());

        // Encerra a ação com sucesso
        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        String userCpf = (context.getUser() == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : context.getUser().getUsername();

        try {
            MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

            if (formData.containsKey("mobileNumberField")) {
                // Se o campo "mobileNumberField" está preenchido, o usuário está cadastrando um telefone novo

                updateMobileNumberAction(context);
            } else {
                // Se o campo "mobileNumberField" não consta, o usuário está validando um telefone já cadastrado

                validateMobileNumberAction(context);
            }
        } catch (Exception e) {
            LOG.errorf("[%s | %s] - Algo deu errado durante o processamento da atualização do celular: " + e.getMessage(),
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página de erro no servidor
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                context.form().setError(!Validation.isBlank(e.getMessage()) ? e.getMessage() : "smsAuthDefaultError")
                    .createErrorPage(Status.BAD_REQUEST));

            // Se o fluxo é condicional ou alternativo, passamos para frente
            if (context.getExecution().isConditional() || context.getExecution().isAlternative())
                context.attempted();
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        String flowPath = session.getContext().getAuthenticationSession().getAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH);

        if (!LoginActionsService.REGISTRATION_PATH.equals(flowPath)) {
            String userPhone = user.getFirstAttribute(PlaygroundConstants.SMS_ATTR_PHONE_NUMBER);

            if (Validation.isBlank(userPhone))
                return false;
        }

        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}

}
