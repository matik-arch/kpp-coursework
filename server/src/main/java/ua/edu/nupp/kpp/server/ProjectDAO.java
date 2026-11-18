package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    public void add(Project pr) throws SQLException {
        String sql = "INSERT INTO project(student_id, code, title, technology, total_requirements, total_checks) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, pr);
            p.executeUpdate();
        }
    }

    public List<Project> getAll() throws SQLException {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT id, student_id, code, title, technology, total_requirements, total_checks FROM project ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Project getById(long id) throws SQLException {
        String sql = "SELECT id, student_id, code, title, technology, total_requirements, total_checks FROM project WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Project pr) throws SQLException {
        String sql = "UPDATE project SET student_id=?, code=?, title=?, technology=?, total_requirements=?, total_checks=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, pr);
            p.setLong(7, pr.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM project WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Project pr) throws SQLException {
        p.setLong(1, pr.getStudentId());
        p.setString(2, pr.getCode());
        p.setString(3, pr.getTitle());
        p.setString(4, pr.getTechnology());
        p.setInt(5, pr.getTotalRequirements());
        p.setInt(6, pr.getTotalChecks());
    }

    private Project map(ResultSet r) throws SQLException {
        return new Project(
                r.getLong("id"),
                r.getLong("student_id"),
                r.getString("code"),
                r.getString("title"),
                r.getString("technology"),
                r.getInt("total_requirements"),
                r.getInt("total_checks")
        );
    }
}
