import static spark.Spark.*;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
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

            if (user.username == null || user.password == null) {
                res.status(400); // Bad Request
                return gson.toJson(new ResponseMessage("Username and password are required."));
            }

            if (registerUser(user.username, user.password)) {
                res.status(201); // Created
                return gson.toJson(new ResponseMessage("Registration successful!"));
            } else {
                res.status(409); // Conflict
                return gson.toJson(new ResponseMessage("Username already exists."));
            }
        });

        post("/login", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);

            if (authenticateUser(user.username, user.password)) {
                res.status(200); // OK
                return gson.toJson(new ResponseMessage("Login successful!"));
            } else {
                res.status(401); // Unauthorized
                return gson.toJson(new ResponseMessage("Invalid credentials."));
            }
        });
    }

    static class User {
        String username;
        String password;
    }

    static class ResponseMessage {
        String message;
        ResponseMessage(String message) {
            this.message = message;
        }
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
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, storedHashedPassword);
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

