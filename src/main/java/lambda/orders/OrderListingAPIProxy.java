package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.AmazonYojakaAPIProxy;
import org.apache.http.client.HttpClient;

import static lambda.Constant.YAPS_API_ORDERS_RESOURCE_PATH;

public abstract class OrderListingAPIProxy extends AmazonYojakaAPIProxy {

    public OrderListingAPIProxy(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected String getPath(APIGatewayV2ProxyRequestEvent event) {
        return YAPS_API_ORDERS_RESOURCE_PATH
                + "/"
                + event.getQueryStringParameters().getOrDefault("orderId", "");
    }
}
