package ua.edu.nupp.kpp.server;

public class Review {
    private Long id;
    private long submissionId;
    private String reviewerName;
    private String comment;
    private String reviewDate;

    public Review() {}

    public Review(Long id, long submissionId, String reviewerName, String comment, String reviewDate) {
        this.id = id;
        this.submissionId = submissionId;
        this.reviewerName = reviewerName;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getSubmissionId() { return submissionId; }
    public void setSubmissionId(long submissionId) { this.submissionId = submissionId; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
}
