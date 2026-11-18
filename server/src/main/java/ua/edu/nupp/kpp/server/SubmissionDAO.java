package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {
    public void add(Submission s) throws SQLException {
        String sql = "INSERT INTO submission(project_id, version_tag, state, completed_requirements, passed_checks, "
                + "documentation_score, f1, f2, f3, quality_index) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.executeUpdate();
        }
    }

    public List<Submission> getAll() throws SQLException {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT * FROM submission ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Submission getById(long id) throws SQLException {
        String sql = "SELECT * FROM submission WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Submission s) throws SQLException {
        String sql = "UPDATE submission SET project_id=?, version_tag=?, state=?, completed_requirements=?, "
                + "passed_checks=?, documentation_score=?, f1=?, f2=?, f3=?, quality_index=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.setLong(11, s.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, SubmissionState state) throws SQLException {
        String sql = "UPDATE submission SET state=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM submission WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Submission s) throws SQLException {
        p.setLong(1, s.getProjectId());
        p.setString(2, s.getVersionTag());
        p.setString(3, s.getState().name());
        p.setInt(4, s.getCompletedRequirements());
        p.setInt(5, s.getPassedChecks());
        p.setDouble(6, s.getDocumentationScore());
        p.setDouble(7, s.getF1());
        p.setDouble(8, s.getF2());
        p.setDouble(9, s.getF3());
        p.setDouble(10, s.getQualityIndex());
    }

    private Submission map(ResultSet r) throws SQLException {
        return new Submission(
                r.getLong("id"),
                r.getLong("project_id"),
                r.getString("version_tag"),
                SubmissionState.valueOf(r.getString("state")),
                r.getInt("completed_requirements"),
                r.getInt("passed_checks"),
                r.getDouble("documentation_score"),
                r.getDouble("f1"),
                r.getDouble("f2"),
                r.getDouble("f3"),
                r.getDouble("quality_index")
        );
    }
}
