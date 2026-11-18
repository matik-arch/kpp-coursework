package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SubmissionService {
    private static final Logger LOG = Logger.getLogger(SubmissionService.class.getName());
    private final SubmissionDAO dao = new SubmissionDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    public List<Submission> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(long projectId, String versionTag, int completedRequirements,
                    int passedChecks, double documentationScore) throws SQLException {
        Submission prepared = buildCalculated(null, projectId, versionTag, SubmissionState.DRAFT,
                completedRequirements, passedChecks, documentationScore);
        dao.add(prepared);
        LOG.info("Створено подання версії " + versionTag + " для проєкту ID=" + projectId);
    }

    public void update(long id, long projectId, String versionTag, int completedRequirements,
                       int passedChecks, double documentationScore) throws SQLException {
        Submission existing = dao.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Подання з ID=" + id + " не знайдено");
        }
        Submission prepared = buildCalculated(id, projectId, versionTag, existing.getState(),
                completedRequirements, passedChecks, documentationScore);
        dao.update(prepared);
        LOG.info("Оновлено подання ID=" + id);
    }

    public void changeState(long id, SubmissionState nextState) throws SQLException {
        Submission current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Подання з ID=" + id + " не знайдено");
        }
        if (!isAllowedTransition(current.getState(), nextState)) {
            throw new IllegalArgumentException(
                    "Недопустимий перехід стану: " + current.getState() + " -> " + nextState);
        }
        dao.updateState(id, nextState);
        LOG.info("Змінено стан подання ID=" + id + ": " + current.getState() + " -> " + nextState);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено подання ID=" + id);
    }

    public static boolean isAllowedTransition(SubmissionState from, SubmissionState to) {
        return (from == SubmissionState.DRAFT && to == SubmissionState.SUBMITTED)
                || (from == SubmissionState.SUBMITTED && to == SubmissionState.IN_REVIEW)
                || (from == SubmissionState.IN_REVIEW && to == SubmissionState.ACCEPTED);
    }

    private Submission buildCalculated(Long id, long projectId, String versionTag, SubmissionState state,
                                       int completedRequirements, int passedChecks,
                                       double documentationScore) throws SQLException {
        if (versionTag == null || versionTag.isBlank()) {
            throw new IllegalArgumentException("Версія подання є обов'язковою");
        }
        if (completedRequirements < 0 || passedChecks < 0) {
            throw new IllegalArgumentException("Кількість виконаних вимог і пройдених перевірок не може бути від'ємною");
        }
        if (documentationScore < 0.0 || documentationScore > 100.0) {
            throw new IllegalArgumentException("Оцінка якості документації F3 має бути в діапазоні 0..100");
        }
        Project project = projectDAO.getById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Проєкт з ID=" + projectId + " не знайдено");
        }
        double f1 = QualityCalculator.normalizeRatio(completedRequirements, project.getTotalRequirements());
        double f2 = QualityCalculator.normalizeRatio(passedChecks, project.getTotalChecks());
        double f3 = Math.round(documentationScore * 100.0) / 100.0;
        double qualityIndex = QualityCalculator.calculate(f1, f2, f3);
        return new Submission(id, projectId, versionTag.trim(), state,
                completedRequirements, passedChecks, f3, f1, f2, f3, qualityIndex);
    }
}
