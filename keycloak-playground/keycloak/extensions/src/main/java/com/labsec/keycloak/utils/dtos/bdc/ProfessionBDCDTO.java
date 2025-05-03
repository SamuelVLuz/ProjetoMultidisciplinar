package com.labsec.keycloak.utils.dtos.bdc;

import java.io.Serializable;

public class ProfessionBDCDTO implements Serializable {
    private String companyName;
    private String startDate;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
