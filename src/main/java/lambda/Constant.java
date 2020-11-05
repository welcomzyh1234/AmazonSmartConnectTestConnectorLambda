package lambda;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lambda.events.CreateEventsSubscription;
import lambda.events.DeleteEventsSubscription;
import lambda.events.GetEventsSubscription;
import lambda.events.UpdateEventsSubscription;
import lambda.inventories.GetInventory;
import lambda.inventories.UpdateInventory;
import lambda.orders.ConfirmOrder;
import lambda.orders.CreatePackages;
import lambda.orders.GenerateInvoice;
import lambda.orders.GenerateShipLabel;
import lambda.orders.GetOrder;
import lambda.orders.ListOrders;
import lambda.orders.RegenerateShipLabel;
import lambda.orders.RejectOrder;
import lambda.orders.RetrieveInvoice;
import lambda.orders.RetrievePickupSlots;
import lambda.orders.RetrieveShipLabel;
import lambda.orders.ShipOrder;
import lambda.orders.UpdatePackages;
import org.apache.http.client.HttpClient;

import java.util.Map;

public class Constant {
    public static final String SELLER_AWS_ACCOUNT_ACCESS_KEY = System.getenv("SELLER_AWS_ACCOUNT_ACCESS_KEY");
    public static final String SELLER_AWS_ACCOUNT_SECRET_KEY = System.getenv("SELLER_AWS_ACCOUNT_SECRET_KEY");
    public static final String YOJAKA_TEST_CONNECTOR_IAM_ROLE = System.getenv("YOJAKA_TEST_CONNECTOR_IAM_ROLE");
    public static final String ROLE_SESSION_NAME = "MyConnector";
    public static final String AWS4_SIGNER_SERVICE_NAME = "execute-api";
    public static final String AU_AWS_REGION = "us-west-2";
    public static final int ONE_HOUR = 60 * 60;
    public static final String YAPS_API_DOMAIN_NAME = "pdkzlh9iq4.execute-api.us-west-2.amazonaws.com";
    public static final String HTTPS = "https";
    public static final String YAPS_API_INVENTORIES_RESOURCE_PATH = "prod/v1/inventories";
    public static final String YAPS_API_ORDERS_RESOURCE_PATH = "prod/v1/orders";
    public static final String YAPS_API_SUBSCRIPTIONS_RESOURCE_PATH = "prod/v1/events/subscriptions";
    public static final String LWA_ACCESS_TOKEN_HEADER_KEY_NAME = "X-Amz-Access-Token";

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final HttpClient httpClient = Util.getHttpClient();

    public static Map<String, AmazonYojakaAPIProxy> GET_ORDERS_API_TO_PROXY_MAP(
            APIGatewayV2ProxyRequestEvent event, Context context) {
        return new ImmutableMap.Builder<String, AmazonYojakaAPIProxy>()
                .put("get-order", new GetOrder(httpClient, event, context))
                .put("list-orders", new ListOrders(httpClient, event, context))
                .put("confirm-order", new ConfirmOrder(httpClient, event, context))
                .put("reject-order", new RejectOrder(httpClient, event, context))
                .put("ship-order", new ShipOrder(httpClient, event, context))
                .put("generate-invoice", new GenerateInvoice(httpClient, event, context))
                .put("retrieve-invoice", new RetrieveInvoice(httpClient, event, context))
                .put("create-packages", new CreatePackages(httpClient, event, context))
                .put("update-packages", new UpdatePackages(httpClient, event, context))
                .put("retrieve-ship-label", new RetrieveShipLabel(httpClient, event, context))
                .put("retrieve-pickup-slots", new RetrievePickupSlots(httpClient, event, context))
                .put("generate-ship-label", new GenerateShipLabel(httpClient, event, context))
                .put("regenerate-ship-label", new RegenerateShipLabel(httpClient, event, context))
                .build();
    }

    public static Map<String, AmazonYojakaAPIProxy> GET_EVENTS_HTTP_METHOD_TO_PROXY_MAP(
            APIGatewayV2ProxyRequestEvent event, Context context) {
        return new ImmutableMap.Builder<String, AmazonYojakaAPIProxy>()
                .put(HttpMethod.GET.name(), new GetEventsSubscription(httpClient, event, context))
                .put(HttpMethod.DELETE.name(), new DeleteEventsSubscription(httpClient, event, context))
                .put(HttpMethod.POST.name(), new CreateEventsSubscription(httpClient, event, context))
                .put(HttpMethod.PUT.name(), new UpdateEventsSubscription(httpClient, event, context))
                .build();
    }

    public static Map<String, AmazonYojakaAPIProxy> GET_INVENTORIES_HTTP_METHOD_TO_PROXY_MAP(
            APIGatewayV2ProxyRequestEvent event, Context context) {
        return new ImmutableMap.Builder<String, AmazonYojakaAPIProxy>()
                .put(HttpMethod.GET.name(), new GetInventory(httpClient, event, context))
                .put(HttpMethod.PUT.name(), new UpdateInventory(httpClient, event, context))
                .build();
    }
}
