package com.labsec.keycloak.utils.dtos;

import com.labsec.keycloak.utils.PlaygroundConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.jboss.logging.Logger;

public class ConfirmedUserIdentityDTO implements Serializable {

    private final static long serialVersionUID = 1;
    private final Logger LOGGER = Logger.getLogger(ConfirmedUserIdentityDTO.class);

    private String name;
    private String cpf;
    private String motherName;
    private LocalDate birthday;
    private boolean isForeign;
    private String uf;
    private String streetType;
    private String street;
    private String streetNumber;
    private String city;
    private String district;
    private String ddd;
    private String phone;
    private String state;
    private String company;
    private String companyDate;
    private String nationalityCountryCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getBirthdayString() {
        return birthday.format(DateTimeFormatter.ofPattern(PlaygroundConstants.BIRTH_DATE_MASK_VALUE));
    }

    public LocalDate getBirthdayLocalDate() {
        return birthday;
    }

    public void setBirthday(String birthday, String format) {
        this.birthday = LocalDate.parse(birthday, DateTimeFormatter.ofPattern(format));
    }

    public boolean isForeign() {
        return isForeign;
    }

    public void setForeign(boolean foreign) {
        isForeign = foreign;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyDate() {
        return companyDate;
    }

    public void setCompanyDate(String companyDate) {
        this.companyDate = companyDate;
    }

    public Boolean hasBasicData() {
        return this.getName() != null && this.getMotherName() != null && this.getBirthdayString() != null;
    }

    public Boolean hasPhone() {
        return this.getPhone() != null && this.getDdd() != null;
    }

    public Boolean hasAddress() {
        return this.getStreet() != null && this.getDistrict() != null;
    }

    public Boolean hasCity() {
        return this.getUf() != null && this.getCity() != null && !this.getUf().equals("") && !this.getCity().equals("");
    }

    public Boolean hasMandatoryData() {
        return this.hasBasicData() && this.hasPhone() && this.hasAddress() && this.hasCity();
    }

    public void setNationalityCountryCode(String nationality) {
        this.nationalityCountryCode = nationality;
    }

    public String getNationalityCountryCode() {
        return this.nationalityCountryCode;
    }

    public String serializeToString() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
            String serializedObject1 = Base64.getEncoder().encodeToString(bos.toByteArray());
            os.close();

            return serializedObject1;
        } catch (Exception ex) {
            LOGGER.error("Erro ao converter ConfirmedUserIdentityDTO para Base64");
            LOGGER.error(ex.getStackTrace());
            return null;
        }
    }

    public static ConfirmedUserIdentityDTO deserializeFromString(String serialized) {
        ConfirmedUserIdentityDTO identity = null;

        try {
            byte[] data = Base64.getDecoder().decode(serialized);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream oInputStream = new ObjectInputStream(bis);
            identity = (ConfirmedUserIdentityDTO) oInputStream.readObject();

            oInputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(ConfirmedUserIdentityDTO.class).error("Erro ao converter ConfirmedUserIdentityDTO para Base64");
            Logger.getLogger(ConfirmedUserIdentityDTO.class).error(ex.getStackTrace());
        }

        return identity;
    }
}
