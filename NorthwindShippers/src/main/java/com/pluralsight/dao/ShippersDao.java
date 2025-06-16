package com.pluralsight.dao;

import com.pluralsight.models.Shippers;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippersDao {

    private final DataSource dataSource;

    // Constructor to inject the database connection pool
    public ShippersDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ✅ 1. INSERT a new shipper and return the new generated ID
    public int insertShipper(String companyName, String phoneNumber) {

        String sql = """
                     INSERT INTO shippers (companyName, phone)
                     VALUES (?, ?)
                     """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            stmt.setString(1, companyName);
            stmt.setString(2, phoneNumber);
            stmt.executeUpdate();

            // Return the newly generated ID
            ResultSet keys = stmt.getGeneratedKeys();

            if (keys.next()) {
                return keys.getInt(1);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ✅ 2. SELECT all shippers
    public List<Shippers> getAllShippers() {
        List<Shippers> shippers = new ArrayList<>();

        String sql = """
                     SELECT shipperId, companyName, phone
                     FROM shippers
                     """;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                Shippers shipper = new Shippers(

                        rs.getInt("shipperId"),
                        rs.getString("companyName"),
                        rs.getString("phone")
                );
                shippers.add(shipper);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shippers;
    }

    // ✅ 3. UPDATE a shipper's phone number by ID
    public void updatePhoneNumber(int shipperId, String newPhoneNumber) {
        String sql = """

                     UPDATE shippers
                     SET phone = ?
                     WHERE shipperId = ?
                     """;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, newPhoneNumber);
            stmt.setInt(2, shipperId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    // Delete shipper by id
    public boolean deleteShipperById(int id) {
        String sql = """
       DELETE FROM shippers
       WHERE shipperId = ?
       """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
