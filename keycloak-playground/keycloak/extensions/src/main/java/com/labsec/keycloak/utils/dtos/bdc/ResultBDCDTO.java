package com.labsec.keycloak.utils.dtos.bdc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultBDCDTO implements Serializable {
    private BasicDataBDCDTO basicData;
    private List<AddressBDCDTO> addresses = new ArrayList<AddressBDCDTO>();
    private List<PhoneBDCDTO> phones = new ArrayList<PhoneBDCDTO>();
    private List<ProfessionBDCDTO> professions = new ArrayList<ProfessionBDCDTO>();

    public BasicDataBDCDTO getBasicData() {
        return basicData;
    }

    public void setBasicData(BasicDataBDCDTO basicData) {
        this.basicData = basicData;
    }

    public List<AddressBDCDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressBDCDTO> addresses) {
        this.addresses = addresses;
    }

    public List<PhoneBDCDTO> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneBDCDTO> phones) {
        this.phones = phones;
    }

    public List<ProfessionBDCDTO> getProfessions() {
        return professions;
    }

    public void setProfessions(List<ProfessionBDCDTO> professions) {
        this.professions = professions;
    }
}
