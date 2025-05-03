package com.labsec.keycloak.utils.dtos.rfb;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonObject;

public class UserRFBDTO {
    private TotalRFBDTO total = new TotalRFBDTO();
    private long maxScore;
    private List<HitsRFBDTO> hits = new ArrayList<HitsRFBDTO>();

    public TotalRFBDTO getTotal() {
        return total;
    }

    public void setTotal(TotalRFBDTO total) {
        this.total = total;
    }

    public long getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(long maxScore) {
        this.maxScore = maxScore;
    }

    public List<HitsRFBDTO> getHits() {
        return hits;
    }

    public void setHits(List<HitsRFBDTO> hits) {
        this.hits = hits;
    }

    public static UserRFBDTO parseJsonToDto(JsonObject json) {
        UserRFBDTO user = new UserRFBDTO();
        TotalRFBDTO total = new TotalRFBDTO();
        List<HitsRFBDTO> hits = new ArrayList<HitsRFBDTO>();

        if (json == null)
            return user;

        try {
            user.setMaxScore(json.getJsonNumber("max_score").longValue());
        } catch (ClassCastException e) {
            // A API pode não retornar nenhum resultado e o max_score será null. Nesse caso, não atualizamos o DTO.
        }

        json.getJsonArray("hits").forEach(hit -> {
            JsonObject hitObj = hit.asJsonObject();
            JsonObject sourceObj = hitObj.getJsonObject("_source");
            HitsRFBDTO hitAux = new HitsRFBDTO();
            SourceRFBDTO auxSource = new SourceRFBDTO();

            if (sourceObj != null) {
                // Todo: fazer validações de uma forma mais automatica
                auxSource.setUf(sourceObj.containsKey("uf") && !sourceObj.isNull("uf") ? getString(sourceObj.getString("uf")) : null);
                auxSource.setCep(sourceObj.containsKey("cep") && !sourceObj.isNull("cep") ? getString(sourceObj.getString("cep")) : null);
                auxSource.setCpf(sourceObj.containsKey("cpf") && !sourceObj.isNull("cpf") ? getString(sourceObj.getString("cpf")) : null);
                auxSource.setDdd(sourceObj.containsKey("ddd") && !sourceObj.isNull("ddd") ? getString(sourceObj.getString("ddd")) : null);
                auxSource.setDdi(sourceObj.containsKey("ddi") && !sourceObj.isNull("ddi") ? getString(sourceObj.getString("ddi")) : null);
                auxSource.setError(sourceObj.containsKey("erro") && !sourceObj.isNull("erro") ? getString(sourceObj.getString("erro")) : null);
                auxSource.setName(sourceObj.containsKey("nome") && !sourceObj.isNull("nome") ? getString(sourceObj.getString("nome")) : null);
                auxSource.setSex(sourceObj.containsKey("sexo") && !sourceObj.isNull("sexo") ? getString(sourceObj.getString("sexo")) : null);
                auxSource.setDistrict(sourceObj.containsKey("bairro") && !sourceObj.isNull("bairro") ? getString(sourceObj.getString("bairro")) : null);
                auxSource.setMotherName(sourceObj.containsKey("nomemae") && !sourceObj.isNull("nomemae") ? getString(sourceObj.getString("nomemae")) : null);
                auxSource.setObitoYear(sourceObj.containsKey("anoobito") && !sourceObj.isNull("anoobito") ? getString(sourceObj.getString("anoobito")) : null);
                auxSource.setPhone(sourceObj.containsKey("telefone") && !sourceObj.isNull("telefone") ? getString(sourceObj.getString("telefone")) : null);
                auxSource.setCity(sourceObj.containsKey("municipio") && !sourceObj.isNull("municipio") ? getString(sourceObj.getString("municipio")) : null);
                auxSource.setStreet(sourceObj.containsKey("logradouro") && !sourceObj.isNull("logradouro") ? getString(sourceObj.getString("logradouro")) : null);
                auxSource.setSocialName(sourceObj.containsKey("nomesocial") && !sourceObj.isNull("nomesocial") ? getString(sourceObj.getString("nomesocial")) : null);
                auxSource.setComplement(sourceObj.containsKey("complemento") && !sourceObj.isNull("complemento") ? getString(sourceObj.getString("complemento")) : null);
                auxSource.setForeign(sourceObj.containsKey("estrangeiro") && !sourceObj.isNull("estrangeiro") ? getString(sourceObj.getString("estrangeiro")) : null);
                auxSource.setRegistrationDate(sourceObj.containsKey("datainscricao") && !sourceObj.isNull("datainscricao") ? getString(sourceObj.getString("datainscricao")) : null);
                auxSource.setVoterRegistration(sourceObj.containsKey("tituloeleitor") && !sourceObj.isNull("tituloeleitor") ? getString(sourceObj.getString("tituloeleitor")) : null);
                auxSource.setBirthDate(sourceObj.containsKey("datanascimento") && !sourceObj.isNull("datanascimento") ? getString(sourceObj.getString("datanascimento")) : null);
                auxSource.setStreetType(sourceObj.containsKey("tipologradouro") && !sourceObj.isNull("tipologradouro") ? getString(sourceObj.getString("tipologradouro")) : null);
                auxSource.setCityCode(sourceObj.containsKey("codigomunicipio") && !sourceObj.isNull("codigomunicipio") ? getString(sourceObj.getString("codigomunicipio")) : null);
                auxSource.setDateLastChange(sourceObj.containsKey("dataultimaalteracao") && !sourceObj.isNull("dataultimaalteracao") ? getString(sourceObj.getString("dataultimaalteracao")) : null);
                auxSource.setNatureOccupation(sourceObj.containsKey("naturezaocupacao") && !sourceObj.isNull("naturezaocupacao") ? getString(sourceObj.getString("naturezaocupacao")) : null);
                auxSource.setForeignResidentCountryName(sourceObj.containsKey("nomepaisresidenciaexterior") && !sourceObj.isNull("nomepaisresidenciaexterior") ? getString(sourceObj.getString("nomepaisresidenciaexterior")) : null);
                auxSource.setStreetNumber(sourceObj.containsKey("numerologradouro") && !sourceObj.isNull("numerologradouro") ? getString(sourceObj.getString("numerologradouro")) : null);
                auxSource.setExerciseOccupation(sourceObj.containsKey("exercicioocupacao") && !sourceObj.isNull("exercicioocupacao") ? getString(sourceObj.getString("exercicioocupacao")) : null);
                auxSource.setMainOccupation(sourceObj.containsKey("ocupacaoprincipal") && !sourceObj.isNull("ocupacaoprincipal") ? getString(sourceObj.getString("ocupacaoprincipal")) : null);
                auxSource.setForeignResident(sourceObj.containsKey("residenteexterior") && !sourceObj.isNull("residenteexterior") ? getString(sourceObj.getString("residenteexterior")) : null);
                auxSource.setCadastralStatus(sourceObj.containsKey("situacaocadastral") && !sourceObj.isNull("situacaocadastral") ? getString(sourceObj.getString("situacaocadastral")) : null);
                auxSource.setCountryCodeForeignResidence(sourceObj.containsKey("codigopaisresidenciaexterior") && !sourceObj.isNull("codigopaisresidenciaexterior") ? getString(sourceObj.getString("codigopaisresidenciaexterior")) : null);
                auxSource.setNameCountryNationality(sourceObj.containsKey("nomepaisnacionalidade") && !sourceObj.isNull("nomepaisnacionalidade") ? getString(sourceObj.getString("nomepaisnacionalidade")) : null);
                auxSource.setNationalityCountryCode(sourceObj.containsKey("codigopaisnacionalidade") && !sourceObj.isNull("codigopaisnacionalidade") ? getString(sourceObj.getString("codigopaisnacionalidade")) : null);
                auxSource.setUfCityOfBirth(sourceObj.containsKey("ufmunicipionaturalidade") && !sourceObj.isNull("ufmunicipionaturalidade") ? getString(sourceObj.getString("ufmunicipionaturalidade")) : null);
                auxSource.setNameMunicipalityPlaceOfBirth(sourceObj.containsKey("nomemunicipionaturalidade") && !sourceObj.isNull("nomemunicipionaturalidade") ? getString(sourceObj.getString("nomemunicipionaturalidade")) : null);
                auxSource.setAdministrativeUnitName(sourceObj.containsKey("nomeunidadeadministrativa") && !sourceObj.isNull("nomeunidadeadministrativa") ? getString(sourceObj.getString("nomeunidadeadministrativa")) : null);
                auxSource.setCityCodeCityOfBirth(sourceObj.containsKey("codigomunicipioNaturalidade") && !sourceObj.isNull("codigomunicipioNaturalidade") ? getString(sourceObj.getString("codigomunicipioNaturalidade")) : null);
                auxSource.setAdministrativeUnitCode(sourceObj.containsKey("codigounidadeadministrativa") && !sourceObj.isNull("codigounidadeadministrativa") ? getString(sourceObj.getString("codigounidadeadministrativa")) : null);
                auxSource.setSituacaoCadastral(sourceObj.containsKey("situacaocadastral") && !sourceObj.isNull("situacaocadastral") && !sourceObj.getString("situacaocadastral").equalsIgnoreCase("0") ? getString(sourceObj.getString("situacaocadastral")) : null);
                auxSource.setAnoObito(sourceObj.containsKey("anoobito") && !sourceObj.isNull("anoobito") ? getString(sourceObj.getString("anoobito")) : null);

                hitAux.setScore(!hitObj.isNull("_score") ? hitObj.getJsonNumber("_score").longValue() : 0);
                hitAux.setIndex(!hitObj.isNull("_index") ? hitObj.getString("_index") : "");
                hitAux.setType(!hitObj.isNull("_type") ? hitObj.getString("_type") : "");
                hitAux.setId(!hitObj.isNull("_id") ? hitObj.getString("_id") : "");
                hitAux.setSource(auxSource);
                hits.add(hitAux);
            }
        });

        total.setValue(json.getJsonObject("total").getInt("value"));
        total.setRelation(json.getJsonObject("total").getString("relation"));

        user.setTotal(total);
        user.setHits(hits);

        return user;
    }

    private static String getString(String value) {
        if (value == null || value.equals("0") || value.trim().equals("")) {
            return null;
        }

        // return WordUtils.capitalize(value);
        return value.toLowerCase();
    }
}
