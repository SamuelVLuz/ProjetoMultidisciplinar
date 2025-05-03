package com.labsec.keycloak.utils;

public class PlaygroundConstants {
    // Atributos de usuário
    public static final String LOA_USER_ATTRIBUTE = "level_of_assurance";

    // Registro/Autenticação de usuários
    public static final String SESSION_CONFIRMED_IDENTITY = "confirmed_identity";
    public static final String BIRTH_ATTR_BIRTH_DATE = "data_nascimento";
    public static final String BIRTH_ATTR_FORMAT_DATE = "formato_data_nascimento";
    public static final String BIRTH_DATE_MASK_VALUE = "dd/MM/yyyy";
    public static final String USER_ATTR_BIRTH_COUNTRY = "pais_nascimento";
    public static final String USER_ATTR_BIRTH_STATE = "uf_nascimento";
    public static final String USER_ATTR_BIRTH_CITY = "cidade_nascimento";

    // Cadastro/Validação do e-mail
    public static final String EMAIL_ADDRESS_AUTH_NOTE = "email_address";
    public static final String EMAIL_OTP_CODE_AUTH_NOTE = "email_otp";
    public static final String EMAIL_CODE_EXPIRATION_AUTH_NOTE = "email_otp_expiration";
    public static final String EMAIL_REGISTRATION_AUTH_NOTE = "registro_email";
    public static final String EMAIL_DUPLICATED_AUTH_NOTE = "_email_duplicated";

    public static final String DEFAULT_EMAIL_TEMPLATE = "default-email.ftl";

    // Cadastro/Validação do número de celular
    public static final String SMS_ATTR_PHONE_NUMBER = "celular";
    public static final String SMS_PHONE_NUMBER_AUTH_NOTE = "phone_number";
    public static final String SMS_OTP_CODE_AUTH_NOTE = "phone_otp";
    public static final String SMS_CODE_EXPIRATION_AUTH_NOTE = "phone_otp_expiration";
    public static final String SMS_REGISTRATION_AUTH_NOTE = "registro_sms";
    public static final String SMS_PHONE_NUMBER_DUPLICATED_AUTH_NOTE = "_phone_number_duplicated";

    // Biometria facial
    public static final String FACIAL_REGISTRATION_AUTH_NOTE = "facial_registration";
}
