package servlets;
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

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet{
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        EngineManager engineManager = ServletUtils.getEngineManager(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet

            String usernameFromParameter = request.getParameter("username");
            String check = request.getParameter("admin");
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                //no username in session and no username in parameter - not standard situation. it's a conflict

                // stands for conflict in server state
                response.setStatus(409);

                // returns answer to the browser to go back to the sign up URL page
                response.getOutputStream().println("Enter Username");
            }
            else {
                //normalize the username value
                String name = usernameFromParameter;
                String role = "Broker";
                usernameFromParameter = usernameFromParameter.trim();
                boolean adminFromParameter = false;
                if (check != null && check.equalsIgnoreCase("on")){
                    adminFromParameter = true;
                    role = "Admin";
                }
                /*
                One can ask why not enclose all the synchronizations inside the userManager object ?
                Well, the atomic action we need to perform here includes both the question (isUserExists) and (potentially) the insertion
                of a new user (addUser). These two actions needs to be considered atomic, and synchronizing only each one of them, solely, is not enough.
                (of course there are other more sophisticated and performable means for that (atomic objects etc) but these are not in our scope)

                The synchronized is on this instance (the servlet).
                As the servlet is singleton - it is promised that all threads will be synchronized on the very same instance (crucial here)

                A better code would be to perform only as little and as necessary things we need here inside the synchronized block and avoid
                do here other not related actions (such as request dispatcher\redirection etc. this is shown here in that manner just to stress this issue
                 */
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(401);
                        response.getOutputStream().println(errorMessage);
                    }
                    else {
                        //add the new user to the users list
                        userManager.addUser(usernameFromParameter,role);
                        engineManager.getEng().addUser(name,adminFromParameter);
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute("username", usernameFromParameter);
                        request.getSession().setAttribute("admin",check);    //'on' for admin
                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.setStatus(200);
                        if(adminFromParameter){
                            response.getOutputStream().println("pages/Admin/AdminHome/AdminHome.html");
                            //response.sendRedirect("pages/Admin/AdminHome/AdminHome.html");
                        }
                        else{
                            response.getOutputStream().println("pages/Broker/BrokerHome/BrokerHome.html");
                            //response.sendRedirect("pages/Broker/BrokerHome/BrokerHome.html");

                        }

                    }
                }
            }
        } else {
            //user is already logged in
            response.setStatus(200);
            boolean adminFromSession = SessionUtils.isAdmin(request);
            if (adminFromSession){
                response.getOutputStream().println("pages/Admin/AdminHome/AdminHome.html");
                //response.sendRedirect("pages/Admin/AdminHome/AdminHome.html");
            }
            else{
                response.getOutputStream().println("pages/Broker/BrokerHome/BrokerHome.html");
                //response.sendRedirect("pages/Broker/BrokerHome/BrokerHome.html");
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


