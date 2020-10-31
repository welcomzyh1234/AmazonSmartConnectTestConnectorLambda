package lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.net.URISyntaxException;

public interface AmazonYojakaAPI {

    HttpResponse invokeAPI() throws IOException, URISyntaxException;

}
