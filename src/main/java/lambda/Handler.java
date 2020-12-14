package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.google.common.collect.ImmutableMap;
import lambda.prices.UpdatePrice;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static lambda.Constant.GET_EVENTS_HTTP_METHOD_TO_PROXY_MAP;
import static lambda.Constant.GET_INVENTORIES_HTTP_METHOD_TO_PROXY_MAP;
import static lambda.Constant.GET_ORDERS_API_TO_PROXY_MAP;

public class Handler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private LambdaLogger logger;

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent event, Context context) {
        logger = context.getLogger();

        logger.log(String.format("[DEBUG] event: %s \n", Constant.GSON.toJson(event)));

        try {
            if ("/inventories".equals(event.getPath())) {
                return invokeInventoriesAPI(event, context);
            }
            if ("/prices".equals(event.getPath())) {
                return new UpdatePrice(Constant.httpClient, event, context).invokeAPI();
            }
            if ("/events/subscriptions".equals(event.getPath())) {
                return invokeEventsAPI(event, context);
            }
            if ("orders".equals(event.getPath().split("/")[1])) {
                String api = event.getPath().split("/")[2];
                return invokeOrdersAPI(event, context, api);
            }
            throw new IllegalStateException(
                    "Wrong Invoke URL Path, only inventories, events, prices and orders acceptable!");
        } catch (Exception e) {
            logger.log("[ERROR]: Exception occurred, refer details: \n"
                    + e.getMessage() + "\n"
                    + e.getCause() + "\n"
                    + Arrays.toString(e.getStackTrace()) + "\n"
            );
        }

        return null;
    }

    /**
     * Send Request to restful API of Amaozn Yojaka on inventories
     *
     * @see <a href="https://docs.smartconnect.amazon.com/swagger/inventories.html#api-InventoryManagement-updateInventory">Amazon Yojaka API Inventories Doc</a>
     */
    private APIGatewayV2ProxyResponseEvent invokeInventoriesAPI(APIGatewayV2ProxyRequestEvent event, Context context) throws Exception {
        Map<String, AmazonYojakaAPIProxy> inventoriesHttpMethodToProxyMap =
                GET_INVENTORIES_HTTP_METHOD_TO_PROXY_MAP(event, context);
        String httpMethod = event.getHttpMethod();

        if (inventoriesHttpMethodToProxyMap.containsKey(httpMethod)) {
            return inventoriesHttpMethodToProxyMap.get(httpMethod).invokeAPI();
        }

        throw new IllegalStateException("Wrong HTTP Method, only GET and PUT acceptable!");
    }

    /**
     * Send Request to restful API of Amaozn Yojaka on orders
     *
     * @see <a href="https://docs.smartconnect.amazon.com/swagger/orders.html#api-_">Amazon Yojaka API Orders Doc</a>
     */
    private APIGatewayV2ProxyResponseEvent invokeOrdersAPI(APIGatewayV2ProxyRequestEvent event, Context context, String api) throws Exception {
        Map<String, AmazonYojakaAPIProxy> ordersApiToProxyMap = GET_ORDERS_API_TO_PROXY_MAP(event, context);

        if (ordersApiToProxyMap.containsKey(api)) {
            return ordersApiToProxyMap.get(api).invokeAPI();
        }

        throw new IllegalStateException("Wrong API name, check https://docs.smartconnect.amazon.com/swagger/orders.html#api-_!");
    }

    /**
     * Send Request to restful API of Amaozn Yojaka on events
     *
     * @see <a href="https://docs.smartconnect.amazon.com/swagger/events.html#api-_">Amazon Yojaka API Events Doc</a>
     */
    private APIGatewayV2ProxyResponseEvent invokeEventsAPI(APIGatewayV2ProxyRequestEvent event, Context context) throws Exception {
        Map<String, AmazonYojakaAPIProxy> eventsHttpMethodToProxyMap = GET_EVENTS_HTTP_METHOD_TO_PROXY_MAP(event,
                context);
        String httpMethod = event.getHttpMethod();

        if (eventsHttpMethodToProxyMap.containsKey(httpMethod)) {
            return eventsHttpMethodToProxyMap.get(httpMethod).invokeAPI();
        }

        throw new IllegalStateException("Wrong HTTP Method, only GET, PUT, POST and DELETE are acceptable!");
    }
}