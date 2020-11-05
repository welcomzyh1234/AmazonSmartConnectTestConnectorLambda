package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.Constant;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;
import java.util.Map;

public class RetrievePickupSlots extends OrderProcessAPIProxy {

    public RetrievePickupSlots(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        Map requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        return new HttpPut(
                Util.getBaseUriBuilder()
                        .setPath(getPathFromPutMethod(requestPayload, "retrieve-pickup-slots"))
                        .build()
        );
    }
}
