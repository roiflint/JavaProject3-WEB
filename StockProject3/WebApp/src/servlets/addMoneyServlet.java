package servlets;
import com.EngineInter;
import managers.EngineManager;
import managers.UserManager;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "addMoneyServlet", urlPatterns = "/pages/Broker/BrokerHome/addFundsToWallet")
public class addMoneyServlet extends HttpServlet{
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        int currencyFromParameter = Integer.parseInt(request.getParameter("funds"));
        EngineManager engineManager = ServletUtils.getEngineManager(getServletContext());
        EngineInter eng = engineManager.getEng();
        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null){
            response.sendRedirect("../../../index.html");
        }
        else{
            eng.updateWallet(usernameFromSession,currencyFromParameter);
            response.setStatus(200);
            response.getOutputStream().println(Integer.toString(eng.getWallet(usernameFromSession)));
        }

    }

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


