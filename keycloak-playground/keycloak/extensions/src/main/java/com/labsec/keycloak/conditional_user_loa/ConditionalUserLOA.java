package com.labsec.keycloak.conditional_user_loa;

import java.util.Map;
import java.util.stream.Stream;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.labsec.keycloak.utils.PlaygroundConstants;
import com.labsec.keycloak.utils.level_of_assurance.LOA;

public class ConditionalUserLOA implements ConditionalAuthenticator {
    @Override
    public boolean matchCondition(AuthenticationFlowContext context) {
        UserModel user = context.getUser();

        // Recuperando atributos das configurações
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String requiredUserLOA = config.get(ConditionalUserLOAFactory.USER_LOA);

        // Recuperando o atributo do usuário
        String userLOAString = user.getFirstAttribute(PlaygroundConstants.LOA_USER_ATTRIBUTE);

        // Se o LoA do usuário for igual a algum dos LoAs solicitados, o fluxo deve ser executado
        return Stream.of(requiredUserLOA.split("##"))
            .anyMatch(loa -> LOA.fromString(loa).equals(LOA.fromInteger(userLOAString)));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // NOOP
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}
