package com.labsec.keycloak.loa_reduction;

import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.util.AcrStore;
import org.keycloak.authentication.authenticators.util.AuthenticatorUtils;
import org.keycloak.models.Constants;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.utils.AmrUtils;
import org.keycloak.sessions.AuthenticationSessionModel;

import com.labsec.keycloak.utils.PlaygroundConstants;
import com.labsec.keycloak.utils.level_of_assurance.LOA;

import jakarta.ws.rs.core.Response.Status;

public class LoAReductionAuthenticator implements Authenticator {
    public static final Logger LOG = Logger.getLogger(LoAReductionAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();

        LOG.infof("[%s | %s] - Iniciando processo de redução do nível de garantia...",
            user.getUsername(), authSession.getParentSession().getId());
        LOG.infof("[%s | %s] - Verificando fatores de autenticação utilizados...",
            user.getUsername(), authSession.getParentSession().getId());

        // Recuperando fatores de autenticação utilizados
        Map<String, Integer> executions = AuthenticatorUtils.parseCompletedExecutions(authSession.getUserSessionNotes().get(Constants.AUTHENTICATORS_COMPLETED));
        List<String> executionRefs = AmrUtils.getAuthenticationExecutionReferences(executions, realm);

        LOG.infof("[%s | %s] - Fatores de autenticação utilizados: " + String.join(", ", executionRefs),
            user.getUsername(), authSession.getParentSession().getId());

        // Verificando se o usuário utilizou biometria facial
        Boolean biometricAuth = executionRefs.contains("face");

        // Se o usuário utilizou biometria facial, atualizamos seu LoA para SUBSTANCIAL
        if (biometricAuth) {
            LOG.infof("[%s | %s] - O usuário utilizou Biometria Facial. Reduzindo seu LoA para SUBSTANCIAL...",
                user.getUsername(), authSession.getParentSession().getId());

            user.setSingleAttribute(PlaygroundConstants.LOA_USER_ATTRIBUTE, String.valueOf(LOA.SUBSTANCIAL.getID()));
        } else { // Se o usuário não utilizou biometria facial, atualizamos seu LoA para BAIXO
            LOG.infof("[%s | %s] - O usuário NÃO utilizou Biometria Facial. Reduzindo seu LoA para BAIXO...",
                user.getUsername(), authSession.getParentSession().getId());

            user.setSingleAttribute(PlaygroundConstants.LOA_USER_ATTRIBUTE, String.valueOf(LOA.LOW.getID()));
        }

        // Recuperando o nível de garantia solicitado pela aplicação
        LOA requestedLOA = LOA.fromInteger(authSession.getClientNote(Constants.REQUESTED_LEVEL_OF_AUTHENTICATION));

        // Atualizando o nível de garantia da autenticação atual
        AcrStore acrStore = new AcrStore(context.getSession(), authSession);
        acrStore.setLevelAuthenticated(LOA.LOW.getID());

        // Se o nível de garantia solicitado for SUBSTANCIAL e o usuário utilizou biometria facial
        if (requestedLOA == LOA.SUBSTANCIAL && biometricAuth) {
            acrStore.setLevelAuthenticated(LOA.SUBSTANCIAL.getID());
        }

        // Se o nível de garantia solicitado for ALTO, o usuário não pode acessar o sistema
        if (requestedLOA == LOA.HIGH) {
            context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED,
                context.form().setError("clientRequiresHighLOA").createErrorPage(Status.UNAUTHORIZED));
            return;
        }

        // Atualizando o histórico de níveis de garantia
        authSession.setUserSessionNote(Constants.LOA_MAP, authSession.getAuthNote(Constants.LOA_MAP));

        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // NOOP
    }

}
