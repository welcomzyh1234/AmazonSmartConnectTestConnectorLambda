package lambda;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.google.common.collect.ImmutableMap;
import lambda.inventories.GetInventory;
import lambda.inventories.UpdateInventory;
import lambda.prices.UpdatePrice;
import org.apache.http.HttpResponse;

import java.util.Arrays;

public class Handler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private LambdaLogger logger;

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent event, Context context) {
        logger = context.getLogger();

        logger.log(String.format("[DEBUG] event: %s \n", Constant.GSON.toJson(event)));

        try {
            if ("/inventories".equals(event.getPath())) {
                HttpResponse httpResponse = invokeInventoriesAPI(event, context);
                return buildApiGatewayProxyResponse(httpResponse);
            }
            if ("/prices".equals(event.getPath())) {
                HttpResponse httpResponse = new UpdatePrice(Constant.httpClient, event, context).invokeAPI();
                return buildApiGatewayProxyResponse(httpResponse);
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
    private HttpResponse invokeInventoriesAPI(APIGatewayV2ProxyRequestEvent event, Context context) throws Exception {
        if (HttpMethod.GET.equals(HttpMethod.valueOf(event.getHttpMethod()))) {
            return new GetInventory(Constant.httpClient, event, context).invokeAPI();
        }

        if (HttpMethod.PUT.equals(HttpMethod.valueOf(event.getHttpMethod()))) {
            return new UpdateInventory(Constant.httpClient, event, context).invokeAPI();
        }

        throw new IllegalStateException("Wrong HTTP Method, only GET and PUT acceptable!");
    }

    private APIGatewayV2ProxyResponseEvent buildApiGatewayProxyResponse(HttpResponse httpResponse) {
        APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        response.setBody(httpResponse.toString());
        response.setHeaders(
                ImmutableMap.of(
                        "Access-Control-Allow-Origin", "*",
                        "Access-Control-Allow-Credentials", "true")
        );
        logger.log(String.format("[DEBUG] APIGatewayProxyResponse: %s \n", Constant.GSON.toJson(response)));
        return response;
    }
}