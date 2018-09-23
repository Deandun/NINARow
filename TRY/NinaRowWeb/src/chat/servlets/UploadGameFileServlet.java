package chat.servlets;

import Logic.Exceptions.InvalidFileInputException;
import MultiGamesLogic.GamesManager;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Collection;
import java.util.Scanner;


@WebServlet(urlPatterns = {"/upload"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadGameFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO: get uploader name from session.
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Collection<Part> parts = request.getParts();
        out.println("Total parts : " + parts.size() + " ");

        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            //to write the content of the file to a string
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        InputStream fileContentStream = new ByteArrayInputStream( fileContent.toString().getBytes("UTF-8"));

        //TODO: if an exception is thrown - send error with response (and error message).
        this.initNewGameFromFileContent(fileContentStream);

    }

    private void initNewGameFromFileContent(InputStream fileContentStream) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        try {
            gamesManager.addGame(fileContentStream, SessionUtils.getUsername(request));
            //TODO: take care of exceptions.
        } catch (InterruptedException e) {
			out.append(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (InvalidFileInputException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.append(e.getMessage());
            e.printStackTrace();
        }  catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
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
