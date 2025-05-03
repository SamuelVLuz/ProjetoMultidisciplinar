package com.labsec.keycloak.sms_reg_auth.gateway;

import org.jboss.logging.Logger;

public class SmsServiceFactory {
    private static final Logger LOG = Logger.getLogger(SmsServiceFactory.class);

    public static SmsService get(Boolean simulation, String senderId, String accessKey, String secretAccessKey, String region) {
        if (simulation) {
            return (phoneNumber, message) -> LOG.warn(String.format("***** SIMULATION MODE ***** Would send SMS to %s with text: %s", phoneNumber, message));
        } else {
            return new AwsSmsService(senderId, accessKey, secretAccessKey, region);
        }
    }
}
