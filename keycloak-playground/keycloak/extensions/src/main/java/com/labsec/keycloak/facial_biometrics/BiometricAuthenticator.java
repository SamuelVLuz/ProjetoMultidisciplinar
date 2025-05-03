package com.labsec.keycloak.facial_biometrics;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.ClientProtocolException;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;

import com.labsec.keycloak.utils.PlaygroundConstants;
import com.labsec.keycloak.utils.http.HttpHelper;
import com.labsec.keycloak.utils.http.HttpHelper.RequestType;
import com.labsec.keycloak.utils.level_of_assurance.LOA;
import com.labsec.keycloak.utils.http.HttpRequestBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

public class BiometricAuthenticator implements Authenticator {
    private static final Logger LOG = Logger.getLogger(BiometricAuthenticator.class);

    private static final String LOADING_FTL = "loading-page.ftl";
    public static final String AUTH_KEY_NOTE = "CONFIA_BIOMETRIC_AUTH_KEY";

    /**
     * Faz uma chamada à API Confia para gerar o link de autenticação biométrica.
     * 
     * @param context
     * @return Objeto JSON contendo URL para autenticação biométrica na API Confia.
     * @throws Exception
     */
    private static JsonObject getAuthUrl(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        String userCpf = user == null
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        LOG.infof("[%s | %s] - Fazendo chamada à API Confia para gerar link de autenticação...",
            userCpf, authSession.getParentSession().getId());

        // Recuperando dados da API de biometria facial
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String apiUrl = config.get(BiometricAuthenticatorFactory.CONFIA_API_URL);
        String apiKey = config.get(BiometricAuthenticatorFactory.CONFIA_API_KEY);
        int apiTimeout = Integer.parseInt(config.get(BiometricAuthenticatorFactory.CONFIA_API_TIMEOUT));

        // Gerando corpo da requisição
        String jsonString;
        if (user == null)
            jsonString = String.format("{\"cpf\": \"%s\"}", userCpf);
        else
            jsonString = String.format("{\"cpf\": \"%s\", \"nome\": \"%s\"}",
                user.getUsername(), user.getFirstName() + " " + user.getLastName());

        // Adicionando caminho à URL da API
        apiUrl += "/company-api/2.0/generateAuthUrl/";

        // Gerando os cabeçalhos da requisição
        Map<String, String> apiHeaders = new HashMap<String, String>();
        apiHeaders.put("x-api-key", apiKey);
        apiHeaders.put("Accept", "application/json");
        apiHeaders.put("Content-Type", "application/json");

        // Gerando requisição HTTP
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
        httpRequestBuilder.setUrl(apiUrl)
            .setHeaders(apiHeaders)
            .setBody(jsonString)
            .setRequestType(RequestType.POST)
            .setTimeout(apiTimeout);

        // Realizando requisição HTTP
        String response = HttpHelper.doRequest(httpRequestBuilder, realm, userCpf, authSession.getParentSession().getId());
        JsonObject jsonResponse = Json.createReader(new StringReader(response)).readObject();

        LOG.infof("[%s | %s] - Resposta da requisição: " + jsonResponse,
            userCpf, authSession.getParentSession().getId());

        // Retornando resposta da requisição, em formato JSON
        return jsonResponse;
    }

    /**
     * Gera o link de autenticação, via API Confia e redireciona o usuário
     * 
     * @param context
     * @throws Exception
     */
    public static void redirectUser(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        LOG.infof("[%s | %s] - Gerando URL de retorno ao Keycloak...",
            userCpf, authSession.getParentSession().getId());

        // Gerando URL de retorno ao Keycloak
        String accessCode = context.generateAccessCode();
        URI actionUrl = context.getActionUrl(accessCode);
        String callbackUrl = URLEncoder.encode(actionUrl.toASCIIString(), StandardCharsets.UTF_8.toString());

        LOG.infof("[%s | %s] - URL de retorno gerada: %s",
            userCpf, authSession.getParentSession().getId(), callbackUrl);

        // Gerando URL de autenticação, via Confia API
        JsonObject jsonResponse = getAuthUrl(context);

        // Verificando se o link de autenticação foi gerado com sucesso
        if (!jsonResponse.containsKey("signUrl")) {
            LOG.errorf("[%s | %s] - Não foi possível gerar link para autenticação.",
                userCpf, authSession.getParentSession().getId());
            LOG.errorf("[%s | %s] - Código: " + jsonResponse.getInt("code"),
                userCpf, authSession.getParentSession().getId());
            LOG.errorf("[%s | %s] - Mensagem: " + jsonResponse.getInt("message"),
                userCpf, authSession.getParentSession().getId());

            throw new Exception("confiaUrlError");
        }

        // Consumindo link de autenticação
        String stringResponse = jsonResponse.getString("signUrl");
        String authKey = stringResponse.substring(stringResponse.indexOf("chave=") + 6);

        // Guardando chave de autenticação, para verificação posterior
        authSession.setAuthNote(BiometricAuthenticator.AUTH_KEY_NOTE, authKey);

        // Gerando URL de autenticação biométrica via Confia API
        String authUrl = stringResponse + "&urlCallback=" + callbackUrl + "&urlCancel=" + callbackUrl;

        LOG.infof("[%s | %s] - URL de autenticação biométrica via Confia API gerada: %s",
            userCpf, authSession.getParentSession().getId(), authUrl);

        LOG.infof("[%s | %s] - Chave de autenticação da Confia gerada: " + authKey,
            userCpf, authSession.getParentSession().getId());

        LOG.infof("[%s | %s] - Redirecionando o usuário...",
            userCpf, authSession.getParentSession().getId());

        // Redirecionando o usuário
        context.challenge(Response.status(Response.Status.FOUND).header("Location", authUrl).build());
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String userCpf = (context.getUser() == null)
            ? context.getAuthenticationSession().getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : context.getUser().getUsername();

        LOG.infof("[%s | %s] - Iniciando biometria facial...",
            userCpf, context.getAuthenticationSession().getParentSession().getId());

        try {
            redirectUser(context);
        } catch (Exception e) {
            LOG.errorf("[%s | %s] - Erro: " + e.getMessage(),
                userCpf, context.getAuthenticationSession().getParentSession().getId());

            String errorMessage = e.getMessage();
            if (e instanceof ClientProtocolException || e instanceof IOException)
                errorMessage = "confiaConnectionError";
            if (errorMessage == null) {
                e.printStackTrace();
                errorMessage = "FacialBiometricsDefaultError";
            }

            // Renderiza a página de erro no servidor
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                context.form()
                    .addError(new FormMessage("FacialBiometricsError"))
                    .addError(new FormMessage(errorMessage))
                    .createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));

            // Se o fluxo é condicional ou alternativo, passamos para frente
            if (context.getExecution().isConditional() || context.getExecution().isAlternative())
                context.attempted();
        }
    }

    /**
     * Verifica o status da autenticação na API Confia.
     * 
     * @param context
     * @return Objeto JSON contendo o status da autenticação.
     * @throws Exception
     */
    public static JsonObject getAuthStatus(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        // Recuperando dados da API de biometria facial
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String apiUrl = config.get(BiometricAuthenticatorFactory.CONFIA_API_URL);
        String apiKey = config.get(BiometricAuthenticatorFactory.CONFIA_API_KEY);
        int apiTimeout = Integer.parseInt(config.get(BiometricAuthenticatorFactory.CONFIA_API_TIMEOUT));
        int retries = Integer.parseInt(config.get(BiometricAuthenticatorFactory.RETRIES_NUMBER));
        int sleep = Integer.parseInt(config.get(BiometricAuthenticatorFactory.SLEEP_TIME));

        // Recuperando a chave de autenticação da API Confia, a partir das notas de sessão
        // Também removemos as notas, após a consulta, para evitar problemas
        String confiaAuthKey = authSession.getAuthNote(BiometricAuthenticator.AUTH_KEY_NOTE);
        authSession.removeAuthNote(BiometricAuthenticator.AUTH_KEY_NOTE);

        // Adicionando caminho à URL da API, de acordo com o tipo de autenticação
        apiUrl += "/company-api/2.0/checkAuthStatus/";

        LOG.infof("[%s | %s] - Fazendo chamada à API Confia para conferir o status da autenticação...",
            userCpf, authSession.getParentSession().getId());

        // Conferindo o status da autenticação 'retries' vezes...
        JsonObject jsonResponse = null;
        for (int counter = 0; counter < retries; counter++) {
            // Gerando os cabeçalhos da requisição
            Map<String, String> apiHeaders = new HashMap<String, String>();
            apiHeaders.put("x-api-key", apiKey);
            apiHeaders.put("Accept", "application/json");
            apiHeaders.put("Content-Type", "application/json");

            // Gerando requisição HTTP
            HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
            httpRequestBuilder.setUrl(apiUrl + confiaAuthKey)
                .setHeaders(apiHeaders)
                .setRequestType(RequestType.GET)
                .setTimeout(apiTimeout);

            // Realizando requisição HTTP
            String response = HttpHelper.doRequest(httpRequestBuilder, realm, userCpf, authSession.getParentSession().getId());
            jsonResponse = Json.createReader(new StringReader(response)).readObject();

            LOG.infof("[%s | %s] - Status da autenticação via Biometria Facial %d/%d => código %d, mensagem: %s",
                userCpf, authSession.getParentSession().getId(), counter,
                retries, jsonResponse.getInt("code"), jsonResponse.getString("message"));
            LOG.infof("[%s | %s] - Resposta da requisição: " + jsonResponse.toString(),
                userCpf, authSession.getParentSession().getId());

            // Verificamos se a autenticação já terminou
            if (jsonResponse.getInt("code") != 0)
                return jsonResponse;

            // Espera 'sleep' milissegundos para realizar uma nova requisição
            try {
                Thread.sleep(sleep);
                continue;
            } catch (InterruptedException e1) {
                throw new RuntimeException("Interrupted");
            }
        }

        // Retorna vazio, se o status da autenticação não for de sucesso, após todas as tentativas
        return null;
    }

    /**
     * Verifica se o usuário se autenticou corretamente via API Confia
     * 
     * @param context
     * @throws Exception
     */
    public static void processAction(AuthenticationFlowContext context) throws Exception {
        UserModel user = context.getUser();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        String userCpf = (user == null)
            ? authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : user.getUsername();

        // Redireciona o usuário para a página de carregamento, assim que retornamos da API
        // Assim garantimos que o usuário terá feedback visual enquanto carregamos as informações
        MultivaluedMap<String, String> formData;
        try {
            HttpRequest request = context.getHttpRequest();
            formData = request.getDecodedFormParameters();

            if (!formData.containsKey("loadingData"))
                throw new Exception();
        } catch (Exception e) {
            LOG.infof("[%s | %s] - Redirecionando o usuário para página de carregamento...",
                userCpf, authSession.getParentSession().getId());

            context.challenge(context.form().createForm(LOADING_FTL));
            return;
        }

        // Verificando status da autenticação na API Confia
        JsonObject jsonResponse = getAuthStatus(context);

        // Verificando se o status é válido
        if (jsonResponse == null) {
            // Se a resposta foi nula...

            LOG.errorf("[%s | %s] - Erro de autenticação... Não houve resposta da API.",
                userCpf, authSession.getParentSession().getId());

            throw new Exception("confiaResponseError");
        } else if (jsonResponse.getInt("code") != 1) {
            // Se o código não foi de sucesso...

            LOG.errorf("[%s | %s] - Erro de autenticação... " + jsonResponse.getString("message"),
                userCpf, authSession.getParentSession().getId());

            throw new Exception(jsonResponse.getString("message"));
        }

        // Encerra a ação com sucesso
        LOG.infof("[%s | %s] - Biometria facial bem sucedida!",
            userCpf, authSession.getParentSession().getId());

        // Se o usuário já existe e seu LoA é BAIXO, vamos atualizar seu LoA
        if (user != null && LOA.fromInteger(user.getFirstAttribute(PlaygroundConstants.LOA_USER_ATTRIBUTE)).equals(LOA.LOW)) {
            user.setSingleAttribute(PlaygroundConstants.LOA_USER_ATTRIBUTE, String.valueOf(LOA.SUBSTANCIAL.getID()));
        } else if (user == null) { // Se o usuário não existe, sinalizamos que ele deve ser criado com LoA SUBSTANCIAL
            authSession.setAuthNote(PlaygroundConstants.FACIAL_REGISTRATION_AUTH_NOTE, "true");
        }

        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        String userCpf = (context.getUser() == null)
            ? context.getAuthenticationSession().getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)
            : context.getUser().getUsername();

        try {
            LOG.infof("[%s | %s] - Processando biometria facial...",
                userCpf, context.getAuthenticationSession().getParentSession().getId());

            processAction(context);
        } catch (Exception e) {
            LOG.errorf("[%s | %s] - Erro: " + e.getMessage(),
                userCpf, context.getAuthenticationSession().getParentSession().getId());

            String errorMessage = e.getMessage();
            if (e instanceof ClientProtocolException || e instanceof IOException)
                errorMessage = "confiaConnectionError";
            if (errorMessage == null) {
                e.printStackTrace();
                errorMessage = "FacialBiometricsDefaultError";
            }

            // Renderiza a página de erro no servidor
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                context.form()
                    .addError(new FormMessage("FacialBiometricsError"))
                    .addError(new FormMessage(errorMessage))
                    .createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));

            // Se o fluxo é condicional ou alternativo, passamos para frente
            if (context.getExecution().isConditional() || context.getExecution().isAlternative())
                context.attempted();
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}
