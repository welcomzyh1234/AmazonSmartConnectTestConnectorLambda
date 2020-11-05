package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.Constant;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.net.URISyntaxException;
import java.util.Map;

public class CreatePackages extends OrderProcessAPIProxy {

    public CreatePackages(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        Map requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        HttpPut httpRequest = new HttpPut(
                Util.getBaseUriBuilder()
                        .setPath(getPathFromPutMethod(requestPayload, "create-packages"))
                        .build()
        );
        httpRequest.setEntity(
                new StringEntity(
                        Constant.GSON.toJson(requestPayload.get("body")),
                        ContentType.APPLICATION_JSON)
        );
        return httpRequest;
    }
}
