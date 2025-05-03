package com.labsec.keycloak.sms_reg_auth;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

/**
 * @author Brendon Vicente Rocha Silva, bredstone13@gmail.com
 */
public class PhoneNumberRegistrationAndAuthenticationFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "mobile-number-reg-auth";

    public static final String OTP_LENGTH = "otp_length";
    public static final String OTP_TTL = "otp_ttl";
    public static final String SENDER_ID = "sender_id";
    public static final String ACCESS_KEY = "access_key";
    public static final String SECRET_ACCESS_KEY = "secret_access_key";
    public static final String REGION = "region";
    public static final String SIMULATION = "simulation";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new PhoneNumberRegistrationAndAuthentication();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Mobile number registration/authentication";
    }

    @Override
    public String getHelpText() {
        return "Register/Authenticate user's mobile number.";
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
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(
            new ProviderConfigProperty(OTP_LENGTH, "OTP Length",
                "The length of the OTP to be sent to the user's phone number",
                ProviderConfigProperty.NUMBER_TYPE, 6),

            new ProviderConfigProperty(OTP_TTL, "OTP TTL",
                "The time-to-live of the OTP to be sent to the user's phone number (in seconds)",
                ProviderConfigProperty.NUMBER_TYPE, 300),

            new ProviderConfigProperty(SENDER_ID, "SenderId",
                "The sender ID is displayed as the message sender on the receiving device.",
                ProviderConfigProperty.STRING_TYPE, "Keycloak"),

            new ProviderConfigProperty(ACCESS_KEY, "AWS Access Key ID",
                "The AWS Access Key ID for authentication.",
                ProviderConfigProperty.STRING_TYPE, ""),

            new ProviderConfigProperty(SECRET_ACCESS_KEY, "AWS Secret Key ID",
                "The AWS Access Key ID for authentication.",
                ProviderConfigProperty.PASSWORD, ""),

            new ProviderConfigProperty(REGION, "AWS Region",
                "The AWS Region.",
                ProviderConfigProperty.STRING_TYPE, ""),

            new ProviderConfigProperty(SIMULATION, "Simulation mode",
                "In simulation mode, the SMS won't be sent, but printed to the server logs",
                ProviderConfigProperty.BOOLEAN_TYPE, true));
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
