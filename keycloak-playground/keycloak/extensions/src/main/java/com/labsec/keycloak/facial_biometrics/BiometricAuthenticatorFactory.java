package com.labsec.keycloak.facial_biometrics;

import java.util.Arrays;
import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class BiometricAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "facial-biometric-authenticator";

    public static final String CONFIA_API_URL = "confia_api_url";
    public static final String CONFIA_API_KEY = "confia_api_key";
    public static final String CONFIA_API_TIMEOUT = "confia_api_timeout";
    public static final String RETRIES_NUMBER = "retries_number";
    public static final String SLEEP_TIME = "sleep_time";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Biometric authentication via Confia API";
    }

    @Override
    public String getHelpText() {
        return "Facial biometric authentication via Confia API.";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new BiometricAuthenticator();
    }

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
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[] {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED,
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(
            new ProviderConfigProperty(CONFIA_API_URL, "Biometrics API URL",
                "The URL to connect to the Facial Biometrics API.",
                ProviderConfigProperty.STRING_TYPE, ""),

            new ProviderConfigProperty(CONFIA_API_KEY, "Biometrics API key",
                "The key to connect to the Facial Biometrics API.",
                ProviderConfigProperty.STRING_TYPE, ""),

            new ProviderConfigProperty(CONFIA_API_TIMEOUT, "Biometrics API timeout",
                "The timeout to Facial Biometrics API.",
                ProviderConfigProperty.INTEGER_TYPE, 10000),

            new ProviderConfigProperty(RETRIES_NUMBER, "Number of retries",
                "The number of retries to check the authentication status.",
                ProviderConfigProperty.INTEGER_TYPE, 1000),

            new ProviderConfigProperty(SLEEP_TIME, "Sleep time",
                "The sleep time between each authentication status request.",
                ProviderConfigProperty.INTEGER_TYPE, 500));
    }
}
