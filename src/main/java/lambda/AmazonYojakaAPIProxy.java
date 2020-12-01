package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

@AllArgsConstructor
public abstract class AmazonYojakaAPIProxy{

    protected final HttpClient httpClient;
    protected final APIGatewayV2ProxyRequestEvent event;
    protected final Context context;

    public HttpResponse invokeAPI() throws IOException, URISyntaxException {
        HttpUriRequest httpRequest = getHttpRequest();
        Util.addLWAAccessTokenToHttpRequest(httpRequest, event);
        context.getLogger().log(String.format("[DEBUG] Invoking %s API with: \n HTTP Method: %s \n URI: %s \n",
                getAPIName(), httpRequest.getMethod(), httpRequest.getURI()));
        HttpResponse response = httpClient.execute(httpRequest);
        context.getLogger().log(String.format("[DEBUG] Http Response: %s \n", response.toString()));
        context.getLogger().log(String.format("[DEBUG] Http Response Body: %s \n",
                EntityUtils.toString(response.getEntity())));
        return response;
    }

    protected abstract HttpUriRequest getHttpRequest() throws URISyntaxException;

    private String getAPIName() {
        String className = this.getClass().toGenericString();
        return className.substring(className.lastIndexOf(".") + 1);
    }
}
