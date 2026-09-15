import java.security.*;
import java.sql.*;
import java.util.ArrayList;

/*
    HI!! If you're wondering who made this all possible, just know ZILE was here :D

    Hey Rynne! I'm sorry I somewhat contributed to this, I hope you'd appreciate the little help for the database
    Since I know how painstaking it is, especially with JDBC and finding the right database for this

    Normally I would use PostgreSQL, but since we want this to run easily without much setup, SQLite is 
    the best and easiest for this case.

    I hope you guys the best for this project proposal and presentation

    Don't forget to check https://nu.324908.xyz | You can express your gratitude there too!
    Anway, this was a fun project to work on and be part of, I won't reveal who I am though, but you can just call me ZILE :)


    
    Your friendly neighborhood nightowl,
    - ZILE

    Discord: @._.zile
    
*/


public class Database {

    private static final String DB_URL = "jdbc:sqlite:lostandfound.db";

    public static Connection connect() {
        try {
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection(DB_URL);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static void initialize() {
            try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

                String createUsersTable = """
                            CREATE TABLE IF NOT EXISTS users (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                first_name TEXT NOT NULL,
                                last_name TEXT NOT NULL,
                                middle_initial TEXT,
                                suffix TEXT,
                                birthdate TEXT,
                                gender TEXT,
                                email TEXT UNIQUE NOT NULL,
                                password TEXT NOT NULL,
                                student_id TEXT NOT NULL,
                                year_level TEXT,
                                program TEXT,
                                section TEXT,
                                role TEXT DEFAULT 'USER',
                                status TEXT DEFAULT 'PENDING',
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                            );
                        """;
                stmt.execute(createUsersTable);

                String createItemsTable = """
                            CREATE TABLE IF NOT EXISTS items (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                reporter_email TEXT,
                                item_name TEXT,
                                landmark TEXT,
                                date_found TEXT,
                                time_found TEXT,
                                description TEXT,
                                image_path TEXT,
                                status TEXT DEFAULT 'PENDING',
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                            );
                        """;
                stmt.execute(createItemsTable);

                String createClaimsTable = """
                            CREATE TABLE IF NOT EXISTS claims (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                item_id INTEGER,
                                claimer_email TEXT,
                                proof_description TEXT,
                                status TEXT DEFAULT 'PENDING',
                                rejection_reason TEXT,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY(item_id) REFERENCES items(id)
                            );
                        """;
                stmt.execute(createClaimsTable);

                String createMessagesTable = """
                            CREATE TABLE IF NOT EXISTS messages (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                sender_email TEXT,
                                receiver_email TEXT,
                                message_text TEXT,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                            );
                        """;
                stmt.execute(createMessagesTable);

                createDefaultAdmin(conn);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static void createDefaultAdmin(Connection conn) {
        String adminEmail = "admin@nu-moa.edu.ph"; // admin domain
        try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT id FROM users WHERE email = ?"
            )) {
            checkStmt.setString(1, adminEmail);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) { // walang existing admin
                    String insertAdmin = """
                            INSERT INTO users (first_name, last_name, email, password, student_id, role, status)
                            VALUES (?, ?, ?, ?, ?, ?, 'APPROVED')
                        """;
                    try (PreparedStatement pstmt = conn.prepareStatement(insertAdmin)) {
                        pstmt.setString(1, "System");
                        pstmt.setString(2, "Administrator");
                        pstmt.setString(3, adminEmail);
                        pstmt.setString(4, hashPassword("admin123"));
                        pstmt.setString(5, "ADMIN-001");
                        pstmt.setString(6, "ADMIN");
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMessage(String sender, String receiver, String message) {
        String sql = "INSERT INTO messages (sender_email, receiver_email, message_text) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, message);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String[]> getMessages(String user1, String user2) {
        ArrayList<String[]> msgs = new ArrayList<>();
        String sql = """
                    SELECT sender_email, message_text, created_at
                    FROM messages
                    WHERE (sender_email = ? AND receiver_email = ?)
                       OR (sender_email = ? AND receiver_email = ?)
                    ORDER BY created_at ASC
                """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                msgs.add(new String[] {
                        rs.getString("sender_email"),
                        rs.getString("message_text"),
                        rs.getString("created_at")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msgs;
    }

    public static ArrayList<String[]> getConversationPartnersInfo(String currentUserEmail) {
        ArrayList<String[]> partners = new ArrayList<>();
        String sql = """
                    SELECT u.email, u.first_name, u.last_name
                    FROM users u
                    JOIN (
                        SELECT DISTINCT other_email FROM (
                            SELECT receiver_email as other_email FROM messages WHERE sender_email = ?
                            UNION
                            SELECT sender_email as other_email FROM messages WHERE receiver_email = ?
                        )
                    ) chat_partners ON u.email = chat_partners.other_email
                """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUserEmail);
            pstmt.setString(2, currentUserEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                String email = rs.getString("email");
                partners.add(new String[] { email, fullName });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partners;
    }

    public static String getUserName(String email) {
        String sql = "SELECT first_name, last_name FROM users WHERE email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    public static boolean isAdmin(String email) {
        String sql = "SELECT role FROM users WHERE email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "ADMIN".equalsIgnoreCase(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<String[]> getPendingItemsWithClaimants() {
        ArrayList<String[]> list = new ArrayList<>();
        String sql = """
                    SELECT i.item_name, i.landmark, i.date_found, i.time_found,
                           u.first_name AS c_fname, u.last_name AS c_lname,
                           i.description, i.image_path,
                           c.claimer_email, c.id, c.proof_description,
                           r.first_name AS r_fname, r.last_name AS r_lname
                    FROM claims c
                    JOIN items i ON c.item_id = i.id
                    JOIN users u ON c.claimer_email = u.email
                    LEFT JOIN users r ON i.reporter_email = r.email
                    WHERE c.status = 'PENDING'
                """;
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String claimerName = rs.getString("c_fname") + " " + rs.getString("c_lname");
                String reporterName = rs.getString("r_fname") + " " + rs.getString("r_lname");
                if (rs.getString("r_fname") == null) {
                    reporterName = "Unknown";
                }

                list.add(new String[] {
                        rs.getString("item_name"),
                        rs.getString("landmark"),
                        rs.getString("date_found"),
                        rs.getString("time_found"),
                        claimerName,
                        rs.getString("description"),
                        rs.getString("image_path"),
                        rs.getString("claimer_email"),
                        String.valueOf(rs.getInt("id")),
                        rs.getString("proof_description"),
                        reporterName
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending items: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<String[]> getUserClaims(String userEmail) {
        ArrayList<String[]> claims = new ArrayList<>();
        String sql = """
                    SELECT c.id, i.id AS item_id, i.item_name, i.description, i.image_path,
                           c.created_at, c.status, c.rejection_reason
                    FROM claims c
                    JOIN items i ON c.item_id = i.id
                    WHERE c.claimer_email = ?
                    ORDER BY c.created_at DESC
                """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String reason = null;
                try {
                    reason = rs.getString("rejection_reason");
                } catch (SQLException ex) {
                    reason = "No specific reason provided.";
                }
                if (reason == null) {
                    reason = "No specific reason provided.";
                }

                claims.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("item_id")),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getString("image_path"),
                        rs.getString("created_at"),
                        rs.getString("status"),
                        reason
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return claims;
    }

    public static boolean rejectClaim(int claimId, String reason) {
        String sql = "UPDATE claims SET status = 'REJECTED', rejection_reason = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reason);
            pstmt.setInt(2, claimId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String[]> getAllClaimedItems() {
        ArrayList<String[]> items = new ArrayList<>();
        String sql = """
                    SELECT c.id, i.item_name, i.date_found,
                           u1.first_name AS r_fname, u1.last_name AS r_lname,
                           c.created_at,
                           u2.first_name AS c_fname, u2.last_name AS c_lname
                    FROM claims c
                    JOIN items i ON c.item_id = i.id
                    LEFT JOIN users u1 ON i.reporter_email = u1.email
                    LEFT JOIN users u2 ON c.claimer_email = u2.email
                    WHERE c.status = 'APPROVED'
                """;
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String reporterName = rs.getString("r_fname") + " " + rs.getString("r_lname");
                if (rs.getString("r_fname") == null) {
                    reporterName = "Unknown";
                }
                String claimerName = rs.getString("c_fname") + " " + rs.getString("c_lname");
                if (rs.getString("c_fname") == null) {
                    claimerName = "Unknown";
                }
                items.add(new String[] { String.valueOf(rs.getInt("id")), rs.getString("item_name"),
                        rs.getString("date_found"), reporterName, rs.getString("created_at"), claimerName });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static boolean isItemAlreadyClaimed(int itemId) {
        ArrayList<String[]> claimed = getAllClaimedItems();
        for (String[] item : claimed) {
            if (Integer.parseInt(item[0]) == itemId) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String[]> getUserClaimedItems(String userEmail) {
        ArrayList<String[]> claimedItems = new ArrayList<>();
        String sql = """
                    SELECT c.id, i.item_name, i.date_found, u.first_name, u.last_name, c.created_at, u2.first_name AS c_fname, u2.last_name AS c_lname
                    FROM claims c
                    JOIN items i ON c.item_id = i.id
                    LEFT JOIN users u ON i.reporter_email = u.email
                    LEFT JOIN users u2 ON c.claimer_email = u2.email
                    WHERE c.claimer_email = ? AND c.status = 'APPROVED'
                """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String reporterName = rs.getString("first_name") + " " + rs.getString("last_name");
                if (rs.getString("first_name") == null) {
                    reporterName = "Unknown";
                }
                String claimerName = rs.getString("c_fname") + " " + rs.getString("c_lname");
                claimedItems.add(new String[] { String.valueOf(rs.getInt("id")), rs.getString("item_name"),
                        rs.getString("date_found"), reporterName, rs.getString("created_at"), claimerName });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return claimedItems;
    }

    public static ArrayList<String[]> getRecentUsers() {
        ArrayList<String[]> usersList = new ArrayList<>();
        String sql = "SELECT student_id, first_name, last_name, email, program, year_level FROM users WHERE role = 'USER' ORDER BY created_at DESC LIMIT 10";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                usersList.add(new String[] { rs.getString("student_id"), fullName, rs.getString("email"),
                        rs.getString("program"), rs.getString("year_level") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    public static ArrayList<String[]> getUnclaimedItems() {
        ArrayList<String[]> itemsList = new ArrayList<>();
        String sql = "SELECT i.id, i.item_name, i.landmark, i.date_found, i.time_found, u.first_name, u.last_name, i.description, i.image_path FROM items i LEFT JOIN users u ON i.reporter_email = u.email WHERE i.status = 'PENDING'";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String reporterName = rs.getString("first_name") + " " + rs.getString("last_name");
                if (rs.getString("first_name") == null) {
                    reporterName = "Unknown";
                }
                itemsList.add(new String[] { String.valueOf(rs.getInt("id")), rs.getString("item_name"),
                        rs.getString("landmark"), rs.getString("date_found"), rs.getString("time_found"), reporterName,
                        rs.getString("description"), rs.getString("image_path") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    public static ArrayList<String[]> getPendingClaims() {
        ArrayList<String[]> claimsList = new ArrayList<>();
        String sql = """
                    SELECT c.id, i.item_name, c.claimer_email, c.proof_description, c.status
                    FROM claims c
                    JOIN items i ON c.item_id = i.id
                    WHERE c.status = 'PENDING'
                """;
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                claimsList.add(new String[] { String.valueOf(rs.getInt("id")), rs.getString("item_name"),
                        rs.getString("claimer_email"), rs.getString("proof_description"), rs.getString("status") });
            }
        } catch (SQLException e) {
        }
        return claimsList;
    }

    public static boolean approveClaim(int claimId) {
        String sql = "UPDATE claims SET status = 'APPROVED' WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, claimId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean submitClaim(int itemId, String claimerEmail, String proof) {
        String sql = "INSERT INTO claims (item_id, claimer_email, proof_description, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.setString(2, claimerEmail);
            pstmt.setString(3, proof);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reportItem(String email, String itemName, String landmark, String date, String time,
            String desc, String imgPath) {
        String sql = "INSERT INTO items (reporter_email, item_name, landmark, date_found, time_found, description, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, itemName);
            pstmt.setString(3, landmark);
            pstmt.setString(4, date);
            pstmt.setString(5, time);
            pstmt.setString(6, desc);
            pstmt.setString(7, imgPath);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerUser(String f, String l, String mi, String suf, String b, String g, String e,
            String p, String sid, String y, String pr, String s) {
        String sql = "INSERT INTO users (first_name, last_name, middle_initial, suffix, birthdate, gender, email, password, student_id, year_level, program, section, role, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'USER', 'PENDING')";
        String hp = hashPassword(p);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f);
            pstmt.setString(2, l);
            pstmt.setString(3, mi);
            pstmt.setString(4, suf);
            pstmt.setString(5, b);
            pstmt.setString(6, g);
            pstmt.setString(7, e);
            pstmt.setString(8, hp);
            pstmt.setString(9, sid);
            pstmt.setString(10, y);
            pstmt.setString(11, pr);
            pstmt.setString(12, s);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            return false;
        }
    }

    public static String validateLogin(String email, String password) {
        String sql = "SELECT role, status, password FROM users WHERE email = ?";
        String inputHash = hashPassword(password);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (!rs.getString("password").equals(inputHash)) {
                    return "INVALID";
                }
                if ("ADMIN".equals(rs.getString("role"))) {
                    return "ADMIN";
                }
                if ("PENDING".equalsIgnoreCase(rs.getString("status"))) {
                    return "PENDING";
                }
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "INVALID";
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte b : hashBytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String[] getUserDetails(String email) {
        String sql = "SELECT first_name, last_name FROM users WHERE email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[] { rs.getString("first_name"), rs.getString("last_name") };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getFullUserProfile(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[] {
                        rs.getString("first_name"), rs.getString("last_name"), rs.getString("student_id"),
                        rs.getString("email"),
                        rs.getString("program"), rs.getString("section"), rs.getString("year_level"),
                        rs.getString("middle_initial"),
                        rs.getString("suffix"), rs.getString("gender"), rs.getString("birthdate"),
                        rs.getString("role")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String[]> getAllUsers() {
        ArrayList<String[]> usersList = new ArrayList<>();
        String sql = "SELECT student_id, first_name, last_name, email, password, program, role FROM users WHERE status = 'APPROVED'";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usersList.add(new String[] {
                        rs.getString("student_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("email"),
                        "********",
                        rs.getString("program"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    public static ArrayList<String[]> getPendingUsers() {
        ArrayList<String[]> usersList = new ArrayList<>();
        String sql = "SELECT student_id, first_name, last_name, email, program, created_at FROM users WHERE status = 'PENDING' AND role = 'USER'";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usersList.add(new String[] { rs.getString("student_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"), rs.getString("email"),
                        rs.getString("program"), rs.getString("created_at") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    public static boolean approveUser(String email) {
        try (Connection conn = connect();
                PreparedStatement pstmt = conn
                        .prepareStatement("UPDATE users SET status = 'APPROVED' WHERE email = ?")) {
            pstmt.setString(1, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean deleteUser(String email) {
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE email = ?")) {
            pstmt.setString(1, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean rejectUser(String email) {
        return deleteUser(email);
    }

    public static boolean updateUser(String oEmail, String f, String l, String mi, String s, String nEmail, String id,
            String p, String sec, String y, String g) {

        return updateUser(oEmail, f, l, mi, s, nEmail, id, p, sec, y, g, "USER");
    }

    public static boolean updateUser(String oEmail, String f, String l, String mi, String s, String nEmail, String id,
            String p, String sec, String y, String g, String role) {
        String sql = "UPDATE users SET first_name=?, last_name=?, middle_initial=?, suffix=?, email=?, student_id=?, program=?, section=?, year_level=?, gender=?, role=? WHERE email=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f);
            pstmt.setString(2, l);
            pstmt.setString(3, mi);
            pstmt.setString(4, s);
            pstmt.setString(5, nEmail);
            pstmt.setString(6, id);
            pstmt.setString(7, p);
            pstmt.setString(8, sec);
            pstmt.setString(9, y);
            pstmt.setString(10, g);
            pstmt.setString(11, role);
            pstmt.setString(12, oEmail);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean updateUserPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE email=?";
        String hashedPassword = hashPassword(newPassword);

        if (hashedPassword == null) {
            return false;
        }

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getPendingUserCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE status = 'PENDING'";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getUnclaimedItemsCount() {

        String sql = "SELECT COUNT(*) FROM items WHERE status = 'PENDING'";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getClaimedItemsCount() {

        String sql = "SELECT COUNT(*) FROM claims WHERE status = 'APPROVED'";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPendingClaimsCount() {
        String sql = "SELECT COUNT(*) FROM claims WHERE status = 'PENDING'";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
