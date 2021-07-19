package servlets;

import com.EngineInter;
import com.Stock;
import managers.EngineManager;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "issueStockServlet", urlPatterns = "/pages/Broker/BrokerHome/issueStock")
public class issueStockServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String symbolFromParameter = request.getParameter("symbol");
        String companyNameFromParameter = request.getParameter("companyName");
        int quantityFromParameter = Integer.parseInt(request.getParameter("quantity"));
        int priceFromParameter = Integer.parseInt(request.getParameter("price"));
        EngineManager engineManager = ServletUtils.getEngineManager(getServletContext());
        EngineInter eng = engineManager.getEng();
        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null){
            response.sendRedirect("../../../index.html");
        }
        else{
            if(eng.createNewStock(usernameFromSession,symbolFromParameter,companyNameFromParameter,quantityFromParameter,priceFromParameter)){
                response.setStatus(200);
            }
            else{
                response.setStatus(409);
            }


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

