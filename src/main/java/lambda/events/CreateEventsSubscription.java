package lambda.events;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPIProxy;
import lambda.Constant;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.net.URISyntaxException;
import java.util.Map;

import static lambda.Constant.YAPS_API_SUBSCRIPTIONS_RESOURCE_PATH;

public class CreateEventsSubscription extends AmazonYojakaAPIProxy {

    public CreateEventsSubscription(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        Map requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        HttpPost httpRequest = new HttpPost(
                Util.getBaseUriBuilder()
                        .setPath(YAPS_API_SUBSCRIPTIONS_RESOURCE_PATH)
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
