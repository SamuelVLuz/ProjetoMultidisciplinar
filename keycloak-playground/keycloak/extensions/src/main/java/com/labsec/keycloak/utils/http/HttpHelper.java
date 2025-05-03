package com.labsec.keycloak.utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.net.ssl.SSLException;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.apache.kerby.asn1.util.IOUtil;
import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;

/**
 * Classe com funções úteis para realização de requisições HTTP e HTTPS
 */
public class HttpHelper {
    private static final Logger LOG = Logger.getLogger(HttpHelper.class);

    public static enum RequestType {
        GET,
        POST,
        DELETE
    }

    /**
     * Realiza uma requisição HTTP ou HTTPS
     *
     * @param httpRequest
     * @param userCpf
     * @param sessionId
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static String doRequest(HttpRequestBuilder httpRequest, RealmModel realm, String userCpf, String sessionId) throws IllegalArgumentException, IOException {
        LOG.infof("[%s | %s] - Realizando requisição HTTP à URL %s",
            userCpf, sessionId, httpRequest.getUrl());

        // Se a requisição é um POST
        if (httpRequest.getRequestType() == RequestType.POST)
            LOG.infof("[%s | %s] - Corpo da requisição: " + httpRequest.getSerializedBody(),
                userCpf, sessionId);

        // Adicionando atributos à requisição HTTP
        HttpUriRequest request;
        switch (httpRequest.getRequestType()) {
        case GET:
            request = new HttpGet(httpRequest.getUrl());
            break;
        case POST:
            request = new HttpPost(httpRequest.getUrl());
            break;
        case DELETE:
            request = new HttpDelete(httpRequest.getUrl());
            break;

        default:
            LOG.errorf("[%s | %s] - Houve um erro ao identificar o tipo de requisição solicitada! Os tipos disponiveis são: " + RequestType.values(),
                userCpf, sessionId);

            throw new IllegalArgumentException("Invalid request type");
        }

        // Adicionando cabeçalhos à requisição HTTP
        if (httpRequest.getHeaders() != null) {
            httpRequest.getHeaders().forEach((key, value) -> {
                request.addHeader(key, value);
            });
        }

        // Adicionando corpo à requisição HTTP, caso seja necessário
        if (request instanceof HttpPost) {
            HttpPost post = (HttpPost) request;

            if (httpRequest.getBody() instanceof String) {
                post.setEntity(new StringEntity((String) httpRequest.getBody(), ContentType.APPLICATION_JSON));
            } else if (httpRequest.getBody() instanceof List<?>) {
                post.setEntity(new UrlEncodedFormEntity((Iterable<NameValuePair>) httpRequest.getBody()));
            }
        }

        // Realizando requisição HTTP
        RequestConfig config = RequestConfig.custom()
            .setCookieSpec(StandardCookieSpec.STRICT)
            .setConnectTimeout(Timeout.ofMilliseconds(httpRequest.getTimeout()))
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(httpRequest.getTimeout()))
            .setResponseTimeout(Timeout.ofMilliseconds(httpRequest.getTimeout())).build();
        CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        // Gravando instante de início da requisição para logs
        Instant end, start = Instant.now();

        try {
            // Executa a requisição
            CloseableHttpResponse rawResponse = httpclient.execute(request);
            end = Instant.now();

            LOG.infof("[%s | %s] - A requisição à URL %s levou %dms e retornou com código %d",
                userCpf, sessionId, httpRequest.getUrl(), Duration.between(start, end).toMillis(), rawResponse.getCode());

            String response = responseToString(rawResponse);

            // Fecha a conexão http
            httpclient.close();

            return response;
        } catch (Exception e) {
            end = Instant.now();

            // Fecha a conexão http
            httpclient.close();

            if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException)
                LOG.errorf("[%s | %s] - Houve um erro de conexão! A requisição à URL %s demorou mais de %dms e foi abortada!",
                    userCpf, sessionId, httpRequest.getUrl(), httpRequest.getTimeout());
            else if (e instanceof SSLException)
                LOG.errorf("[%s | %s] - Houve um erro de conexão! O certificado SSL da URL %s não pôde ser validado! A requisição levou %dms.",
                    userCpf, sessionId, httpRequest.getUrl(), Duration.between(start, end).toMillis());
            else if (e instanceof UnknownHostException)
                LOG.errorf("[%s | %s] - Houve um erro de conexão! Não foi possível determinar o endereço IP correspondente à URL %s! A requisição levou %dms.",
                    userCpf, sessionId, httpRequest.getUrl(), Duration.between(start, end).toMillis());
            else {
                LOG.errorf("[%s | %s] - Houve um erro de conexão! Não foi possível se conectar à URL %s! A requisição levou %dms. Erro: " + e.getMessage(),
                    userCpf, sessionId, httpRequest.getUrl(), Duration.between(start, end).toMillis());

                e.printStackTrace();
            }

            throw e;
        }
    }

    /**
     * Converte a reposta à uma requisição HTTP em String
     *
     * @param response
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static String responseToString(CloseableHttpResponse response) throws ClientProtocolException, IOException {
        String res = null;
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            byte[] bytes = IOUtil.readInputStream(instream);
            res = new String(bytes, "UTF-8");
            res = res.replaceAll("^Authorized", "");
            instream.close();
        }

        return res;
    }
}
