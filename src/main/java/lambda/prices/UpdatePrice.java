package lambda.prices;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPIProxy;
import lambda.Constant;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.net.URISyntaxException;
import java.util.Map;

public class UpdatePrice extends AmazonYojakaAPIProxy {

    public UpdatePrice(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        Map requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        HttpPut httpRequest = new HttpPut(
                Util.getBaseUriBuilder()
                        .setPath(getPath(requestPayload))
                        .setCustomQuery(
                                String.format(
                                        "marketplaceName=%s&channelName=%s",
                                        requestPayload.get("marketplaceName"),
                                        requestPayload.get("channelname")
                                )
                        )
                        .build()
        );
        httpRequest.setEntity(
                new StringEntity(
                        Constant.GSON.toJson(requestPayload.get("body")),
                        ContentType.APPLICATION_JSON)
        );
        return httpRequest;
    }

    private String getPath(Map<String, String> requestPayload) {
        return String.format("prod/v1/skus/%s/prices", requestPayload.get("skuId"));
    }
}
