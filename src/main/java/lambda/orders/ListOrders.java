package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;

public class ListOrders extends OrderListingAPIProxy {

    public ListOrders(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        return new HttpGet(
                Util.getBaseUriBuilder()
                        .setPath(getPath(event))
                        .setCustomQuery(
                                String.format(
                                        "locationId=%s&status=%s&fromTimestamp=%s&toTimestamp=%s&cursor=%s&maxResults=%s",
                                        event.getQueryStringParameters().get("locationId"),
                                        event.getQueryStringParameters().get("status"),
                                        event.getQueryStringParameters().get("fromTimestamp"),
                                        event.getQueryStringParameters().get("toTimestamp"),
                                        event.getQueryStringParameters().get("cursor"),
                                        event.getQueryStringParameters().get("maxResults")
                                )
                        )
                        .build()
        );
    }
}
