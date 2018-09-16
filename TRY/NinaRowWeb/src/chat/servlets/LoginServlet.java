package chat.servlets;

import ChatLogicEngine.users.*;
import Logic.Enums.ePlayerType;
import Logic.Models.Player;
import chat.constants.Constants;
import chat.utils.SessionUtils;
import chat.utils.ServletUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static chat.constants.Constants.USERNAME;

@WebServlet(urlPatterns = {"/pages/signup/login"})
public class LoginServlet extends HttpServlet {

    // urls that starts with forward slash '/' are considered absolute
    // urls that doesn't start with forward slash '/' are considered relative to the place where this servlet request comes from
    // you can use absolute paths, but then you need to build them from scratch, starting from the context path
    // ( can be fetched from request.getContextPath() ) and then the 'absolute' path from it.
    // Each method with it's pros and cons...
    private final String CHAT_ROOM_URL = "../chatroom/chatroom.html";
    private final String SIGN_UP_URL = "../signup/singup.html";
    private final String LOGIN_ERROR_URL = "/pages/loginerror/login_attempt_after_error.jsp";  // must start with '/' since will be used in request dispatcher...
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8"); //TODO: figure out if returning JSON (and relying on JS) or html.
        response.setContentType("application/json");
        System.out.println("Entered login servlet.");

        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null) {
                //no username in session and no username in parameter -
                //redirect back to the index page
                //this return an HTTP code back to the browser telling it to load
                response.sendRedirect(SIGN_UP_URL);
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        // username already exists, forward the request back to index.jsp
                        // with a parameter that indicates that an error should be displayed
                        // the request dispatcher obtained from the servlet context is one that MUST get an absolute path (starting with'/')
                        // and is relative to the web app root

                        //request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                        //getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
                        response.setStatus(499);
                    } else {
                        userManager.addUser(usernameFromParameter);
                        Player logingPlayer = new Player();
                        logingPlayer.setTurnCounter(0);
                        logingPlayer.setType(ePlayerType.Human);
                        logingPlayer.setName(usernameFromParameter);

                        Gson gson = new Gson();
                        String playerJson = gson.toJson(logingPlayer);
                        System.out.println(playerJson);
                        try (PrintWriter out = response.getWriter()) {
                            out.print(playerJson);
                            out.flush();
                        }
                        //TODO: figure out if returning JSON (and relying on JS) or html.
//                        //add the new user to the users list
//                        userManager.addUser(usernameFromParameter);
//                        //set the username in a session so it will be available on each request
//                        //the true parameter means that if a session object does not exists yet
//                        //create a new one
//                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
//
//                        //redirect the request to the chat room - in order to actually change the URL
//                        System.out.println("On login, request URI is: " + request.getRequestURI());
//                        response.sendRedirect(CHAT_ROOM_URL);
                    }
                }
            }
        } else {
            //user is already logged in
            response.sendRedirect(CHAT_ROOM_URL);
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
