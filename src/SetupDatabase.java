
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetupDatabase {
    public static void main(String[] args) {
        String createTable = """
            CREATE TABLE IF NOT EXISTS questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category TEXT NOT NULL,
                question_text TEXT NOT NULL,
                option_a TEXT NOT NULL,
                option_b TEXT NOT NULL,
                option_c TEXT NOT NULL,
                option_d TEXT NOT NULL,
                correct_option TEXT NOT NULL
                    CHECK (correct_option IN ('A','B','C','D'))
            )
            """;

        String insertQuestion = """
            INSERT INTO questions
            (category, question_text, option_a, option_b,
             option_c, option_d, correct_option)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(createTable);
            System.out.println("Questions table is ready.");

            String countSql = "SELECT COUNT(*) FROM questions";
            int count;

            try (var rs = stmt.executeQuery(countSql)) {
                rs.next();
                count = rs.getInt(1);
            }

            if (count == 0) {
                try (PreparedStatement ps =
                         con.prepareStatement(insertQuestion)) {

                    ps.setString(1, "Java Basics");
                    ps.setString(2,
                        "Which keyword is used to inherit a class in Java?");
                    ps.setString(3, "this");
                    ps.setString(4, "extends");
                    ps.setString(5, "implements");
                    ps.setString(6, "super");
                    ps.setString(7, "B");
                    ps.executeUpdate();

                    ps.setString(1, "Java Basics");
                    ps.setString(2,
                        "Which method is the entry point of a Java application?");
                    ps.setString(3, "start()");
                    ps.setString(4, "run()");
                    ps.setString(5, "main()");
                    ps.setString(6, "init()");
                    ps.setString(7, "C");
                    ps.executeUpdate();

                    ps.setString(1, "JDBC");
                    ps.setString(2,
                        "Which class is commonly used to obtain a JDBC connection?");
                    ps.setString(3, "DriverManager");
                    ps.setString(4, "ResultSet");
                    ps.setString(5, "SQLException");
                    ps.setString(6, "Statement");
                    ps.setString(7, "A");
                    ps.executeUpdate();

                    System.out.println("Inserted 3 sample questions.");
                }
            } else {
                System.out.println(
                    "Existing questions found; sample data not reinserted.");
            }

            System.out.println("Database setup completed.");

        } catch (SQLException e) {
            System.out.println("Database setup failed.");
            e.printStackTrace();
        }
    }
}
