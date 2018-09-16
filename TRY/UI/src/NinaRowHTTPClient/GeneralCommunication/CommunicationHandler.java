package NinaRowHTTPClient.GeneralCommunication;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.function.Consumer;

// General communication class - Send simple Get request. Return response body or response error code.
public class CommunicationHandler {
    private static final int MINIMUM_SUCCESS_CODE = 200;
    private static final int MAXIMUM_SUCCESS_CODE = 300;

    private String mAddress;

    public void doGet(Consumer<String> onSuccess, Consumer<Integer> onFailure) {
        // Perform http request in a different thread.
        new Thread(() -> {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(this.mAddress);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                this.handleResponse(response, onSuccess, onFailure);
            } catch (IOException exception) {
                System.out.println("Exception with message: " + exception.getMessage());
            }
        }).start();
    }

    private void handleResponse(CloseableHttpResponse response, Consumer<String> onSuccess, Consumer<Integer> onFailure) throws IOException {
        System.out.println(response.toString());

        if(response.getStatusLine().getStatusCode() >= MINIMUM_SUCCESS_CODE && response.getStatusLine().getStatusCode() <= MAXIMUM_SUCCESS_CODE) {
            String responseBody = EntityUtils.toString(response.getEntity());
            onSuccess.accept(responseBody);
        } else {
            onFailure.accept(response.getStatusLine().getStatusCode());
        }
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
