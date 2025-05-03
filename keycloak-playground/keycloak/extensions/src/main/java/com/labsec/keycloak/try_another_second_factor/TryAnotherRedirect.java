package com.labsec.keycloak.try_another_second_factor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationSelectionOption;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import org.keycloak.sessions.AuthenticationSessionModel;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class TryAnotherRedirect implements Authenticator {
    private static final Logger LOG = Logger.getLogger(TryAnotherRedirect.class);
    private static final String TRY_ANOTHER_FTL = "try-another.ftl";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String userCpf = (context.getUser() == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : context.getUser().getUsername();

        // Recuperando métodos de autenticação disponíveis no fluxo
        List<AuthenticationSelectionOption> selectionOptions = context.getAuthenticationSelections();
        List<AuthenticationSelectionOption> nonElegibleAuthenticators = new ArrayList<AuthenticationSelectionOption>();

        LOG.infof("[%s | %s] - Carregando opções de autenticação...",
            userCpf, authSession.getParentSession().getId());

        // Para cada método de autenticação disponível...
        for (AuthenticationSelectionOption option : selectionOptions) {
            // Recuperando ID do autenticador
            String authenticatorId = option.getAuthenticationExecution().getAuthenticator();

            // Removemos o TryAnother da lista
            if (authenticatorId.equals(TryAnotherRedirectFactory.PROVIDER_ID)) {
                nonElegibleAuthenticators.add(option);
                continue;
            }

            // Verificando se o método de autenticação é elegível para o usuário
            boolean isCurAuthConfiguredFor = context.getSession().getProvider(Authenticator.class, authenticatorId)
                .configuredFor(context.getSession(), context.getRealm(), context.getUser());

            LOG.infof("[%s | %s] - Autenticador '%s' configurado para o usuário (configuredFor())? %s",
                userCpf, authSession.getParentSession().getId(), authenticatorId, String.valueOf(isCurAuthConfiguredFor));

            if (!isCurAuthConfiguredFor)
                nonElegibleAuthenticators.add(option);
        }

        // Removendo métodos de autenticação não elegíveis para o usuário
        for (AuthenticationSelectionOption option : nonElegibleAuthenticators) {
            selectionOptions.remove(option);
        }

        // Configurando a tela de seleção com as opções disponíveis
        context.setAuthenticationSelections(selectionOptions);

        /**
         * O Keycloak por padrão envia a lista de AuthenticatorSelectionOptions para os arquivos de template .ftl. Porém
         * esta lista contém todos os autenticadores do fluxo atual, e não apenas os elegíveis para o usuário. Para
         * contornar isso, adicionamos às notas de sessão a quantidade de autenticadores elegíveis para o usuário, e a
         * acessamos no CustomFreeMarkerLoginFormsProvider para que os templates .ftl possam acessar para mostrar o
         * botão de tentar outro método quando apropriado.
         **/
        authSession.setAuthNote("availableAuthenticators", Integer.toString(selectionOptions.size()));

        // Se nenhum método de autenticação estiver disponível, uma tela de erro é apresentada
        if (selectionOptions.isEmpty()) {
            LOG.errorf("[%s | %s] - Nenhuma forma de autenticação disponível!",
                userCpf, authSession.getParentSession().getId());

            // Página de erro com mensagem
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, context.form()
                .addError(new FormMessage("tryAnotherRedirectNoOptions"))
                .createErrorPage(Status.INTERNAL_SERVER_ERROR));
            return;
        }

        // Se apenas um método de autenticação estiver disponível, a tela de seleção de autenticador não é apresentada
        if (selectionOptions.size() == 1) {
            // Aqui vai um truque: o Keycloak redireciona o usuário automaticamente para o método authenticate de um
            // autenticador se seu AuthenticationExecutionId estiver no formData de qualquer requisição para uma URL do
            // método action. É assim que o try another funciona originalmente
            String htmlRedirect = "<html><body><form id=\"autoSubmitForm\" action=\"%s\" method=\"post\">";
            htmlRedirect += "<input type=\"hidden\" name=\"%s\" value=\"%s\"></form>";
            htmlRedirect += "<script>document.getElementById('autoSubmitForm').submit();</script></body></html>";

            URI actionUrl = context.getActionUrl(context.generateAccessCode());

            LOG.infof("[%s | %s] - Apenas uma forma de autenticação disponível. Redirecionando o usuário...",
                userCpf, context.getAuthenticationSession().getParentSession().getId());

            String html = String.format(htmlRedirect, actionUrl.toASCIIString(),
                org.keycloak.models.Constants.AUTHENTICATION_EXECUTION, selectionOptions.get(0).getAuthenticationExecution().getId());

            context.challenge(Response.ok(html, MediaType.TEXT_HTML_TYPE).build());

            return;
        }

        // Se estamos num fluxo de registro de 2FA, modificamos o título e descrição da página
        if (Boolean.valueOf(config.get(TryAnotherRedirectFactory.REGISTRATION_2FA))) {
            context.form().setAttribute("tryAnotherTitle", "registration2FAChooseAuthenticator");
            context.form().setAttribute("isRegistrationOptional", true);
            context.form().setAttribute("tryAnotherText", "registration2FAOptionalDescription");
        }

        // Renderizando tela de seleção de fator de autenticação
        context.challenge(context.form().createForm(TRY_ANOTHER_FTL));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        String userCpf = (context.getUser() == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : context.getUser().getUsername();

        // Recuperando configurações do SPI e dados do formulário enviado
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        Boolean is2FARegistration = Boolean.valueOf(config.get(TryAnotherRedirectFactory.REGISTRATION_2FA));

        if (is2FARegistration && formData.containsKey("skipRegister")) {
            LOG.infof("[%s | %s] - O usuário optou por não cadastrar fatores de autenticação. Prosseguindo com o fluxo...",
                userCpf, authSession.getParentSession().getId());

            context.success();
            return;
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}

}
