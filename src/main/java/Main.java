import static spark.Spark.*;
import com.google.gson.Gson;
import java.sql.*;

public class Main {
    static final String DB_URL = "jdbc:sqlite:users.db";

    public static void main(String[] args) {
        port(8080);
        Gson gson = new Gson();
        initializeDatabase();

        post("/signup", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);

            if (registerUser(user.username, user.password)) {
                return "Registration successful!";
            } else {
                return "Username already exists.";
            }
        });

        post("/login", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);

            if (authenticateUser(user.username, user.password)) {
                return "Login successful!";
            } else {
                return "Invalid credentials.";
            }
        });
    }

    static class User {
        String username;
        String password;
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users " +
                         "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         " username TEXT UNIQUE NOT NULL, " +
                         " password TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean registerUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
