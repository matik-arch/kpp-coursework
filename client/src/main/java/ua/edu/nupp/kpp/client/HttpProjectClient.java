package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpProjectClient {
    private static final String BASE = System.getenv("SERVER_URL") != null && !System.getenv("SERVER_URL").isBlank()
            ? System.getenv("SERVER_URL")
            : "http://localhost:8080/student-projects-server";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // --- Students ---
    public List<Student> getStudents() {
        return Arrays.asList(read(get("/students"), Student[].class));
    }

    public void addStudent(Student s) {
        post("/students", "action=add&studentCode=" + enc(s.getStudentCode())
                + "&fullName=" + enc(s.getFullName())
                + "&groupName=" + enc(s.getGroupName())
                + "&email=" + enc(s.getEmail()));
    }

    public void updateStudent(Student s) {
        post("/students", "action=update&id=" + s.getId()
                + "&studentCode=" + enc(s.getStudentCode())
                + "&fullName=" + enc(s.getFullName())
                + "&groupName=" + enc(s.getGroupName())
                + "&email=" + enc(s.getEmail()));
    }

    public void deleteStudent(long id) {
        post("/students", "action=delete&id=" + id);
    }

    // --- Projects ---
    public List<Project> getProjects() {
        return Arrays.asList(read(get("/projects"), Project[].class));
    }

    public void addProject(Project p) {
        post("/projects", projectForm("add", p));
    }

    public void updateProject(Project p) {
        post("/projects", projectForm("update", p) + "&id=" + p.getId());
    }

    public void deleteProject(long id) {
        post("/projects", "action=delete&id=" + id);
    }

    // --- Submissions ---
    public List<Submission> getSubmissions() {
        return Arrays.asList(read(get("/submissions"), Submission[].class));
    }

    public void addSubmission(long projectId, String versionTag, int completedRequirements,
                              int passedChecks, double documentationScore) {
        post("/submissions", "action=add&projectId=" + projectId
                + "&versionTag=" + enc(versionTag)
                + "&completedRequirements=" + completedRequirements
                + "&passedChecks=" + passedChecks
                + "&documentationScore=" + documentationScore);
    }

    public void updateSubmission(long id, long projectId, String versionTag, int completedRequirements,
                                 int passedChecks, double documentationScore) {
        post("/submissions", "action=update&id=" + id
                + "&projectId=" + projectId
                + "&versionTag=" + enc(versionTag)
                + "&completedRequirements=" + completedRequirements
                + "&passedChecks=" + passedChecks
                + "&documentationScore=" + documentationScore);
    }

    public void changeSubmissionState(long id, SubmissionState state) {
        if (state == null) {
            throw new IllegalArgumentException("Виберіть новий стан подання");
        }
        post("/submissions", "action=transition&id=" + id + "&state=" + state.name());
    }

    public void deleteSubmission(long id) {
        post("/submissions", "action=delete&id=" + id);
    }

    // --- Reviews ---
    public List<Review> getReviews() {
        return Arrays.asList(read(get("/reviews"), Review[].class));
    }

    public void addReview(Review r) {
        post("/reviews", reviewForm("add", r));
    }

    public void updateReview(Review r) {
        post("/reviews", reviewForm("update", r) + "&id=" + r.getId());
    }

    public void deleteReview(long id) {
        post("/reviews", "action=delete&id=" + id);
    }

    private String projectForm(String action, Project p) {
        return "action=" + action
                + "&studentId=" + p.getStudentId()
                + "&code=" + enc(p.getCode())
                + "&title=" + enc(p.getTitle())
                + "&technology=" + enc(p.getTechnology())
                + "&totalRequirements=" + p.getTotalRequirements()
                + "&totalChecks=" + p.getTotalChecks();
    }

    private String reviewForm(String action, Review r) {
        return "action=" + action
                + "&submissionId=" + r.getSubmissionId()
                + "&reviewerName=" + enc(r.getReviewerName())
                + "&comment=" + enc(r.getComment())
                + "&reviewDate=" + enc(r.getReviewDate());
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        return send(req);
    }

    private String send(HttpRequest req) {
        try {
            HttpResponse<String> response = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                throw new RuntimeException(response.body());
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Помилка HTTP: " + e.getMessage(), e);
        }
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка обробки JSON", e);
        }
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
