package servlets;

import com.EngineInter;
import com.google.gson.Gson;
import managers.EngineManager;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "sellTransactionsServlet", urlPatterns = "/sellTransactionsList")
public class sellTransactionsServlet extends HttpServlet {
        protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            //returning JSON objects, not HTML
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                String usernameFromSession = SessionUtils.getUsername(request);
                Gson gson = new Gson();
                String symbol = request.getParameter("stock");
                EngineManager engineManager = ServletUtils.getEngineManager(getServletContext());
                EngineInter eng = engineManager.getEng();
                if (eng.IsStockExist(symbol)) {
                    response.setStatus(200);
                    String json = gson.toJson(eng.GetSellList(symbol));
                    out.println(json);
                    out.flush();
                } else {
                    response.setStatus(404);
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
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request, response);
        }

        /*
         * Handles the HTTP <code>POST</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

