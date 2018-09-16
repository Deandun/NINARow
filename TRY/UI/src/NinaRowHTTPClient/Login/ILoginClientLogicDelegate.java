package NinaRowHTTPClient.Login;

import Logic.Models.Player;

public interface ILoginClientLogicDelegate {
    void onLoginSuccess(Player player);

    void onLoginError(String errorMessage);
}
