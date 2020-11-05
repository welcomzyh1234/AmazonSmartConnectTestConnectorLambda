package lambda.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import lambda.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;

public class RetrieveInvoice extends OrderProcessAPIProxy {

    public RetrieveInvoice(HttpClient httpClient, APIGatewayV2ProxyRequestEvent event, Context context) {
        super(httpClient, event, context);
    }

    protected HttpUriRequest getHttpRequest() throws URISyntaxException {
        return new HttpGet(
                Util.getBaseUriBuilder()
                        .setPath(getPathFromGetMethod(event, "invoice"))
                        .build()
        );
    }
}
