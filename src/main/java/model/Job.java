package model;

public class Job {
    private int    id;
    private int    userId;
    private String company;
    private String role;
    private String status;
    private String notes;
    private String appliedDate;

    public Job() {}

    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public int    getUserId()          { return userId; }
    public void   setUserId(int uid)   { this.userId = uid; }

    public String getCompany()         { return company; }
    public void   setCompany(String c) { this.company = c; }

    public String getRole()            { return role; }
    public void   setRole(String r)    { this.role = r; }

    public String getStatus()          { return status; }
    public void   setStatus(String s)  { this.status = s; }

    public String getNotes()           { return notes; }
    public void   setNotes(String n)   { this.notes = n; }

    public String getAppliedDate()     { return appliedDate; }
    public void   setAppliedDate(String d) { this.appliedDate = d; }
}
