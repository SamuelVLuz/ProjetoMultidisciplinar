package com.labsec.keycloak.email_reg_auth;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.validation.Validation;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.theme.Theme;
import org.keycloak.theme.beans.MessageFormatterMethod;
import org.keycloak.theme.freemarker.FreeMarkerProvider;

import com.labsec.keycloak.utils.PlaygroundConstants;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response.Status;

public class EMailRegistrationAndAuthentication implements Authenticator {
    private static final Logger LOG = Logger.getLogger(EMailRegistrationAndAuthentication.class);

    private static final String REGISTRATION_FTL = "email-registration.ftl";
    private static final String VALIDATION_FTL = "email-validation.ftl";

    /**
     * Verifica se o email fornecido já está em uso por outro usuário no sistema.
     *
     * @param session     A sessão do Keycloak.
     * @param authSession A sessão da autenticação atual.
     * @param realm       O realm.
     * @param user        O usuário atual (pode ser null).
     * @param email       O email a ser verificado (pode ser vazio).
     * @return {@code true} se o email já estiver em uso por outro usuário, {@code false} caso contrário.
     */
    private boolean isEmailDuplicated(KeycloakSession session, AuthenticationSessionModel authSession, RealmModel realm, UserModel user, String email) {
        if (Validation.isBlank(email))
            return false;

        UserModel userByEmail;
        try {
            userByEmail = session.users().getUserByEmail(realm, email);
        } catch (ModelDuplicateException e) {
            return true;
        }

        return userByEmail != null && !userByEmail.equals(user);
    }

    /**
     * Exibe o formulário para cadastro do email
     * 
     * @param context
     * @param error
     */
    private void updateEMailForm(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        LoginFormsProvider form = context.form();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        LOG.infof("[%s | %s] - Realizando atualização do endereço de e-mail...",
            userCpf, authSession.getParentSession().getId());

        // Renderiza a página que solicita o endereço de email para o usuário
        String emailAddressUserAttribute = (user == null) ? "" : user.getEmail();
        String emailAddressAuthNote = authSession.getAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE);

        String emailField = "";
        if (!Validation.isBlank(emailAddressAuthNote))
            emailField = emailAddressAuthNote;
        else if (!Validation.isBlank(emailAddressUserAttribute))
            emailField = emailAddressUserAttribute;

        form.setAttribute("emailField", emailField);

        context.challenge(form.createForm(REGISTRATION_FTL));
    }

    /**
     * Exibe o formulário para validação do email
     * 
     * @param context
     * @param sameCode
     */
    private void validateEMailForm(AuthenticationFlowContext context, boolean sameCode) throws Exception {
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();
        KeycloakSession session = context.getSession();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        LoginFormsProvider form = context.form();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        LOG.infof("[%s | %s] - Realizando validação de e-mail...",
            userCpf, authSession.getParentSession().getId());

        // Recuperando o tipo do fluxo atual
        String flowPath = authSession.getAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH);

        // Recuperando o email do usuário e o possível novo email cadastrado
        String userEmail = (user == null) ? "" : user.getEmail();
        String newEmail = authSession.getAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE);

        if (LoginActionsService.REGISTRATION_PATH.equals(flowPath) && Validation.isBlank(newEmail)) {
            // O usuário está em um fluxo de registro, mas o email não foi encontrado nas notas de sessão

            LOG.errorf("[%s | %s] - E-mail não cadastrado!",
                userCpf, authSession.getParentSession().getId());

            // Renderiza novamente a página de registro de email com erro de email não encontrado
            form.addError(new FormMessage("emailNotFound"));
            updateEMailForm(context);
            return;
        }

        // Recuperando os parâmetros do código OTP
        int length = Integer.parseInt(config.getOrDefault(EMailRegistrationAndAuthenticationFactory.OTP_LENGTH, "6"));
        int ttl = Integer.parseInt(config.getOrDefault(EMailRegistrationAndAuthenticationFactory.OTP_TTL, "300"));

        String code = "";
        if (sameCode) {
            // Caso seja necessário utilizar o mesmo código já gerado

            code = authSession.getAuthNote(PlaygroundConstants.EMAIL_OTP_CODE_AUTH_NOTE);
        }

        if (!sameCode || Validation.isBlank(code)) {
            // Caso seja necessário gerar um código novo ou o código não seja encontrado nas notas de sessão

            code = SecretGenerator.getInstance().randomString(length, SecretGenerator.DIGITS);
            authSession.setAuthNote(PlaygroundConstants.EMAIL_OTP_CODE_AUTH_NOTE, code);
        }

        // Atualizando o tempo de expiração nas notas de sessão
        authSession.setAuthNote(PlaygroundConstants.EMAIL_CODE_EXPIRATION_AUTH_NOTE, Long.toString(System.currentTimeMillis() + (ttl * 1000L)));

        try {
            if (LoginActionsService.REGISTRATION_PATH.equals(flowPath)) {
                LOG.infof("[%s | %s] - Enviando código %s para o e-mail %s, válido por %d minuto(s)...",
                    userCpf, authSession.getParentSession().getId(), code, newEmail, Math.floorDiv(ttl, 60));
            } else {
                LOG.infof("[%s | %s] - Enviando código %s para o e-mail %s, válido por %d minuto(s)...",
                    userCpf, authSession.getParentSession().getId(), code, userEmail, Math.floorDiv(ttl, 60));
            }

            // Recuperando o tema e linguagem para criar o email com código OTP
            Theme theme = session.theme().getTheme(Theme.Type.LOGIN);
            Theme emailTheme = session.theme().getTheme(Theme.Type.EMAIL);
            Locale locale = (user == null) ? Locale.of("pt", "BR") : session.getContext().resolveLocale(user);

            // Recuperando atributos do email
            String emailAuthText = theme.getMessages(locale).getProperty("emailAuthText");
            String emailText = String.format(emailAuthText, code, Math.floorDiv(ttl, 60));
            String subject = theme.getMessages(locale).getProperty("emailSubject");

            // Atributos do template HTML de email
            Map<String, Object> emailAttributes = new HashMap<String, Object>();
            emailAttributes.put("msg", new MessageFormatterMethod(locale, emailTheme.getMessages(locale)));
            emailAttributes.put("bodyText", emailText);

            String htmlBody = session.getProvider(FreeMarkerProvider.class)
                .processTemplate(emailAttributes, String.format("html/%s", PlaygroundConstants.DEFAULT_EMAIL_TEMPLATE), emailTheme);

            // Enviando o email para o endereço cadastrado
            EmailSenderProvider emailSender = session.getProvider(EmailSenderProvider.class);
            if (LoginActionsService.REGISTRATION_PATH.equals(flowPath)) {
                emailSender.send(realm.getSmtpConfig(), newEmail, subject, emailText, htmlBody);

                authSession.setAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE, newEmail);
                form.setAttribute("redefinitionAllowed", true);
            } else {
                emailSender.send(realm.getSmtpConfig(), userEmail, subject, emailText, htmlBody);

                authSession.setAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE, userEmail);
                form.setAttribute("redefinitionAllowed", false);
            }

            // Atualizando a página de validação com o email do usuário
            form.setAttribute("emailAddress", authSession.getAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE));

            LOG.infof("[%s | %s] - Email enviado com sucesso!",
                userCpf, authSession.getParentSession().getId());

            context.challenge(form.createForm(VALIDATION_FTL));
        } catch (Exception e) { // Caso não seja possível enviar o email
            e.printStackTrace();

            LOG.errorf("[%s | %s] - Não foi possível enviar o email para o usuário: " + e.getMessage(),
                userCpf, authSession.getParentSession().getId());

            // Passamos o erro para frente, para ser tratado pelo try-catch
            throw new Exception("emailAuthNotSent");
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
                // Se o fluxo atual for de registro, redirecionamos o usuário para cadastrar/atualizar seu email

                updateEMailForm(context);
            } else {
                // Se o fluxo atual não for de registro, redirecionamos o usuário para validação do email

                validateEMailForm(context, false);
            }
        } catch (Exception e) {
            LOG.errorf(e, "[%s | %s] - Algo deu errado durante a autenticação via email: " + e.getMessage(),
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página de erro no servidor
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                context.form().setError(!Validation.isBlank(e.getMessage()) ? e.getMessage() : "emailAuthDefaultError")
                    .createErrorPage(Status.BAD_REQUEST));

            // Se o fluxo é condicional ou alternativo, passamos para frente
            if (context.getExecution().isConditional() || context.getExecution().isAlternative())
                context.attempted();
        }
    }

    /**
     * Ação para atualizar o endereço de email
     * 
     * @param context
     */
    private void updateEMailAction(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();
        KeycloakSession session = context.getSession();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        LoginFormsProvider form = context.form();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        // Recupera o email digitado
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String email = formData.getFirst("emailField");

        // Salva o email inserido nas notas de sessão, para ser mostrado no form
        authSession.setAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE, email);

        LOG.infof("[%s | %s] - E-mail salvo nas notas de sessão.",
            userCpf, authSession.getParentSession().getId());

        if (Validation.isBlank(email) || !Validation.isEmailValid(email)) {
            // Se o email não foi fornecido ou não é válido

            LOG.errorf("[%s | %s] - E-mail inválido! Renderizando página de registro de e-mail novamente...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o endereço de email para o usuário indicando erro
            form.addError(new FormMessage("errorRequiredMessageEmail"));
            updateEMailForm(context);
            return;
        }

        if (!realm.isDuplicateEmailsAllowed() && isEmailDuplicated(session, authSession, realm, user, email)) {
            // Se o email já foi cadastrado e o realmo não permite duplicatas

            LOG.errorf("[%s | %s] - E-mail já cadastrado! Renderizando página de registro de e-mail novamente...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o endereço de email para o usuário indicando erro
            form.addError(new FormMessage("updateEMailAlreadyRegistered"));
            updateEMailForm(context);
            return;
        }

        // Exibe o formulário de validação do endereço de email
        validateEMailForm(context, false);
    }

    /**
     * Ação para validar o email
     * 
     * @param context
     */
    public void validateEMailAction(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        LoginFormsProvider form = context.form();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        // Recupera o código OTP digitado
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String enteredCode = formData.getFirst("code");

        // Recuperando o tipo do fluxo atual
        String flowPath = authSession.getAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH);

        // Recuperando dados das notas de sessão
        String code = authSession.getAuthNote(PlaygroundConstants.EMAIL_OTP_CODE_AUTH_NOTE);
        String ttl = authSession.getAuthNote(PlaygroundConstants.EMAIL_CODE_EXPIRATION_AUTH_NOTE);

        String email = authSession.getAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE);
        if (Validation.isBlank(email)) { // Isso não deveria acontecer
            LOG.errorf("[%s | %s] - Não foi possível recuperar o email nas notas de sessão!",
                userCpf, authSession.getParentSession().getId());

            // Passamos o erro para frente, para ser tratado pelo try-catch
            throw new Exception("emailNotFound");
        }

        // Caso o usuário tenha selecionado a opção de enviar novamente
        if (formData.containsKey("sendAgain")) {
            LOG.infof("[%s | %s] - Enviando código novamente...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o código enviado ao email
            validateEMailForm(context, true);
            return;
        }

        // Caso o usuário tenha selecionado a opção de redefinir email e esteja em um fluxo de cadastro
        if (LoginActionsService.REGISTRATION_PATH.equals(flowPath) && formData.containsKey("redefineEmail")) {
            LOG.infof("[%s | %s] - Redirecionando o usuário para redefinir o email...",
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o código enviado ao email
            updateEMailForm(context);
            return;
        }

        // Caso o código ou ttl não sejam encontrados
        if (Validation.isBlank(code) || Validation.isBlank(ttl)) {
            LOG.errorf("[%s | %s] - Código ou time-to-live não encontrados!",
                userCpf, authSession.getParentSession().getId());

            // Passamos o erro para frente, para ser tratado pelo try-catch
            throw new Exception("emailAuthDefaultError");
        }

        // Caso o código digitado esteja incorreto
        if (!enteredCode.trim().toUpperCase().equals(code.toUpperCase())) {
            LOG.errorf("[%s | %s] - Código inválido! Código digitado: " + enteredCode,
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página que solicita o código enviado ao email indicando erro sem enviar um novo código
            form.setAttribute("emailAddress", email);
            form.setAttribute("redefinitionAllowed", LoginActionsService.REGISTRATION_PATH.equals(flowPath));
            form.addError(new FormMessage("emailAuthCodeInvalid"));
            context.challenge(form.createForm(VALIDATION_FTL));
            return;
        }

        // Se o código estiver expirado
        if (Long.parseLong(ttl) < System.currentTimeMillis()) {
            LOG.errorf("[%s | %s] - O usuário inseriu um código que expirou! Enviando novo código...",
                userCpf, authSession.getParentSession().getId());

            form.addError(new FormMessage("emailAuthCodeExpired"));
            validateEMailForm(context, false);
            return;
        }

        // Se o usuário já está cadastrado na base de dados
        if (user != null) {
            LOG.infof("[%s | %s] - Adicionando atributo \"e-mail\" ao usuário...",
                userCpf, authSession.getParentSession().getId());

            user.setEmail(email);
            user.setEmailVerified(true);
        } else if (LoginActionsService.REGISTRATION_PATH.equals(flowPath)) {
            LOG.infof("[%s | %s] - Usuário ainda não cadastrado no IdRC. Atualizando notas de sessão para indicar que o email foi informado e validado...",
                userCpf, authSession.getParentSession().getId());

            authSession.setAuthNote(PlaygroundConstants.EMAIL_REGISTRATION_AUTH_NOTE, "true");
        }

        LOG.infof("[%s | %s] - E-mail validado com sucesso! Autenticação via e-mail bem sucedida!",
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

            if (formData.containsKey("emailField")) {
                // Se o campo "emailField" está preenchido, o usuário está cadastrando um email novo

                updateEMailAction(context);
            } else {
                // Se o campo "emailField" não está preenchido, o usuário está validando um email já cadastrado

                validateEMailAction(context);
            }
        } catch (Exception e) {
            LOG.errorf("[%s | %s] - Algo deu errado durante o processamento da atualização do email: " + e.getMessage(),
                userCpf, authSession.getParentSession().getId());

            // Renderiza a página de erro no servidor
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                context.form().setError(!Validation.isBlank(e.getMessage()) ? e.getMessage() : "updateEmailDefaultError")
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

        if (!LoginActionsService.REGISTRATION_PATH.equals(flowPath) && (Validation.isBlank(user.getEmail()) || !user.isEmailVerified()))
            return false;

        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}

}
