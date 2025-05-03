package com.labsec.keycloak.utils;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class PhoneNumberChecker {

    public static boolean check(String phoneNumber, String country) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try {
            // Análise o número de acordo com o país
            PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, country);

            // Verifica se o número é válido para a região dada
            boolean isValidRegionNumber = phoneNumberUtil.isValidNumberForRegion(parsedPhoneNumber, country);

            // Verifica se o número é de um celular e não de um fixo
            boolean isMobileNumber = phoneNumberUtil.getNumberType(parsedPhoneNumber) == PhoneNumberUtil.PhoneNumberType.MOBILE;

            // O número precisa ser válido para a região dada e tem que ser de um celular
            return isValidRegionNumber && isMobileNumber;
        } catch (NumberParseException e) {

            // Caso na análise do número aconteça algo de errado, o número é considerado inválido
            return false;
        }
    }
}