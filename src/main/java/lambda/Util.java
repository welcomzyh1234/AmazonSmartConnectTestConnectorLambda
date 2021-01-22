package lambda;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;

import java.util.Base64;
import java.util.Map;

import static lambda.Constant.LWA_ACCESS_TOKEN_HEADER_KEY_NAME;

public class Util {

    public static URIBuilder getBaseUriBuilder() {
        return new URIBuilder()
                .setScheme(Constant.HTTPS)
                .setHost(Constant.YAPS_API_DOMAIN_NAME);
    }

    public static void addLWAAccessTokenToHttpRequest(HttpUriRequest httpRequest, APIGatewayV2ProxyRequestEvent event) {
        // The headers names sent by Jquery AJAX would be automatically updated to lower case, so we need retrieve
        // the value with lower case key name
        String lwaAccessToken = event.getHeaders().get(LWA_ACCESS_TOKEN_HEADER_KEY_NAME.toLowerCase());
        httpRequest.addHeader("Accept", "application/json");
        httpRequest.addHeader(Constant.LWA_ACCESS_TOKEN_HEADER_KEY_NAME, lwaAccessToken);
    }

    /**
     * Get Http Client which can Assume Yojaka Test Connector Role and can generate AWS4Signer signature
     */
    public static HttpClient getHttpClient() {
        AWS4Signer aws4Signer = new AWS4Signer();
        aws4Signer.setServiceName(Constant.AWS4_SIGNER_SERVICE_NAME);
        aws4Signer.setRegionName(Constant.AU_AWS_REGION);
        AWSSecurityTokenService awsSecurityTokenService = AWSSecurityTokenServiceClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        Constant.SELLER_AWS_ACCOUNT_ACCESS_KEY, Constant.SELLER_AWS_ACCOUNT_SECRET_KEY)
                        )
                )
                .build();
        STSAssumeRoleSessionCredentialsProvider stsAssumeRoleSessionCredentialsProvider =
                new STSAssumeRoleSessionCredentialsProvider.Builder(Constant.YOJAKA_TEST_CONNECTOR_IAM_ROLE, Constant.ROLE_SESSION_NAME)
                        .withStsClient(awsSecurityTokenService)
                        .withRoleSessionDurationSeconds(Constant.ONE_HOUR)
                        .build();
        AWSRequestSigningApacheInterceptor awsRequestSigningApacheInterceptor =
                new AWSRequestSigningApacheInterceptor(Constant.AWS4_SIGNER_SERVICE_NAME, aws4Signer,
                        stsAssumeRoleSessionCredentialsProvider);
        return HttpClients.custom()
                .addInterceptorLast(awsRequestSigningApacheInterceptor)
                .build();
    }

    public static String decryptYojakaEncryptedData(String encryptedValue, String encryptionInfoContext) {
        AwsCrypto awsCrypto = new AwsCrypto();
        KmsMasterKeyProvider kmsMasterKeyProvider =
                KmsMasterKeyProvider.builder()
                        .withCredentials(
                                new AWSStaticCredentialsProvider(
                                        new BasicAWSCredentials(
                                                Constant.SELLER_AWS_ACCOUNT_ACCESS_KEY,
                                                Constant.SELLER_AWS_ACCOUNT_SECRET_KEY
                                        )
                                )
                        )
                        .withKeysForEncryption(Constant.YOJAKA_KMS_KEY)
                        .build();

        CryptoResult<byte[], KmsMasterKey> decryptedDataEnvelope =
                awsCrypto.decryptData(kmsMasterKeyProvider, Base64.getDecoder().decode(encryptedValue));

        Map<String, String> encryptionContextMap = decryptedDataEnvelope.getEncryptionContext();
        Validate.isTrue(encryptionContextMap.get("client").equals("yojaka"));
        Validate.isTrue(encryptionContextMap.get("dataType").equals(encryptionInfoContext));

        return new String(decryptedDataEnvelope.getResult());
    }

    public static <T> T getNestedValueFromMap(Map map, String... keys) {
        Object value = map;

        for (String key : keys) {
            value = ((Map) value).get(key);
        }

        return (T) value;
    }
}
