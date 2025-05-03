package com.labsec.keycloak.sms_reg_auth.gateway;

public interface SmsService {
    void send(String phoneNumber, String message);
}
