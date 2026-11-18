package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ReviewService {
    private static final Logger LOG = Logger.getLogger(ReviewService.class.getName());
    private final ReviewDAO dao = new ReviewDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    public List<Review> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(Review rev) throws SQLException {
        validate(rev);
        requireSubmission(rev.getSubmissionId());
        dao.add(rev);
        LOG.info("Додано рецензію для подання ID=" + rev.getSubmissionId());
    }

    public void update(Review rev) throws SQLException {
        validate(rev);
        requireSubmission(rev.getSubmissionId());
        dao.update(rev);
        LOG.info("Оновлено рецензію ID=" + rev.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено рецензію ID=" + id);
    }

    static void validate(Review rev) {
        if (rev == null || rev.getSubmissionId() <= 0 || blank(rev.getReviewerName())
                || blank(rev.getComment()) || blank(rev.getReviewDate())) {
            throw new IllegalArgumentException("Усі поля рецензії є обов'язковими");
        }
    }

    private void requireSubmission(long submissionId) throws SQLException {
        if (submissionDAO.getById(submissionId) == null) {
            throw new IllegalArgumentException("Подання з ID=" + submissionId + " не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
