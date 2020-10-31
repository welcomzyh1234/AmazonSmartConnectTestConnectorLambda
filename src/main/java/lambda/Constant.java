package lambda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.HttpClient;

public class Constant {
    public static final String SELLER_AWS_ACCOUNT_ACCESS_KEY = "AKIAUS7VSIIQWNCEMWPZ";
    public static final String SELLER_AWS_ACCOUNT_SECRET_KEY = "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";
    public static final String ROLSE_SESSION_NAME = "MyConnector";
    public static final String AWS4_SIGNER_SERVICE_NAME = "execute-api";
    public static final String AU_AWS_REGION = "us-west-2";
    public static final String YOJAKA_TEST_CONNECTOR_IAM_ROLE = "arn:aws:iam::244389800418:role/TestConnectorRole";
    public static final int ONE_HOUR = 60 * 60;
    public static final String AU_YAPS_API_DOMAIN_NAME = "pdkzlh9iq4.execute-api.us-west-2.amazonaws.com";
    public static final String HTTPS = "https";
    public static final String AU_YAPS_API_INVENTORIES_RESOURCE_PATH = "prod/v1/inventories";
    public static final String LWA_ACCESS_TOKEN_HEADER_KEY_NAME = "X-Amz-Access-Token";

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final HttpClient httpClient = Util.getHttpClient();
}
