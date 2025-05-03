package com.labsec.keycloak.custom_freemarker_provider;

import java.util.Locale;
import java.util.Properties;

import org.apache.commons.text.WordUtils;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.forms.login.LoginFormsPages;
import org.keycloak.forms.login.freemarker.FreeMarkerLoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.theme.Theme;

import jakarta.ws.rs.core.UriBuilder;

import org.keycloak.services.validation.Validation;

public class CustomFreeMarkerLoginFormsProvider extends FreeMarkerLoginFormsProvider {
    public CustomFreeMarkerLoginFormsProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected void createCommonAttributes(Theme theme, Locale locale, Properties messagesBundle, UriBuilder baseUriBuilder, LoginFormsPages page) {
        super.createCommonAttributes(theme, locale, messagesBundle, baseUriBuilder, page);

        if (context != null) {
            // Variável que carrega o número de autenticadores disponíveis ao usuário no fluxo atual. Serve para mostrar
            // o botão de tentar outro autenticador apenas quando existe mais de um autenticador disponível.
            String availableAuthenticatorsAuthNote = context.getAuthenticationSession().getAuthNote("availableAuthenticators");

            int availableAuthenticatorsCount;
            if (!Validation.isBlank(availableAuthenticatorsAuthNote)) {
                availableAuthenticatorsCount = Integer.parseInt(availableAuthenticatorsAuthNote);
                attributes.put("tryAnotherAuthenticators", availableAuthenticatorsCount);
            } else {
                attributes.put("tryAnotherAuthenticators", 0);
            }
            attributes.put("flowPath", new String(context.getFlowPath().toLowerCase()));

            if (context.getUser() != null) {
                attributes.put("userFirstName", new String(WordUtils.capitalize(context.getUser().getFirstName())));
                attributes.put("userLastName", new String(WordUtils.capitalize(context.getUser().getLastName())));
                attributes.put("userFullName", new String(WordUtils.capitalize(context.getUser().getFirstName() + " " + context.getUser().getLastName())));
            } else {
                String attemptedUsername = context.getAuthenticationSession().getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME);
                if (attemptedUsername == null)
                    attemptedUsername = "";

                attributes.put("userFirstName", new String(attemptedUsername));
                attributes.put("userLastName", new String(attemptedUsername));
                attributes.put("userFullName", new String(attemptedUsername));
            }
        } else {
            attributes.put("userFirstName", "");
            attributes.put("userLastName", "");
            attributes.put("userFullName", "");
        }
    }
}
