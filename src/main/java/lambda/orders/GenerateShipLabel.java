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

public class GenerateShipLabel extends OrderProcessAPIProxy {

    public GenerateShipLabel(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        Map requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        return new HttpPut(
                Util.getBaseUriBuilder()
                        .setPath(getPathFromPutMethod(requestPayload, "generate-ship-label"))
                        .setCustomQuery(
                                String.format(
                                        "packageId=%s&pickupTimeSlotId=%s",
                                        requestPayload.get("packageId"),
                                        requestPayload.get("pickupTimeSlotId")
                                )
                        )
                        .build()
        );
    }
}
