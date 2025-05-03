package com.labsec.keycloak.utils;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.text.WordUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
import org.keycloak.userprofile.ValidationException;

import com.labsec.keycloak.utils.dtos.ConfirmedUserIdentityDTO;
import com.labsec.keycloak.utils.dtos.rfb.SourceRFBDTO;
import com.labsec.keycloak.utils.dtos.rfb.UserRFBDTO;
import com.labsec.keycloak.utils.level_of_assurance.LOA;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class User {
    private static final Logger LOG = Logger.getLogger(User.class);

    /**
     * Verifica se uma String é uma data válida, no formato especificado
     * 
     * @param inDate
     * @param format
     * @return
     */
    private static boolean isValidDate(String inDate, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);

            dateFormat.setLenient(false);
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }

        return true;
    }

    /**
     * Recupera o primeiro nome do usuário, a partir de seu nome completo
     * 
     * @param fullName
     * @return
     */
    private static String getFirstName(String fullName) {
        int idx = fullName.lastIndexOf(' ');

        return (idx == -1) ? WordUtils.capitalize(fullName) : WordUtils.capitalize(fullName.substring(0, idx));
    }

    /**
     * Recupera os sobrenomes do usuário, a partir de seu nome completo
     * 
     * @param fullName
     * @return
     */
    private static String getLastName(String fullName) {
        int idx = fullName.lastIndexOf(' ');

        return (idx == -1) ? "" : WordUtils.capitalize(fullName.substring(idx + 1));
    }

    public static UserModel createUser(AuthenticationFlowContext context, KeycloakSession session, ConfirmedUserIdentityDTO confirmedUserIdentity) throws ValidationException {
        LOG.infof("[%s | %s] - Criando usuário...", confirmedUserIdentity.getCpf(),
            (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***");

        String username = confirmedUserIdentity.getCpf();

        LOG.infof("[%s | %s] - Adicionando dados ao usuário...", username,
            (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***");

        MultivaluedMap<String, String> userData = new MultivaluedHashMap<>();

        LOG.infof("[%s | %s] - CPF: " + username, username,
            (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***");
        userData.add(UserModel.USERNAME, username);

        LOG.infof("[%s | %s] - Nome completo: " + confirmedUserIdentity.getName(), username,
            (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***");
        userData.add(UserModel.FIRST_NAME, getFirstName(confirmedUserIdentity.getName()));
        userData.add(UserModel.LAST_NAME, getLastName(confirmedUserIdentity.getName()));

        if (session == null)
            session = context.getSession();

        UserProfileProvider profileProvider = session.getProvider(UserProfileProvider.class);
        UserProfile profile = profileProvider.create(UserProfileContext.REGISTRATION, userData);

        if (context != null) {
            context.getEvent().detail(Details.USERNAME, username);
            context.getEvent().detail(Details.FIRST_NAME, getFirstName(confirmedUserIdentity.getName()));
            context.getEvent().detail(Details.LAST_NAME, getLastName(confirmedUserIdentity.getName()));
        }

        LOG.infof("[%s | %s] - Validando dados do usuário...", username,
            (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***");
        profile.validate();

        UserModel user = profile.create();
        user.setFirstName(getFirstName(confirmedUserIdentity.getName()));
        user.setLastName(getLastName(confirmedUserIdentity.getName()));
        user.setSingleAttribute(PlaygroundConstants.BIRTH_ATTR_BIRTH_DATE, confirmedUserIdentity.getBirthdayString());

        LOG.infof("[%s | %s] - Verificando se %s tem formato padrão %s", username,
            (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***",
            PlaygroundConstants.BIRTH_ATTR_BIRTH_DATE, PlaygroundConstants.BIRTH_DATE_MASK_VALUE);

        if (isValidDate(confirmedUserIdentity.getBirthdayString(), PlaygroundConstants.BIRTH_DATE_MASK_VALUE)) {
            LOG.infof("[%s | %s] - %s tem formato de data %s. Setando o meta-atributo...", username,
                (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***",
                PlaygroundConstants.BIRTH_ATTR_BIRTH_DATE, PlaygroundConstants.BIRTH_DATE_MASK_VALUE);

            user.setSingleAttribute(PlaygroundConstants.BIRTH_ATTR_FORMAT_DATE, PlaygroundConstants.BIRTH_DATE_MASK_VALUE);
        } else {
            LOG.infof("[%s | %s] - %s NÃO tem formato de data %s. Meta-atributo não será setado...", username,
                (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***",
                PlaygroundConstants.BIRTH_ATTR_BIRTH_DATE, PlaygroundConstants.BIRTH_DATE_MASK_VALUE);
        }

        user.setSingleAttribute(PlaygroundConstants.USER_ATTR_BIRTH_COUNTRY, confirmedUserIdentity.getNationalityCountryCode().toUpperCase(Locale.ROOT));
        user.setSingleAttribute(PlaygroundConstants.USER_ATTR_BIRTH_STATE, confirmedUserIdentity.getUf().toUpperCase());
        user.setSingleAttribute(PlaygroundConstants.USER_ATTR_BIRTH_CITY, confirmedUserIdentity.getCity().toUpperCase(Locale.ROOT));

        user.setEnabled(true);

        if (context != null) {
            context.setUser(user);

            context.getAuthenticationSession().setClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM, username);

            // Salvamos o evento de registro do usuário usando as ferramentas do Keycloak para que os registros sejam
            // contabilizados pelo SPI de métricas e possam aparecer no painel de monitoramento
            EventBuilder registrationEvent = context.newEvent().event(EventType.REGISTER);
            registrationEvent.user(user);
            registrationEvent.client(context.getAuthenticationSession().getClient().getClientId())
                .detail(Details.REDIRECT_URI, context.getAuthenticationSession().getRedirectUri())
                .detail(Details.AUTH_METHOD, context.getAuthenticationSession().getProtocol());
            String authType = context.getAuthenticationSession().getAuthNote(Details.AUTH_TYPE);

            if (authType != null) {
                registrationEvent.detail(Details.AUTH_TYPE, authType);
            }

            registrationEvent.success();

            // Atualizamos o LoA do usuário
            user.setSingleAttribute(PlaygroundConstants.LOA_USER_ATTRIBUTE, String.valueOf(LOA.LOW.getID()));

            // Se o usuário se registrou utilizando Biometria Facial
            if (Boolean.parseBoolean(context.getAuthenticationSession().getAuthNote(PlaygroundConstants.FACIAL_REGISTRATION_AUTH_NOTE))) {
                LOG.infof("[%s | %s] - O usuário realizou o registro com biometria facial... Atualizando seu LoA!",
                    username, context.getAuthenticationSession().getParentSession().getId());

                user.setSingleAttribute(PlaygroundConstants.LOA_USER_ATTRIBUTE, String.valueOf(LOA.SUBSTANCIAL.getID()));
            }

            // Se o usuário cadastrou endereço de email antes de se registrar
            if (Boolean.parseBoolean(context.getAuthenticationSession().getAuthNote(PlaygroundConstants.EMAIL_REGISTRATION_AUTH_NOTE))) {
                LOG.infof("[%s | %s] - O usuário validou o email antes de se cadastrar... Adicionando atributo \"email\" e \"email_verificado\" ao usuário!",
                    username, context.getAuthenticationSession().getParentSession().getId());

                user.setEmail(context.getAuthenticationSession().getAuthNote(PlaygroundConstants.EMAIL_ADDRESS_AUTH_NOTE));
                user.setEmailVerified(true);
            }

            // Se o usuário cadastrou número de celular antes de se registrar
            if (Boolean.parseBoolean(context.getAuthenticationSession().getAuthNote(PlaygroundConstants.SMS_REGISTRATION_AUTH_NOTE))) {
                LOG.infof("[%s | %s] - O usuário validou o número de celular antes de se cadastrar... Adicionando atributo \"celular\" ao usuário!",
                    username, context.getAuthenticationSession().getParentSession().getId());

                context.getUser().setSingleAttribute(PlaygroundConstants.SMS_ATTR_PHONE_NUMBER, context.getAuthenticationSession().getAuthNote(PlaygroundConstants.SMS_PHONE_NUMBER_AUTH_NOTE).replaceAll("\\D", ""));
            }
        }

        LOG.infof("[%s | %s] - Usuário criado com sucesso!", username,
            (context != null) ? context.getAuthenticationSession().getParentSession().getId() : "***");

        return user;
    }

    public static ConfirmedUserIdentityDTO fetchUserFromExternalAPIs(String cpf, AuthenticationFlowContext context) throws Exception {
        return fetchUserFromExternalAPIs(cpf, context.getRealm(), context.getAuthenticationSession().getParentSession().getId(), context);
    }

    public static ConfirmedUserIdentityDTO fetchUserFromExternalAPIs(String cpf, RealmModel realm, String sessionId) throws Exception {
        return fetchUserFromExternalAPIs(cpf, realm, sessionId, null);
    }

    public static ConfirmedUserIdentityDTO fetchUserFromExternalAPIs(String cpf, RealmModel realm, String sessionId, AuthenticationFlowContext context) throws Exception {
        LOG.infof("[%s | %s] - Confirmando identidade do usuário...",
            cpf, sessionId);

        ConfirmedUserIdentityDTO confirmedUserIdentity = new ConfirmedUserIdentityDTO();
        UserRFBDTO userRFBDTO;
        SourceRFBDTO dataUserRFB = null;

        try {
            // todo: verificar string como "mae desconhecida"
            userRFBDTO = RFBHelper.search(cpf, realm, sessionId);
        } catch (Exception e) {
            e.printStackTrace();

            LOG.errorf("[%s | %s] - Houve um erro ao capturar os dados do usuário na API da Receita Federal!",
                cpf, sessionId);
            LOG.errorf("[%s | %s] - Erro: " + e.getMessage(),
                cpf, sessionId);
            throw new Exception("Houve um erro ao capturar os dados do usuário na API da Receita Federal!");
        }

        if (userRFBDTO != null && userRFBDTO.getHits().size() > 0) {
            LOG.infof("[%s | %s] - Adicionando dados da Receita ao DTO...",
                cpf, sessionId);

            dataUserRFB = userRFBDTO.getHits().get(0).getSource();

            confirmedUserIdentity.setName(dataUserRFB.getName());
            confirmedUserIdentity.setCpf(dataUserRFB.getCpf());
            confirmedUserIdentity.setMotherName(dataUserRFB.getMotherName());
            confirmedUserIdentity.setBirthday(dataUserRFB.getBirthDate(), "yyyyMMdd");
            confirmedUserIdentity.setUf(dataUserRFB.getUfCityOfBirth() != null && !dataUserRFB.getUfCityOfBirth().equals("") ? dataUserRFB.getUfCityOfBirth() : "");
            confirmedUserIdentity.setForeign(dataUserRFB.getForeign() != null && dataUserRFB.getForeign().equals("1"));
            confirmedUserIdentity.setCity(dataUserRFB.getNameMunicipalityPlaceOfBirth() != null && !dataUserRFB.getNameMunicipalityPlaceOfBirth().equals("") ? dataUserRFB.getNameMunicipalityPlaceOfBirth() : "");
            confirmedUserIdentity.setStreetType(dataUserRFB.getStreetType());
            confirmedUserIdentity.setNationalityCountryCode(dataUserRFB.getNationalityCountryCode() != null && !dataUserRFB.getNationalityCountryCode().equals("") ? dataUserRFB.getNationalityCountryCode() : "");
        }

        return confirmedUserIdentity;
    }
}
