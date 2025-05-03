package com.labsec.keycloak.utils.level_of_assurance;

public enum LOA {
    LOW(1),
    SUBSTANCIAL(100),
    HIGH(200);

    private int id;

    LOA(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public static LOA fromInteger(int x) {
        if (x >= HIGH.id)
            return HIGH;
        if (x >= SUBSTANCIAL.id)
            return SUBSTANCIAL;
        return LOW;
    }

    public static LOA fromInteger(String x) {
        try {
            return fromInteger(Integer.parseInt(x.replaceAll("\\D", "")));
        } catch (NumberFormatException | NullPointerException e) {
            return LOW;
        }
    }

    public static LOA fromString(String loaString) {
        switch (loaString.toUpperCase().trim()) {
        case "BAIXO":
            return LOW;
        case "SUBSTANCIAL":
            return SUBSTANCIAL;
        case "ALTO":
            return HIGH;
        default:
            return LOW;
        }
    }
}
