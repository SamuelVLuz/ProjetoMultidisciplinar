package com.labsec.keycloak.email_reg_auth;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

public class EMailRegistrationAndAuthenticationFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "email-reg-auth";

    public static final String OTP_LENGTH = "otp_length";
    public static final String OTP_TTL = "otp_ttl";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new EMailRegistrationAndAuthentication();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Email Registration/Authentication";
    }

    @Override
    public String getHelpText() {
        return "Register/Authenticate and validate user's email by sending an OTP code.";
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
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(
            new ProviderConfigProperty(OTP_LENGTH, "OTP Length",
                "The length of the OTP to be sent to the user's email address",
                ProviderConfigProperty.NUMBER_TYPE, 6),

            new ProviderConfigProperty(OTP_TTL, "OTP TTL",
                "The time-to-live of the OTP to be sent to the user's email address (in seconds)",
                ProviderConfigProperty.NUMBER_TYPE, 300));
    }

    @Override
    public void init(Config.Scope config) {
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

}
