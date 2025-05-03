package com.labsec.keycloak.dynamic_reg_auth;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class DynamicRegistrationUsernameFormFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "dynamic-reg-auth";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new DynamicRegistrationUsernameForm();
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

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getReferenceCategory() {
        return PasswordCredentialModel.TYPE;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[] {
            AuthenticationExecutionModel.Requirement.REQUIRED
        };
    }

    @Override
    public String getDisplayType() {
        return "SPI que cria ou autentica usuário";
    }

    @Override
    public String getHelpText() {
        return "Esse SPI precisa ser sempre o primeiro. Apresenta o campo de username e busca o que for fornecido no banco. Se existir, apresenta a senha. Se não existir, vai para o registro.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }
}
