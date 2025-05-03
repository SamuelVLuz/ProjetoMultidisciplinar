package com.labsec.keycloak.custom_freemarker_provider;

import org.keycloak.Config.Scope;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.forms.login.LoginFormsProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomFreeMarkerLoginFormsProviderFactory implements LoginFormsProviderFactory {
    private static final String PROVIDER_ID = "custom-freemarker";

    @Override
    public LoginFormsProvider create(KeycloakSession session) {
        return new CustomFreeMarkerLoginFormsProvider(session);
    }

    @Override
    public void init(Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}
