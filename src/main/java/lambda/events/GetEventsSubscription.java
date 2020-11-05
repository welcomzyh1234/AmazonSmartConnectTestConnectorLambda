package lambda.events;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPIProxy;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;

import static lambda.Constant.YAPS_API_SUBSCRIPTIONS_RESOURCE_PATH;

public class GetEventsSubscription extends AmazonYojakaAPIProxy {

    public GetEventsSubscription(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        return new HttpGet(
                Util.getBaseUriBuilder()
                        .setPath(YAPS_API_SUBSCRIPTIONS_RESOURCE_PATH)
                        .build()
        );
    }
}
