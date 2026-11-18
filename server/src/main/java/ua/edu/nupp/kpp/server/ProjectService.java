package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ProjectService {
    private static final Logger LOG = Logger.getLogger(ProjectService.class.getName());
    private final ProjectDAO dao = new ProjectDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public List<Project> getAll() throws SQLException {
        return dao.getAll();
    }

    public Project getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Project pr) throws SQLException {
        validate(pr);
        requireStudent(pr.getStudentId());
        dao.add(pr);
        LOG.info("Додано проєкт: " + pr.getCode());
    }

    public void update(Project pr) throws SQLException {
        validate(pr);
        requireStudent(pr.getStudentId());
        dao.update(pr);
        LOG.info("Оновлено проєкт ID=" + pr.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено проєкт ID=" + id);
    }

    static void validate(Project pr) {
        if (pr == null || pr.getStudentId() <= 0 || blank(pr.getCode())
                || blank(pr.getTitle()) || blank(pr.getTechnology())) {
            throw new IllegalArgumentException("Усі основні поля проєкту є обов'язковими");
        }
        if (pr.getTotalRequirements() <= 0 || pr.getTotalChecks() <= 0) {
            throw new IllegalArgumentException("Кількість вимог і перевірок має бути більшою за 0");
        }
    }

    private void requireStudent(long studentId) throws SQLException {
        if (studentDAO.getById(studentId) == null) {
            throw new IllegalArgumentException("Студента з ID=" + studentId + " не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
