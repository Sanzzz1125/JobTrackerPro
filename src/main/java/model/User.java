package model;

public class User {
    private int    id;
    private String name;
    private String email;
    private String password;
    private String role;      // "user" or "admin"

    public User() {}

    public User(int id, String name, String email, String password, String role) {
        this.id = id; this.name = name; this.email = email;
        this.password = password; this.role = role;
    }

    public int    getId()       { return id; }
    public void   setId(int id) { this.id = id; }

    public String getName()           { return name; }
    public void   setName(String n)   { this.name = n; }

    public String getEmail()          { return email; }
    public void   setEmail(String e)  { this.email = e; }

    public String getPassword()       { return password; }
    public void   setPassword(String p){ this.password = p; }

    public String getRole()           { return role; }
    public void   setRole(String r)   { this.role = r; }
}
