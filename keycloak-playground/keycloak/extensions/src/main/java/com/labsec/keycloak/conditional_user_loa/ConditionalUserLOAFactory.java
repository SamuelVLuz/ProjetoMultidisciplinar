package com.labsec.keycloak.conditional_user_loa;

import java.util.Arrays;
import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class ConditionalUserLOAFactory implements ConditionalAuthenticatorFactory {
    public static final String PROVIDER_ID = "conditional-user-loa";

    public static final String USER_LOA = "user_loa";

    @Override
    public void init(Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Condition - User LoA";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public Requirement[] getRequirementChoices() {
        return new Requirement[] {
            Requirement.DISABLED, Requirement.REQUIRED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Flow is executed only if the user's Level of Assurance (LoA) is the same as the one specified in the configuration.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(
            new ProviderConfigProperty(USER_LOA, "User LoA",
                "The LoA required for the user to pass the condition. The value must be one of the LoA values defined in the Keycloak realm.",
                ProviderConfigProperty.MULTIVALUED_LIST_TYPE, "BAIXO", "BAIXO", "SUBSTANCIAL", "ALTO"));
    }

    @Override
    public ConditionalAuthenticator getSingleton() {
        return new ConditionalUserLOA();
    }
}
