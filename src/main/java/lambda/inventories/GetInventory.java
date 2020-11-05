package lambda.inventories;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPIProxy;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;

import static lambda.Constant.YAPS_API_INVENTORIES_RESOURCE_PATH;

public class GetInventory extends AmazonYojakaAPIProxy {

    public GetInventory(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        return new HttpGet(
                Util.getBaseUriBuilder()
                        .setPath(YAPS_API_INVENTORIES_RESOURCE_PATH)
                        .setCustomQuery(
                                String.format(
                                        "locationId=%s&skuId=%s",
                                        event.getQueryStringParameters().get("locationId"),
                                        event.getQueryStringParameters().get("skuId")
                                )
                        )
                        .build()
        );
    }
}
