package servlets.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("username") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static boolean isAdmin(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("admin") : null;
        if (sessionAttribute != null){
            if (sessionAttribute.toString().equals("on"))
                return true;
        }
        return false;
    }
    
    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}