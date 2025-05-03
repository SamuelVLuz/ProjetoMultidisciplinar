package com.labsec.keycloak.utils.dtos.rfb;

public class HitsRFBDTO {
    private String index;
    private String type;
    private String id;
    private Number score;
    private SourceRFBDTO source;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Number getScore() {
        return score;
    }

    public void setScore(Number score) {
        this.score = score;
    }

    public SourceRFBDTO getSource() {
        return source;
    }

    public void setSource(SourceRFBDTO source) {
        this.source = source;
    }
}
