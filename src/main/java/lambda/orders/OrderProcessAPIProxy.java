package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPIProxy;
import org.apache.http.client.HttpClient;

import java.util.Map;

import static lambda.Constant.YAPS_API_ORDERS_RESOURCE_PATH;

public abstract class OrderProcessAPIProxy extends AmazonYojakaAPIProxy {

    public OrderProcessAPIProxy(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected String getPathFromPutMethod(Map<String, String> requestPayload, String apiQualifier) {
        return String.format("%s/%s/%s", YAPS_API_ORDERS_RESOURCE_PATH, requestPayload.get("orderId"), apiQualifier);
    }

    protected String getPathFromGetMethod(APIGatewayV2ProxyRequestEvent event, String apiQualifier) {
        return String.format(
                "%s/%s/%s",
                YAPS_API_ORDERS_RESOURCE_PATH, event.getQueryStringParameters().get("orderId"), apiQualifier
        );
    }
}
