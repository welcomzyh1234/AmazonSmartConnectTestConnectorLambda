package lambda.prices;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPI;
import lambda.Constant;
import lambda.Util;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@AllArgsConstructor
public class UpdatePrice implements AmazonYojakaAPI {

    private final HttpClient httpClient;
    private final APIGatewayV2ProxyRequestEvent event;
    private final Context context;

    @Override
    public HttpResponse invokeAPI() throws IOException, URISyntaxException {
        Map requestPayload = Constant.GSON.fromJson(event.getBody(), Map.class);
        HttpPut httpRequest = new HttpPut(
                Util.getBaseUriBuilder()
                        .setPath(getPath(requestPayload))
                        .setCustomQuery(
                                String.format(
                                        "marketplaceName=%s&channelname=%s",
                                        requestPayload.get("marketplaceName"),
                                        requestPayload.get("channelname")
                                )
                        )
                        .build()
        );
        Util.addBasicHeadersToHttpRequest(httpRequest, event);
        httpRequest.setEntity(
                new StringEntity(
                        Constant.GSON.toJson(requestPayload.get("body")),
                        ContentType.APPLICATION_JSON)
                );
        context.getLogger().log(String.format("[DEBUG] Invoking UpdatePrice API with: \n HTTP Method: %s \n URI: %s \n",
                httpRequest.getMethod(), httpRequest.getURI()));
        return httpClient.execute(httpRequest);
    }

    private String getPath(Map<String, String> requestPayload) {
        return String.format("prod/v1/skus/%s/prices", requestPayload.get("skuId"));
    }
}
