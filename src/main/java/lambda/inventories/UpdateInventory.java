package lambda.inventories;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPIProxy;
import lambda.Constant;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static lambda.Constant.YAPS_API_INVENTORIES_RESOURCE_PATH;

public class UpdateInventory extends AmazonYojakaAPIProxy {

    public UpdateInventory(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        Map<String, String> requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        return new HttpPut(
                Util.getBaseUriBuilder()
                        .setPath(YAPS_API_INVENTORIES_RESOURCE_PATH)
                        .setCustomQuery(
                                String.format(
                                        "locationId=%s&skuId=%s&quantity=%s&inventoryUpdateSequence=%s",
                                        requestPayload.get("locationId"),
                                        requestPayload.get("skuId"),
                                        requestPayload.get("quantity"),
                                        requestPayload.get("inventoryUpdateSequence")
                                )
                        )
                        .build()
        );
    }
}
