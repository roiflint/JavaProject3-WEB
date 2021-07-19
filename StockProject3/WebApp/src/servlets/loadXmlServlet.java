package servlets;

import com.EngineInter;
import com.ErrorDto;
import managers.EngineManager;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@WebServlet(name = "loadXmlServlet", urlPatterns = "/pages/Broker/BrokerHome/loadFile")
@MultipartConfig
public class loadXmlServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("fileupload/form.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
         Part filePart = request.getPart("fileINP"); // Retrieves <input type="file" name="fileINP">
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
        InputStream fileContent = filePart.getInputStream();
        EngineManager engineManager = ServletUtils.getEngineManager(getServletContext());
        EngineInter eng = engineManager.getEng();
        String usernameFromSession = SessionUtils.getUsername(request);
        if (usernameFromSession == null){
            response.sendRedirect("../../../index.html");
        }
        try {
            ErrorDto dto = eng.LoadXML(fileContent,usernameFromSession);
            if (dto.getSuccess()){
                response.setStatus(200);
                response.getOutputStream().println(dto.getMessage());
            }
            else{
                response.setStatus(409);
                response.getOutputStream().println(dto.getMessage());
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getOutputStream().println(e.getMessage());
        }

    }
}
