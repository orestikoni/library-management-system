package library;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 4696254069465519074L;
    
    private String name;
    private String surname;
    private String phone;
    private String username;
    private String password;
    private boolean suspended;  // Field for account suspension

    public User(String name, String surname, String phone, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.suspended = false;  // by default, the user is active
    }

    // Getters
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhone() { return phone; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isSuspended() { return suspended; }
    
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    // Setter for suspended
    public void setSuspended(boolean suspended) { this.suspended = suspended; }
}

