package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public void add(Student s) throws SQLException {
        String sql = "INSERT INTO student(student_code, full_name, group_name, email) VALUES(?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.executeUpdate();
        }
    }

    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, student_code, full_name, group_name, email FROM student ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Student getById(long id) throws SQLException {
        String sql = "SELECT id, student_code, full_name, group_name, email FROM student WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE student SET student_code=?, full_name=?, group_name=?, email=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.setLong(5, s.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM student WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Student s) throws SQLException {
        p.setString(1, s.getStudentCode());
        p.setString(2, s.getFullName());
        p.setString(3, s.getGroupName());
        p.setString(4, s.getEmail());
    }

    private Student map(ResultSet r) throws SQLException {
        return new Student(
                r.getLong("id"),
                r.getString("student_code"),
                r.getString("full_name"),
                r.getString("group_name"),
                r.getString("email")
        );
    }
}
