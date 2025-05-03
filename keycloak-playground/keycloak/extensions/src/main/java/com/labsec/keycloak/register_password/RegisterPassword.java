package com.labsec.keycloak.register_password;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.forms.login.LoginFormsPages;
import org.keycloak.forms.login.freemarker.Templates;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.userprofile.ValidationException;

import com.labsec.keycloak.utils.PlaygroundConstants;
import com.labsec.keycloak.utils.User;
import com.labsec.keycloak.utils.dtos.ConfirmedUserIdentityDTO;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response.Status;

import org.keycloak.authentication.forms.RegistrationPage;

public class RegisterPassword implements Authenticator {
    private static final Logger LOG = Logger.getLogger(RegisterPassword.class);

    private static final String FIELD_PASSWORD_NEW = "password-new";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        // Tenta recuperar os dados do usuário salvos nas notas de sessão
        String userIdentity = authSession.getAuthNote(PlaygroundConstants.SESSION_CONFIRMED_IDENTITY);
        ConfirmedUserIdentityDTO confirmedUserIdentity = !Validation.isBlank(userIdentity)
            ? ConfirmedUserIdentityDTO.deserializeFromString(userIdentity)
            : null;

        // Recupera o CPF do usuário
        String userCpf;
        if (confirmedUserIdentity != null)
            userCpf = confirmedUserIdentity.getCpf();
        else if (context.getUser() != null)
            userCpf = context.getUser().getUsername();
        else
            userCpf = authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME);

        LOG.infof("[%s | %s] - Iniciando o cadastro de uma nova senha...",
            userCpf, authSession.getParentSession().getId());

        context.challenge(context.form().setAttribute("username", userCpf)
            .createForm(Templates.getTemplate(LoginFormsPages.LOGIN_UPDATE_PASSWORD)));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        // Tenta recuperar os dados do usuário salvos nas notas de sessão
        String userIdentity = authSession.getAuthNote(PlaygroundConstants.SESSION_CONFIRMED_IDENTITY);
        ConfirmedUserIdentityDTO confirmedUserIdentity = !Validation.isBlank(userIdentity)
            ? ConfirmedUserIdentityDTO.deserializeFromString(userIdentity)
            : null;

        // Recupera o CPF do usuário
        String userCpf;
        if (confirmedUserIdentity != null)
            userCpf = confirmedUserIdentity.getCpf();
        else if (context.getUser() != null)
            userCpf = context.getUser().getUsername();
        else
            userCpf = authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME);

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        List<FormMessage> errors = new ArrayList<>();

        LOG.infof("[%s | %s] - Validando senha...",
            userCpf, authSession.getParentSession().getId());

        // Se o usuário não digitou a senha
        if (Validation.isBlank(formData.getFirst(FIELD_PASSWORD_NEW))) {
            LOG.errorf("[%s | %s] - Senha vazia!",
                userCpf, authSession.getParentSession().getId());

            errors.add(new FormMessage(FIELD_PASSWORD_NEW, Messages.MISSING_PASSWORD));
        }

        // Se as senhas não conferem
        if (!formData.getFirst(FIELD_PASSWORD_NEW).equals(formData.getFirst(RegistrationPage.FIELD_PASSWORD_CONFIRM))) {
            LOG.errorf("[%s | %s] - Confirmação de senha inválida!",
                userCpf, authSession.getParentSession().getId());

            errors.add(new FormMessage(RegistrationPage.FIELD_PASSWORD_CONFIRM, Messages.INVALID_PASSWORD_CONFIRM));
        }

        if (formData.getFirst(FIELD_PASSWORD_NEW) != null) {
            PasswordPolicyManagerProvider passwordPolicyManager = context.getSession().getProvider(PasswordPolicyManagerProvider.class);
            PolicyError err = passwordPolicyManager.validate(context.getRealm().isRegistrationEmailAsUsername() ? formData.getFirst(RegistrationPage.FIELD_EMAIL) : formData.getFirst(RegistrationPage.FIELD_USERNAME), formData.getFirst(RegistrationPage.FIELD_PASSWORD));

            // Se a senha não atende a política de senhas
            if (err != null) {
                LOG.errorf("[%s | %s] - Erro: " + err.getMessage(),
                    userCpf, authSession.getParentSession().getId());

                errors.add(new FormMessage(FIELD_PASSWORD_NEW, err.getMessage(), err.getParameters()));
            }
        }

        // Se houveram erros de validação
        if (errors.size() > 0) {
            context.challenge(context.form().setAttribute("username", userCpf)
                .setErrors(errors).createForm(Templates.getTemplate(LoginFormsPages.LOGIN_UPDATE_PASSWORD)));

            return;
        }

        LOG.infof("[%s | %s] - Senha validada com sucesso!",
            userCpf, authSession.getParentSession().getId());

        String password = formData.getFirst(FIELD_PASSWORD_NEW);
        UserModel user = context.getSession().users().getUserByUsername(context.getRealm(), userCpf);

        // Se o usuário não existe, tentamos criar um novo
        if (user == null) {
            try {
                LOG.infof("[%s | %s] - Criando novo usuário...",
                    userCpf, authSession.getParentSession().getId());

                user = User.createUser(context, null, confirmedUserIdentity);
                context.setUser(user);

                LOG.infof("[%s | %s] - Novo usuário criado com sucesso!",
                    userCpf, authSession.getParentSession().getId());
            } catch (ValidationException pve) {
                pve.printStackTrace();

                LOG.errorf("[%s | %s] - Erro: " + pve.getMessage(),
                    userCpf, authSession.getParentSession().getId());
                LOG.errorf("[%s | %s] - Não foi possível criar o usuário!",
                    userCpf, authSession.getParentSession().getId());

                context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                    context.form().setErrors(Validation.getFormErrorsFromValidation(pve.getErrors()))
                        .createErrorPage(Status.INTERNAL_SERVER_ERROR));

                return;
            }
        }

        try {
            LOG.infof("[%s | %s] - Atualizando senha do usuário...",
                userCpf, authSession.getParentSession().getId());

            user.credentialManager().updateCredential(UserCredentialModel.password(password, false));

            LOG.infof("[%s | %s] - Senha atualizada com sucesso!",
                userCpf, authSession.getParentSession().getId());
        } catch (Exception me) {
            LOG.errorf("[%s | %s] - Não foi possível atualizar a senha do usuário... Adicionando nova ação para atualizar a senha no próximo login...",
                userCpf, authSession.getParentSession().getId());

            user.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);

            LOG.infof("[%s | %s] - Ação adicionada com sucesso!",
                userCpf, authSession.getParentSession().getId());
        }

        context.success();
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}

}
