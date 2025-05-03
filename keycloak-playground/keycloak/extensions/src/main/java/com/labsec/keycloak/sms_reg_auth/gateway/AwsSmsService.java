package com.labsec.keycloak.sms_reg_auth.gateway;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import java.util.HashMap;
import java.util.Map;

public class AwsSmsService implements SmsService {

    private final SnsClient sns;
    private final String senderId;

    AwsSmsService(String senderId, String accessKey, String secretAccessKey, String region) {
        this.senderId = senderId;

        sns = SnsClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretAccessKey)))
            .region(Region.of(region))
            .build();
    }

    @Override
    public void send(String phoneNumber, String message) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put("AWS.SNS.SMS.SenderID",
            MessageAttributeValue.builder().stringValue(senderId).dataType("String").build());
        messageAttributes.put("AWS.SNS.SMS.SMSType",
            MessageAttributeValue.builder().stringValue("Transactional").dataType("String").build());

        sns.publish(builder -> builder
            .message(message)
            .phoneNumber(phoneNumber)
            .messageAttributes(messageAttributes));
    }

}
