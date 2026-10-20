
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuizServer {
    private static final Path WEB_DIR =
            Paths.get("web").toAbsolutePath().normalize();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 8080), 0);

        server.createContext("/api/questions", QuizServer::getQuestions);
        server.createContext("/api/submit", QuizServer::submitQuiz);
        server.createContext("/", QuizServer::serveWebsite);

        server.start();
        System.out.println("Quiz website: http://localhost:8080");
        System.out.println("API: http://localhost:8080/api/questions");
        System.out.println("Press Ctrl+C to stop.");
    }

    private static void getQuestions(HttpExchange ex)
            throws IOException {
        if (!ex.getRequestMethod().equals("GET")) {
            sendJson(ex, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String sql = """
            SELECT id, category, question_text,
                   option_a, option_b, option_c, option_d
            FROM questions ORDER BY id
            """;

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"category\":\"")
                    .append(escape(rs.getString("category"))).append("\",")
                    .append("\"question\":\"")
                    .append(escape(rs.getString("question_text"))).append("\",")
                    .append("\"options\":{")
                    .append("\"A\":\"").append(escape(rs.getString("option_a"))).append("\",")
                    .append("\"B\":\"").append(escape(rs.getString("option_b"))).append("\",")
                    .append("\"C\":\"").append(escape(rs.getString("option_c"))).append("\",")
                    .append("\"D\":\"").append(escape(rs.getString("option_d"))).append("\"")
                    .append("}}");
            }

            json.append("]");
            sendJson(ex, 200, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, "{\"error\":\"Could not load questions\"}");
        }
    }

    private static void submitQuiz(HttpExchange ex)
            throws IOException {
        if (!ex.getRequestMethod().equals("POST")) {
            sendJson(ex, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = new String(
                ex.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        Map<Integer, String> submitted = new HashMap<>();

        try {
            if (!body.isBlank()) {
                for (String item : body.split(",")) {
                    String[] pair = item.split(":", 2);
                    if (pair.length != 2) continue;

                    int id = Integer.parseInt(pair[0].trim());
                    String choice = pair[1].trim().toUpperCase();

                    if (id > 0 && choice.matches("[ABCD]")) {
                        submitted.put(id, choice);
                    }
                }
            }
        } catch (NumberFormatException e) {
            sendJson(ex, 400, "{\"error\":\"Invalid answer format\"}");
            return;
        }

        String sql = "SELECT id, correct_option FROM questions";
        int score = 0;
        int total = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Set<Integer> seen = new HashSet<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String correct = rs.getString("correct_option");
                total++;

                if (submitted.containsKey(id) && seen.add(id)
                        && correct.equalsIgnoreCase(submitted.get(id))) {
                    score++;
                }
            }

            sendJson(ex, 200,
                    "{\"score\":" + score + ",\"total\":" + total + "}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, "{\"error\":\"Could not calculate score\"}");
        }
    }

    private static void serveWebsite(HttpExchange ex)
            throws IOException {
        if (!ex.getRequestMethod().equals("GET")) {
            sendJson(ex, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String requested = ex.getRequestURI().getPath();
        if (requested.equals("/")) requested = "/index.html";

        Path file = WEB_DIR.resolve("." + requested).normalize();

        if (!file.startsWith(WEB_DIR)) {
            sendJson(ex, 403, "{\"error\":\"Forbidden\"}");
            return;
        }

        if (!Files.isRegularFile(file)) {
            sendJson(ex, 404, "{\"error\":\"File not found\"}");
            return;
        }

        String name = file.getFileName().toString().toLowerCase();
        String type = name.endsWith(".html") ? "text/html; charset=UTF-8"
                : name.endsWith(".css") ? "text/css; charset=UTF-8"
                : name.endsWith(".js") ? "text/javascript; charset=UTF-8"
                : "application/octet-stream";

        byte[] bytes = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(200, bytes.length);

        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String escape(String value) {
        StringBuilder result = new StringBuilder();

        for (char ch : value.toCharArray()) {
            switch (ch) {
                case '"'  -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\b' -> result.append("\\b");
                case '\f' -> result.append("\\f");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> {
                    if (ch < 0x20) {
                        result.append(String.format("\\u%04x", (int) ch));
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    private static void sendJson(
            HttpExchange ex, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(status, bytes.length);

        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }
}
