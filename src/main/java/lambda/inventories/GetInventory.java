package lambda.inventories;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPI;
import lambda.Constant;
import lambda.Util;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.net.URISyntaxException;

import static lambda.Constant.AU_YAPS_API_INVENTORIES_RESOURCE_PATH;
import static lambda.Constant.LWA_ACCESS_TOKEN_HEADER_KEY_NAME;

@AllArgsConstructor
public class GetInventory implements AmazonYojakaAPI {

    private final HttpClient httpClient;
    private final APIGatewayV2ProxyRequestEvent event;
    private final Context context;

    @Override
    public HttpResponse invokeAPI() throws IOException, URISyntaxException {
        HttpUriRequest httpRequest = new HttpGet(
                Util.getBaseUriBuilder()
                        .setPath(AU_YAPS_API_INVENTORIES_RESOURCE_PATH)
                        .setCustomQuery(
                                String.format(
                                        "locationId=%s&skuId=%s",
                                        event.getQueryStringParameters().get("locationId"),
                                        event.getQueryStringParameters().get("skuId")
                                )
                        )
                        .build()
        );
        Util.addBasicHeadersToHttpRequest(httpRequest, event);
        context.getLogger().log(String.format("[DEBUG] Invoking GetInventory API with: \n HTTP Method: %s \n URI: %s \n",
                httpRequest.getMethod(), httpRequest.getURI()));
        return httpClient.execute(httpRequest);
    }
}
