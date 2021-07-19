package servlets;

import com.EngineInter;
import com.Transaction;
import com.TransactionDTO;
import managers.EngineManager;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "makeTransactionServlet", urlPatterns = "/makeTransaction")
public class makeTransactionServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String symbolFromParameter = request.getParameter("symbol");
        int quantityFromParameter = Integer.parseInt(request.getParameter("quantity"));
        String bsFromParameter = request.getParameter("bs");
        String actionFromParameter = request.getParameter("action");
        EngineManager engineManager = ServletUtils.getEngineManager(getServletContext());
        EngineInter eng = engineManager.getEng();
        String usernameFromSession = SessionUtils.getUsername(request);
        int priceFromParameter;
        if(actionFromParameter.equalsIgnoreCase("mkt")) {
            priceFromParameter = 1;
        }
        else{
            priceFromParameter = Integer.parseInt(request.getParameter("price"));
        }


        if (usernameFromSession == null){
            response.setStatus(404);
            response.sendRedirect("../../../index.html");
        }
        if (!eng.IsStockExist(symbolFromParameter)){
            response.setStatus(404);
            response.sendRedirect("../BrokerHome/BrokerHome.html");
        }
        String companyName = eng.getCompanyNameBySymbol(symbolFromParameter);
        if (bsFromParameter.equalsIgnoreCase("buy")){
            List<TransactionDTO> dto = eng.MakeTransaction(new Transaction(quantityFromParameter,priceFromParameter,symbolFromParameter,actionFromParameter,usernameFromSession,"",companyName),true);
            response.setStatus(200);
            response.getOutputStream().println(dto.get(0).getMessage());
        }
        else{
            response.setStatus(200);
            List<TransactionDTO> dto = eng.MakeTransaction(new Transaction(quantityFromParameter,priceFromParameter,symbolFromParameter,actionFromParameter,"",usernameFromSession,companyName),false);
            response.getOutputStream().println(dto.get(0).getMessage());
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


