
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewQuestions {
    public static void main(String[] args) {
        String sql = """
            SELECT id, category, question_text, correct_option
            FROM questions
            ORDER BY id
            """;

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + ". [" +
                    rs.getString("category") + "] " +
                    rs.getString("question_text"));
                System.out.println(
                    "   Correct option: " +
                    rs.getString("correct_option"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
