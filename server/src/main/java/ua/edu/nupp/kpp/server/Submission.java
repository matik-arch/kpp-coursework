package ua.edu.nupp.kpp.server;

public class Submission {
    private Long id;
    private long projectId;
    private String versionTag;
    private SubmissionState state;
    private int completedRequirements;
    private int passedChecks;
    private double documentationScore;
    private double f1;
    private double f2;
    private double f3;
    private double qualityIndex;

    public Submission() {}

    public Submission(Long id, long projectId, String versionTag, SubmissionState state,
                      int completedRequirements, int passedChecks, double documentationScore,
                      double f1, double f2, double f3, double qualityIndex) {
        this.id = id;
        this.projectId = projectId;
        this.versionTag = versionTag;
        this.state = state;
        this.completedRequirements = completedRequirements;
        this.passedChecks = passedChecks;
        this.documentationScore = documentationScore;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.qualityIndex = qualityIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getProjectId() { return projectId; }
    public void setProjectId(long projectId) { this.projectId = projectId; }

    public String getVersionTag() { return versionTag; }
    public void setVersionTag(String versionTag) { this.versionTag = versionTag; }

    public SubmissionState getState() { return state; }
    public void setState(SubmissionState state) { this.state = state; }

    public int getCompletedRequirements() { return completedRequirements; }
    public void setCompletedRequirements(int completedRequirements) { this.completedRequirements = completedRequirements; }

    public int getPassedChecks() { return passedChecks; }
    public void setPassedChecks(int passedChecks) { this.passedChecks = passedChecks; }

    public double getDocumentationScore() { return documentationScore; }
    public void setDocumentationScore(double documentationScore) { this.documentationScore = documentationScore; }

    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }

    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }

    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }

    public double getQualityIndex() { return qualityIndex; }
    public void setQualityIndex(double qualityIndex) { this.qualityIndex = qualityIndex; }
}
