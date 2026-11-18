package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public void add(Review rev) throws SQLException {
        String sql = "INSERT INTO review(submission_id, reviewer_name, comment, review_date) VALUES(?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, rev);
            p.executeUpdate();
        }
    }

    public List<Review> getAll() throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT id, submission_id, reviewer_name, comment, review_date FROM review ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Review getById(long id) throws SQLException {
        String sql = "SELECT id, submission_id, reviewer_name, comment, review_date FROM review WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Review rev) throws SQLException {
        String sql = "UPDATE review SET submission_id=?, reviewer_name=?, comment=?, review_date=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, rev);
            p.setLong(5, rev.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM review WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Review rev) throws SQLException {
        p.setLong(1, rev.getSubmissionId());
        p.setString(2, rev.getReviewerName());
        p.setString(3, rev.getComment());
        p.setString(4, rev.getReviewDate());
    }

    private Review map(ResultSet r) throws SQLException {
        return new Review(
                r.getLong("id"),
                r.getLong("submission_id"),
                r.getString("reviewer_name"),
                r.getString("comment"),
                r.getString("review_date")
        );
    }
}
