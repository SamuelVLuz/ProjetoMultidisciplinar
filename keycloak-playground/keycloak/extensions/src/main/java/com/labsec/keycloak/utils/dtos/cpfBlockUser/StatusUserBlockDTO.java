package com.labsec.keycloak.utils.dtos.cpfBlockUser;

import java.math.BigInteger;

import jakarta.json.JsonObject;

public class StatusUserBlockDTO {

    private String cpf;
    private String reason;
    private String status;
    private String timeUntilUnlock;
    private BigInteger lockTimestamp;
    private BigInteger unlockTimestamp;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeUntilUnlock() {
        return timeUntilUnlock;
    }

    public void setTimeUntilUnlock(String timeUntilUnlock) {
        this.timeUntilUnlock = timeUntilUnlock;
    }

    public BigInteger getLockTimestamp() {
        return lockTimestamp;
    }

    public void setLockTimestamp(BigInteger lockTimestamp) {
        this.lockTimestamp = lockTimestamp;
    }

    public BigInteger getUnlockTimestamp() {
        return unlockTimestamp;
    }

    public void setUnlockTimestamp(BigInteger unlockTimestamp) {
        this.unlockTimestamp = unlockTimestamp;
    }

    public static StatusUserBlockDTO parseJSONToDTO(JsonObject json) {
        StatusUserBlockDTO statusUserBlockDTO = new StatusUserBlockDTO();
        statusUserBlockDTO.setCpf(json.containsKey("cpf") ? json.getString("cpf") : null);
        statusUserBlockDTO.setReason(json.containsKey("motivo") ? json.getString("motivo") : null);
        statusUserBlockDTO.setStatus(json.containsKey("status") ? json.getString("status") : null);
        statusUserBlockDTO.setTimeUntilUnlock(json.containsKey("tempo_ate_desbloqueio") ? json.getString("tempo_ate_desbloqueio") : null);
        statusUserBlockDTO.setLockTimestamp(json.containsKey("timestamp_bloqueio") ? BigInteger.valueOf(json.getInt("timestamp_bloqueio")) : null);
        statusUserBlockDTO.setUnlockTimestamp(json.containsKey("timestamp_liberacao") ? BigInteger.valueOf(json.getInt("timestamp_liberacao")) : null);
        return statusUserBlockDTO;
    }
}
