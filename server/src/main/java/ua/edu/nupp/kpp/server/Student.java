package ua.edu.nupp.kpp.server;

public class Student {
    private Long id;
    private String studentCode;
    private String fullName;
    private String groupName;
    private String email;

    public Student() {}

    public Student(Long id, String studentCode, String fullName, String groupName, String email) {
        this.id = id;
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.groupName = groupName;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
