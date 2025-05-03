package com.labsec.keycloak.utils.http;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.NameValuePair;

import com.labsec.keycloak.utils.http.HttpHelper.RequestType;

/**
 * Classe que representa uma requisição HTTP
 */
public class HttpRequestBuilder {
    private String url;
    private Map<String, String> headers;
    private Object body;
    private RequestType requestType;
    private int timeout;

    public HttpRequestBuilder() {
        this.requestType = RequestType.GET;
        this.timeout = 10000;
    }

    public String getUrl() {
        return url;
    }

    public HttpRequestBuilder setUrl(String url) {
        this.url = url;

        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;

        return this;
    }

    /**
     * Serializa os cabeçalhos da requisição HTTP em uma String única
     * 
     * @return
     */
    public String getSerializedHeaders() {
        if (headers == null)
            return "";

        return "{" + String.join(", ", headers
            .entrySet()
            .stream()
            .map(entry -> entry.getKey().trim() + ":" + entry.getValue().trim())
            .collect(Collectors.toList())) + "}";
    }

    public Object getBody() {
        return body;
    }

    public HttpRequestBuilder setBody(String body) {
        this.body = body;

        return this;
    }

    public HttpRequestBuilder setBody(List<NameValuePair> body) {
        this.body = body;

        return this;
    }

    /**
     * Serializa o corpo da requisição HTTP em uma String única
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getSerializedBody() {
        if (body == null)
            return "";
        else if (this.body instanceof String)
            return ((String) this.body).trim();
        else {
            List<NameValuePair> bodyList = (List<NameValuePair>) body;

            return "{" + String.join(", ", bodyList
                .stream()
                .map(nameValuePair -> nameValuePair.getName().trim() + ": " + nameValuePair.getValue().trim())
                .collect(Collectors.toList())) + "}";
        }
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public HttpRequestBuilder setRequestType(RequestType requestType) {
        this.requestType = requestType;

        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public HttpRequestBuilder setTimeout(int timeout) {
        this.timeout = timeout;

        return this;
    }
}
