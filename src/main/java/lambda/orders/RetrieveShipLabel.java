package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;

public class RetrieveShipLabel extends OrderProcessAPIProxy {

    public RetrieveShipLabel(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        return new HttpGet(
                Util.getBaseUriBuilder()
                        .setPath(getPathFromGetMethod(event, "ship-label"))
                        .setCustomQuery(
                                String.format(
                                        "packageId=%s",
                                        event.getQueryStringParameters().get("packageId")
                                )
                        )
                        .build()
        );
    }
}
