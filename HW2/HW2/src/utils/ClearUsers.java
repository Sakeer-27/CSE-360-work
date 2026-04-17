package utils;

import database.Database;
import java.sql.SQLException;
import java.sql.Statement;

public class ClearUsers {

    public static void main(String[] args) {
        Database db = new Database();
        try {
            db.connectToDatabase();
            clearAllUsers(db);
            System.out.println("✅ All users deleted successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Error clearing users: " + e.getMessage());
        } finally {
            db.closeConnection();
        }
    }

    public static void clearAllUsers(Database db) throws SQLException {
        Statement stmt = db.getConnection().createStatement();
        stmt.executeUpdate("DELETE FROM userDB");
        stmt.close();
    }
}
