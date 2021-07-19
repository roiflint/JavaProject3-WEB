package managers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private Set<ActiveUser> users;

    public UserManager(){
        this.users = new HashSet<>();
    }

    public synchronized void addUser(String username, String role) { this.users.add(new ActiveUser(username,role)); }

    public synchronized void removeUser(String username) {
        this.users.remove(username);
    }

    public Set<ActiveUser> getUsers() {
        return this.users;
    }

    public boolean isUserExists(String username) {
        for (ActiveUser u:this.users) {
            if (u.getUserName().equalsIgnoreCase(username)){
                return true;
            }
        }
        return false;
    }
}
