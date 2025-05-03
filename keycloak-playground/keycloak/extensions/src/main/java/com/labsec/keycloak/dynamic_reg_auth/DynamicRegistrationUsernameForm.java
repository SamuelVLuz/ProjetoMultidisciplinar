package com.labsec.keycloak.dynamic_reg_auth;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.Constants;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.services.Urls;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;
import org.keycloak.sessions.AuthenticationSessionModel;

import com.labsec.keycloak.utils.PlaygroundConstants;
import com.labsec.keycloak.utils.User;
import com.labsec.keycloak.utils.dtos.ConfirmedUserIdentityDTO;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;

/*
 * @author Thomas Darimont
 * 
 * @author Frederico Schardong
 */
public class DynamicRegistrationUsernameForm extends UsernamePasswordForm {
    private static final Logger LOG = Logger.getLogger(DynamicRegistrationUsernameForm.class);

    /**
     * Valida se o CPF informado é válido
     * 
     * @param cpf
     * @param sessionId
     * @return
     */
    private static boolean validateCPF(String cpf, String sessionId) {
        try {
            CPFValidator cpfValidator = new CPFValidator();
            cpfValidator.assertValid(cpf);

            LOG.infof("[%s | %s] - CPF digitado tem formato valido!",
                cpf, sessionId);

            return true;
        } catch (InvalidStateException e) {
            LOG.errorf("[%s] - O CPF digitado é inválido: " + cpf,
                sessionId);

            return false;
        }
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        super.authenticate(context);

        // Recupera o CPF via query param da URL, através do parâmetro "login_hint"
        String userCpf = context.getAuthenticationSession().getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);

        // Se o CPF foi informado via parâmetro "login_hint", pulamos a tela de entrar com o CPF
        if (!Validation.isBlank(userCpf) && context.getUser() == null) {
            // Configuramos a nota de sessão para uso posterior
            context.getAuthenticationSession().setAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME, userCpf);

            action(context);
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        RealmModel realm = context.getRealm();
        KeycloakSession session = context.getSession();

        LOG.infof("[%s] - Iniciando registro/autenticação...",
            authSession.getParentSession().getId());

        // Recupera os parâmetros enviados no form
        HttpRequest httpRequest = context.getHttpRequest();
        MultivaluedMap<String, String> formData = httpRequest != null ? httpRequest.getDecodedFormParameters() : null;

        // Recupera o CPF do usuário
        String userCpf;
        if (formData != null && formData.containsKey(Validation.FIELD_USERNAME))
            userCpf = formData.getFirst(Validation.FIELD_USERNAME);
        else if (context.getUser() != null)
            userCpf = context.getUser().getUsername();
        else
            userCpf = authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME);

        // Remove todos os caracteres não numéricos do CPF
        userCpf = userCpf.replaceAll("\\D", "");

        // Se o CPF informado não for válido
        if (!validateCPF(userCpf, authSession.getParentSession().getId())) {
            // Renderiza a tela de inserir CPF, indicando erro
            context.form().setAttribute("registrationDisabled", true);
            context.form().addError(new FormMessage(Messages.INVALID_USERNAME));
            context.failureChallenge(AuthenticationFlowError.INVALID_USER,
                context.form().createLoginUsername());
            return;
        }

        // Atualizamos a nota de sessão que pode não ter sido atualizada ainda
        authSession.setAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME, userCpf);

        // Recuperamos o usuário por seu username
        UserModel user = session.users().getUserByUsername(realm, userCpf);

        // Se o usuário ainda não está cadastrado, estamos em um fluxo de registro
        if (user == null) {
            // Salva os dados do usuário, recuperados na API da Receita, nas notas de sessão
            try {
                LOG.infof("[%s | %s] - Salvando dados da receita nas notas de sessão...",
                    userCpf, authSession.getParentSession().getId());

                ConfirmedUserIdentityDTO confirmedUserIdentity = User.fetchUserFromExternalAPIs(userCpf, context);
                authSession.setAuthNote(PlaygroundConstants.SESSION_CONFIRMED_IDENTITY, confirmedUserIdentity.serializeToString());
            } catch (Exception e) {
                // Se a API da Receita estiver fora do ar, não podemos fazer nada
                LOG.errorf("[%s | %s] - Não foi possível confirmar os dados do usuário na API da receita! Erro: " + e.getMessage(),
                    userCpf, authSession.getParentSession().getId());

                // Renderiza a página de erro no servidor
                context.form().addError(new FormMessage("errorConfirmUserIdentity"));
                context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                    context.form().createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
                return;
            }

            LOG.infof("[%s | %s] - Usuário não identificado!",
                userCpf, authSession.getParentSession().getId());

            // Resetamos o fluxo
            context.resetFlow();

            // Recuperamos os parâmetros da query necessários para fazer o redirecionamento para o fluxo de registro
            MultivaluedMap<String, String> queryParameters = context.getUriInfo().getQueryParameters();
            String clientId = queryParameters.getFirst(Constants.CLIENT_ID);
            String tabId = queryParameters.getFirst(Constants.TAB_ID);

            // Construimos a URL do fluxo de registro
            UriBuilder registerUriBuilder = UriBuilder.fromUri(Urls.realmRegisterPage(
                context.getUriInfo().getBaseUri(),
                context.getRealm().getName()));
            URI registerLink = registerUriBuilder
                .queryParam(Constants.CLIENT_ID, clientId == null ? context.getAuthenticationSession().getClient().getId() : clientId)
                .queryParam(Constants.TAB_ID, tabId == null ? context.getAuthenticationSession().getTabId() : tabId)
                .build();

            LOG.infof("[%s | %s] - Redirecionando usuário para o fluxo de registro...",
                userCpf, authSession.getParentSession().getId());

            // Redirecionamos o usuário para o fluxo de registro
            context.challenge(Response.temporaryRedirect(registerLink).build());
        } else { // Do contrário, estamos em um fluxo de autenticação
            // Configuramos o contexto da autenticação com o usuário identificado
            context.setUser(user);

            LOG.infof("[%s | %s] - Usuário identificado!",
                userCpf, authSession.getParentSession().getId());

            // Prosseguimos com a autenticação
            context.success();
        }
    }

    @Override
    protected Response challenge(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        LoginFormsProvider forms = context.form();
        forms.setAttribute("registrationDisabled", true);

        if (!formData.isEmpty()) {
            forms.setFormData(formData);
        }

        return forms.createLoginUsername();
    }

    @Override
    protected Response createLoginForm(LoginFormsProvider form) {
        form.setAttribute("registrationDisabled", true);
        return form.createLoginUsername();
    }

    @Override
    protected String getDefaultChallengeMessage(AuthenticationFlowContext context) {
        if (context.getRealm().isLoginWithEmailAllowed())
            return Messages.INVALID_USERNAME_OR_EMAIL;
        return Messages.INVALID_USERNAME;
    }
}