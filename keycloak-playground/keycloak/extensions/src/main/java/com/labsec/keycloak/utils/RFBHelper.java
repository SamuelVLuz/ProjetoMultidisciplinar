package com.labsec.keycloak.utils;

import com.labsec.keycloak.utils.dtos.rfb.UserRFBDTO;
import com.labsec.keycloak.utils.http.HttpHelper;
import com.labsec.keycloak.utils.http.HttpRequestBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;

import com.labsec.keycloak.utils.http.HttpHelper.RequestType;

import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;
import org.keycloak.services.validation.Validation;

import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RFBHelper {
    private static final Logger LOG = Logger.getLogger(RFBHelper.class);

    private static final String RC_RFB_API_URL = "https://cpf.registrocivil.org.br/consultaCPF.php";
    private static final String RC_RFB_API_TOKEN = System.getenv("RC_RFB_API_TOKEN");
    private static final int RC_RFB_API_TIMEOUT = 10000;

    public static UserRFBDTO search(String cpf, RealmModel realm, String sessionId) throws Exception {
        return RFBHelper.search(cpf, null, realm, sessionId);
    }

    public static UserRFBDTO search(String cpf, String name, RealmModel realm, String sessionId) throws Exception {
        LOG.infof("[%s | %s] - Buscando dados do usuário na API da Receita Federal...",
            cpf, sessionId);

        String url = RC_RFB_API_URL + "?tipo=formulario";

        if (!Validation.isBlank(cpf))
            url += "&cpf=" + cpf;
        if (!Validation.isBlank(name))
            url += "&nome=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
        if (!Validation.isBlank(RC_RFB_API_TOKEN))
            url += "&TOKEN=" + RC_RFB_API_TOKEN;

        // Gerando os cabeçalhos da requisição
        Map<String, String> apiHeaders = new HashMap<String, String>();
        apiHeaders.put("Content-Type", "application/json");

        // Gerando requisição HTTP
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
        httpRequestBuilder.setUrl(url)
            .setHeaders(apiHeaders)
            .setRequestType(RequestType.GET)
            .setTimeout(RC_RFB_API_TIMEOUT);

        // Realizando requisição HTTP
        String response = HttpHelper.doRequest(httpRequestBuilder, realm, cpf, sessionId);
        JsonObject jsonResponseRFB = Json.createReader(new StringReader(response)).readObject();

        if (jsonResponseRFB.containsKey("total") && jsonResponseRFB.containsKey("hits")) {
            LOG.infof("[%s | %s] - Dados encontrados na API da Receita Federal!",
                cpf, sessionId);

            return UserRFBDTO.parseJsonToDto(jsonResponseRFB);
        }

        LOG.errorf("[%s | %s] - Nenhum dado encontrado na API da Receita Federal!",
            cpf, sessionId);

        return null;
    }
}
