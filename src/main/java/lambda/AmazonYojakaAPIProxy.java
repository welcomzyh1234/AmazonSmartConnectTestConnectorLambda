package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@AllArgsConstructor
public abstract class AmazonYojakaAPIProxy {

    protected final HttpClient httpClient;
    protected final APIGatewayV2ProxyRequestEvent event;
    protected final Context context;

    public APIGatewayV2ProxyResponseEvent invokeAPI() throws IOException, URISyntaxException {
        HttpResponse response = getHttpResponse();

        String responseBody = response.getEntity() == null
                ? "Empty Body"
                : EntityUtils.toString(response.getEntity());

        return buildApiGatewayProxyResponse(response.getStatusLine().getStatusCode(), responseBody);
    }

    protected APIGatewayV2ProxyResponseEvent decryptResponse(HttpResponse response) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity());
        String responseBodyWithDecryptedValue = getResponseBodyWithDecryptedValue(responseBody);

        return buildApiGatewayProxyResponse(response.getStatusLine().getStatusCode(), responseBodyWithDecryptedValue);
    }

    protected HttpResponse getHttpResponse() throws URISyntaxException, IOException {
        HttpUriRequest httpRequest = getHttpRequest();
        Util.addLWAAccessTokenToHttpRequest(httpRequest, event);
        context.getLogger().log(String.format("[DEBUG] Invoking %s API with: \n HTTP Method: %s \n URI: %s \n",
                getAPIName(), httpRequest.getMethod(), httpRequest.getURI()));
        HttpResponse response = httpClient.execute(httpRequest);
        context.getLogger().log(String.format("[DEBUG] Http Response: %s \n", response.toString()));
        return response;
    }

    protected String getAPIName() {
        String className = this.getClass().toGenericString();
        return className.substring(className.lastIndexOf(".") + 1);
    }

    protected APIGatewayV2ProxyResponseEvent buildApiGatewayProxyResponse(int statusCode, String responseBody) throws IOException {
        APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(responseBody);
        response.setHeaders(
                ImmutableMap.of(
                        "Access-Control-Allow-Origin", "*",
                        "Access-Control-Allow-Credentials", "true")
        );
        context.getLogger().log(String.format("[DEBUG] APIGatewayProxyResponse: %s \n", Constant.GSON.toJson(response)));
        return response;
    }

    protected abstract HttpUriRequest getHttpRequest() throws URISyntaxException;

    private String getResponseBodyWithDecryptedValue(String responseBody) {
        Map responseBodyMap = Constant.GSON.fromJson(responseBody, Map.class);
        String encryptedValue =
                Util.getNestedValueFromMap(responseBodyMap, "fileData", "encryptedContent", "value");
        String encryptionInfoContext =
                Util.getNestedValueFromMap(responseBodyMap, "fileData", "encryptedContent", "encryptionInfo", "context");
        String decryptedValue = Util.decryptYojakaEncryptedData(encryptedValue, encryptionInfoContext);
        Map fileData = Util.getNestedValueFromMap(responseBodyMap, "fileData");
        fileData.put("decryptedValue", decryptedValue);
        return Constant.GSON.toJson(responseBodyMap);
    }
}
