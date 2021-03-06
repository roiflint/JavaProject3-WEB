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

@WebServlet(name = "updateWalletServlet", urlPatterns = "/updateWallet")
public class updateWalletServlet extends HttpServlet{
        protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            response.setContentType("text/plain;charset=UTF-8");
            EngineManager engineManager = ServletUtils.getEngineManager(getServletContext());
            EngineInter eng = engineManager.getEng();
            String usernameFromSession = SessionUtils.getUsername(request);
            if (usernameFromSession == null){
                response.sendRedirect("../../../index.html");
            }
            else{
                response.setStatus(200);
                System.out.println((eng.getWallet(usernameFromSession)));
                response.getOutputStream().println(Integer.toString(eng.getWallet(usernameFromSession)));
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
