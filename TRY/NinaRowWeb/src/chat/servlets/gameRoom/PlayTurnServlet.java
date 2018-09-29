package chat.servlets.gameRoom;

import Logic.Exceptions.InvalidInputException;
import Logic.Models.GameDescriptionData;
import Logic.Models.PlayTurnParameters;
import MultiGamesLogic.GamesManager;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/playturn"})

public class PlayTurnServlet extends HttpServlet {

    private static final int GENERAL_ERROR = 499;
    private static final int INPUT_ERROR = 498;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String gameName = ServletUtils.getGameNameFromRequest(request);

        if(gameName != null) {
            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Gson gson = new Gson();

            PlayTurnParameters turnParameters = gson.fromJson(requestBody, PlayTurnParameters.class);
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());

            PrintWriter out = null;

            try {
                out = response.getWriter();
                gamesManager.playTurn(gameName, turnParameters);
            } catch (IOException e) {
                response.setStatus(GENERAL_ERROR);
            } catch (InvalidInputException e) {
                response.setStatus(INPUT_ERROR);
                out.println(e.getMessage());
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
