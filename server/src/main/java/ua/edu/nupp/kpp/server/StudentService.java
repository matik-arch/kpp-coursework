package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class StudentService {
    private static final Logger LOG = Logger.getLogger(StudentService.class.getName());
    private final StudentDAO dao = new StudentDAO();

    public List<Student> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(Student s) throws SQLException {
        validate(s);
        dao.add(s);
        LOG.info("Додано студента: " + s.getStudentCode());
    }

    public void update(Student s) throws SQLException {
        validate(s);
        dao.update(s);
        LOG.info("Оновлено студента ID=" + s.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено студента ID=" + id);
    }

    static void validate(Student s) {
        if (s == null || blank(s.getStudentCode()) || blank(s.getFullName())
                || blank(s.getGroupName()) || blank(s.getEmail())) {
            throw new IllegalArgumentException("Усі поля студента є обов'язковими");
        }
        if (!s.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некоректний формат електронної пошти студента");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
