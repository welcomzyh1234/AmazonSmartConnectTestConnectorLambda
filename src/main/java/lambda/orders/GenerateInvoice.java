package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import lambda.Constant;
import lambda.Util;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class GenerateInvoice extends OrderProcessAPIProxy {

    public GenerateInvoice(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    @Override
    public APIGatewayV2ProxyResponseEvent invokeAPI() throws IOException, URISyntaxException {
        return decryptResponse(getHttpResponse());
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        Map requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        return new HttpPut(
                Util.getBaseUriBuilder()
                        .setPath(getPathFromPutMethod(requestPayload, "generate-invoice"))
                        .build()
        );
    }
}
