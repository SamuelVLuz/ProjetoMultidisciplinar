package com.labsec.keycloak.try_another_second_factor;

import java.util.Arrays;
import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class TryAnotherRedirectFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "try-another-redirect";

    public static final String REGISTRATION_2FA = "registration_2fa";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "\"Try Another Way\" link redirect";
    }

    @Override
    public String getHelpText() {
        return "Redirects user to \"Try Another Way\" link.";
    }

    @Override
    public String getReferenceCategory() {
        return "redirect";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new TryAnotherRedirect();
    }

    @Override
    public void init(Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public Requirement[] getRequirementChoices() {
        return new Requirement[] {
            Requirement.ALTERNATIVE
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(
            new ProviderConfigProperty(REGISTRATION_2FA, "2FA Registration?", "Is this a 2FA registration flow?",
                ProviderConfigProperty.BOOLEAN_TYPE, false));
    }

}
