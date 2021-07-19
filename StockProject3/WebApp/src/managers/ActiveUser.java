package managers;

public class ActiveUser {
    private final String username;
    private final String role;

    public ActiveUser(String username, String role){
        this.username = username;
        this.role = role;
    }

    public String getUserName() { return username; }

    public String getRole() { return role; }
}
