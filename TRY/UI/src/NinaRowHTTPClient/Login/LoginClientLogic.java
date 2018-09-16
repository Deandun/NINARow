package NinaRowHTTPClient.Login;

import Logic.Models.Player;
import NinaRowHTTPClient.GeneralCommunication.AddressBuilder;
import NinaRowHTTPClient.GeneralCommunication.CommunicationHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// This class is in charge of the login logic. It does not use a designated communications class and tasks because it
// Only has 1 task to fulfill.
public class LoginClientLogic {
    public static final String USERNAME_PARAMETER = "username";
    public static final int LOGIN_ERROR_USER_ALREADY_EXIST = 499;

    private ILoginClientLogicDelegate mDelegate;
    private Gson mGson = new GsonBuilder().create();
    private AddressBuilder mAddressBuilder;
    private CommunicationHandler mCommunicationHandler;
    private String mPlayerName;


    public LoginClientLogic(ILoginClientLogicDelegate mDelegate) {
        this.mDelegate = mDelegate;
        this.mAddressBuilder = new AddressBuilder();
        this.mCommunicationHandler = new CommunicationHandler();
    }

    // Receiving JSON:
//    {
//        mTurnCounter: int
//        mName: str
//        mType: enum
//    }
    public void performLogin(String name) {
        this.mPlayerName = name;
        this.mCommunicationHandler.setmAddress(this.getLoginURIString(name));
        this.mCommunicationHandler.doGet(this::handleResponseSuccess, this::handleResponseFailure);
    }

    private String getLoginURIString(String playerName) {
        this.mAddressBuilder.setmScheme("http");
        this.mAddressBuilder.setmHost("localhost:8080");
        this.mAddressBuilder.setmPath("/NinaRowWeb/pages/signup/login");
        this.mAddressBuilder.addParameter(USERNAME_PARAMETER, playerName);

        return this.mAddressBuilder.build();
    }

    private void handleResponseSuccess(String responseString) {
        System.out.println(responseString);

        Player loggedInPlayer = this.mGson.fromJson(responseString, Player.class);
        this.mDelegate.onLoginSuccess(loggedInPlayer);
    }

    private void handleResponseFailure(Integer errorCode) {
        this.mDelegate.onLoginError("The name " + this.mPlayerName + " is already used by another player.");
    }
}
