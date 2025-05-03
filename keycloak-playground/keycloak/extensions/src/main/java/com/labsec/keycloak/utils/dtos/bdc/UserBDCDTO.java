package com.labsec.keycloak.utils.dtos.bdc;

import org.jboss.logging.Logger;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserBDCDTO implements Serializable {
    private List<ResultBDCDTO> result = new ArrayList<ResultBDCDTO>();
    private static final Logger LOGGER = Logger.getLogger(UserBDCDTO.class);

    public static final String SESSION_USER_BDC_DTO = "user_bdc";

    public static String serializeToString(UserBDCDTO userBDCDTO) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(userBDCDTO);
            oos.close();
            return baos.toString(StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            LOGGER.errorf("Falha ao serializar: " + e);
            throw e;
        }
    }

    public static UserBDCDTO deserializeFromString(String serializedData) throws IOException, ClassNotFoundException {
        try {
            byte[] data = serializedData.getBytes(StandardCharsets.ISO_8859_1);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            UserBDCDTO userBDCDTO = (UserBDCDTO) ois.readObject();
            ois.close();
            return userBDCDTO;
        } catch (Exception e) {
            LOGGER.errorf("Falha ao desserializar: " + e);
            throw e;
        }
    }

    public List<ResultBDCDTO> getResult() {
        return result;
    }

    public void setResult(List<ResultBDCDTO> result) {
        this.result = result;
    }

    public static UserBDCDTO parseJsonToDto(JsonObject json) {
        UserBDCDTO user = new UserBDCDTO();
        List<ResultBDCDTO> results = new ArrayList<ResultBDCDTO>();
        List<AddressBDCDTO> addresses = new ArrayList<AddressBDCDTO>();
        List<PhoneBDCDTO> phones = new ArrayList<PhoneBDCDTO>();
        List<ProfessionBDCDTO> professions = new ArrayList<ProfessionBDCDTO>();

        if (json == null)
            return user;

        json.getJsonArray("Result").forEach(result -> {
            JsonObject resultObj = result.asJsonObject();
            JsonObject basicDataObj = resultObj.getJsonObject("BasicData");
            JsonArray addressesArr = resultObj.getJsonArray("Addresses");
            JsonArray phonesArr = resultObj.getJsonArray("Phones");
            JsonObject professionData = resultObj.getJsonObject("ProfessionData");
            JsonArray professionsArr = professionData.getJsonArray("Professions");
            ResultBDCDTO resultAux = new ResultBDCDTO();
            BasicDataBDCDTO auxBasicData = new BasicDataBDCDTO();

            if (basicDataObj != null) {
                auxBasicData.setName(basicDataObj.containsKey("Name") && !basicDataObj.isNull("Name") ? getString(basicDataObj.getString("Name")) : null);
                auxBasicData.setMotherName(basicDataObj.containsKey("MotherName") && !basicDataObj.isNull("MotherName") ? getString(basicDataObj.getString("MotherName")) : null);
                auxBasicData.setTaxIdNumber(basicDataObj.containsKey("TaxIdNumber") && !basicDataObj.isNull("TaxIdNumber") ? getString(basicDataObj.getString("TaxIdNumber")) : null);
                auxBasicData.setBirthCountry(basicDataObj.containsKey("BirthCountry") && !basicDataObj.isNull("BirthCountry") ? getString(basicDataObj.getString("BirthCountry")) : null);
                auxBasicData.setBirthDate(basicDataObj.containsKey("BirthDate") && !basicDataObj.isNull("BirthDate") ? formatDate(getString(basicDataObj.getString("BirthDate")), "yyyyMMdd") : null);

                resultAux.setBasicData(auxBasicData);
            }

            if (addressesArr != null && !addressesArr.isEmpty()) {
                addressesArr.forEach(address -> {
                    AddressBDCDTO auxAddress = new AddressBDCDTO();
                    JsonObject addressJson = address.asJsonObject();

                    auxAddress.setCity(addressJson.containsKey("City") && !addressJson.isNull("City") ? getString(addressJson.getString("City")) : null);
                    auxAddress.setCountry(addressJson.containsKey("Country") && !addressJson.isNull("Country") ? getString(addressJson.getString("Country")) : null);
                    auxAddress.setNeighborhood(addressJson.containsKey("Neighborhood") && !addressJson.isNull("Neighborhood") ? getString(addressJson.getString("Neighborhood")) : null);
                    auxAddress.setState(addressJson.containsKey("State") && !addressJson.isNull("State") ? getString(addressJson.getString("State")) : null);
                    auxAddress.setNumber(addressJson.containsKey("Number") && !addressJson.isNull("Number") ? getString(addressJson.getString("Number")) : null);
                    auxAddress.setAddressMain(addressJson.containsKey("AddressMain") && !addressJson.isNull("AddressMain") ? getString(addressJson.getString("AddressMain")) : null);
                    auxAddress.setIsMain(addressJson.containsKey("IsMain") && !addressJson.isNull("IsMain") && addressJson.getBoolean("IsMain"));

                    addresses.add(auxAddress);

                    // TODO: verificar se da pra melhorar custo de chamadas da API
                });

                resultAux.setAddresses(addresses);
            } ;

            if (phonesArr != null && !phonesArr.isEmpty()) {
                phonesArr.forEach(phone -> {
                    PhoneBDCDTO auxPhone = new PhoneBDCDTO();
                    JsonObject phoneJson = phone.asJsonObject();

                    auxPhone.setNumber(phoneJson.containsKey("Number") && !phoneJson.isNull("Number") ? getString(phoneJson.getString("Number")) : null);
                    auxPhone.setAreaCode(phoneJson.containsKey("AreaCode") && !phoneJson.isNull("AreaCode") ? getString(phoneJson.getString("AreaCode")) : null);
                    auxPhone.setCountryCode(phoneJson.containsKey("CountryCode") && !phoneJson.isNull("CountryCode") ? getString(phoneJson.getString("CountryCode")) : null);
                    auxPhone.setType(phoneJson.containsKey("Type") && !phoneJson.isNull("Type") ? getString(phoneJson.getString("Type")) : null);
                    auxPhone.setIsMain(phoneJson.containsKey("IsMain") && !phoneJson.isNull("IsMain") && phoneJson.getBoolean("IsMain"));

                    phones.add(auxPhone);
                });

                resultAux.setPhones(phones);
            } ;

            if (professionsArr != null && !professionsArr.isEmpty()) {
                professionsArr.forEach(profession -> {
                    ProfessionBDCDTO auxProfession = new ProfessionBDCDTO();
                    JsonObject professionsJson = profession.asJsonObject();
                    auxProfession.setCompanyName(professionsJson.containsKey("CompanyName") && !professionsJson.isNull("CompanyName") ? getString(professionsJson.getString("CompanyName")) : null);
                    auxProfession.setStartDate(professionsJson.containsKey("StartDate") && !professionsJson.isNull("StartDate") ? formatDate(getString(professionsJson.getString("StartDate")), "dd/MM/yyyy") : null);

                    professions.add(auxProfession);
                });

                resultAux.setProfessions(professions);
            } ;

            results.add(resultAux);
        });

        user.setResult(results);
        return user;
    }

    private static String getString(String value) {
        if (value == null || value.equals("0") || value.trim().equals("")) {
            return null;
        }

        return value.toLowerCase();
    }

    private static String formatDate(String value, String pattern) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(date);
        } catch (Exception e) {
            LOGGER.info("Não foi possível converter a data: " + e);
            return null;
        }
    }
}
