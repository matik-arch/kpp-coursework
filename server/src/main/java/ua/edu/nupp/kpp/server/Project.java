package ua.edu.nupp.kpp.server;

public class Project {
    private Long id;
    private long studentId;
    private String code;
    private String title;
    private String technology;
    private int totalRequirements;
    private int totalChecks;

    public Project() {}

    public Project(Long id, long studentId, String code, String title,
                   String technology, int totalRequirements, int totalChecks) {
        this.id = id;
        this.studentId = studentId;
        this.code = code;
        this.title = title;
        this.technology = technology;
        this.totalRequirements = totalRequirements;
        this.totalChecks = totalChecks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public int getTotalRequirements() { return totalRequirements; }
    public void setTotalRequirements(int totalRequirements) { this.totalRequirements = totalRequirements; }

    public int getTotalChecks() { return totalChecks; }
    public void setTotalChecks(int totalChecks) { this.totalChecks = totalChecks; }
}
