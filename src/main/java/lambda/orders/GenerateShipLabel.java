package lambda.orders;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import lambda.Constant;
import lambda.Util;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;

public class GenerateShipLabel extends OrderProcessAPIProxy {

    public GenerateShipLabel(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    @Override
    public APIGatewayV2ProxyResponseEvent invokeAPI() throws IOException, URISyntaxException {
        return decryptResponse(getHttpResponse());
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
