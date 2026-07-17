import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ApiServer {

    private static final int PORT = Integer.parseInt(
            System.getenv("API_PORT") != null ? System.getenv("API_PORT") : "8080");

    public static void main(String[] args) throws IOException {
        DatabaseConnection.initializeDatabase();
        start();
    }

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/health", ApiServer::handleHealth);
        server.createContext("/api/books", ApiServer::handleBooksCollection);
        server.createContext("/api/books/", ApiServer::handleBookItem);
        server.createContext("/api/members", ApiServer::handleMembersCollection);
        server.createContext("/api/openapi.yaml", ApiServer::handleOpenApiSpec);
        server.createContext("/docs", ApiServer::handleDocs);

        server.setExecutor(null);
        server.start();
        System.out.println("REST API server started on port " + PORT);
        System.out.println("Swagger docs available at http://localhost:" + PORT + "/docs");
    }


    private static void handleDocs(HttpExchange exchange) throws IOException {
        byte[] bytes = OpenApiSpec.DOCS_HTML.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void handleOpenApiSpec(HttpExchange exchange) throws IOException {
        byte[] bytes = OpenApiSpec.YAML.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/yaml; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }


    private static void handleHealth(HttpExchange exchange) throws IOException {
        sendJson(exchange, 200, JsonUtil.object("status", JsonUtil.toJsonString("ok")));
    }


    private static void handleBooksCollection(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> getAllBooks(exchange);
                case "POST" -> createBook(exchange);
                default -> sendError(exchange, 405, "Method not allowed");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }


    private static void handleBookItem(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/"); // ["", "api", "books", "{id}", maybe "borrow"/"return"]

        if (parts.length < 4 || parts[3].isBlank()) {
            sendError(exchange, 400, "Book id is required");
            return;
        }

        String bookId = URLDecoder.decode(parts[3], StandardCharsets.UTF_8);
        String action = parts.length >= 5 ? parts[4] : null;
        String method = exchange.getRequestMethod();

        try {
            if (action == null) {
                switch (method) {
                    case "GET" -> getBookById(exchange, bookId);
                    case "DELETE" -> deleteBook(exchange, bookId);
                    default -> sendError(exchange, 405, "Method not allowed");
                }
            } else if (action.equals("borrow") && method.equals("PUT")) {
                setBookAvailability(exchange, bookId, false, "Book is already borrowed");
            } else if (action.equals("return") && method.equals("PUT")) {
                setBookAvailability(exchange, bookId, true, "Book is already available");
            } else {
                sendError(exchange, 404, "Unknown endpoint");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private static void getAllBooks(HttpExchange exchange) throws IOException, SQLException {
        List<String> books = new ArrayList<>();
        String sql = "SELECT book_id, title, author, is_available FROM books ORDER BY title";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(bookToJson(rs.getString("book_id"), rs.getString("title"),
                        rs.getString("author"), rs.getBoolean("is_available")));
            }
        }
        sendJson(exchange, 200, JsonUtil.array(books));
    }

    private static void getBookById(HttpExchange exchange, String bookId) throws IOException, SQLException {
        String sql = "SELECT book_id, title, author, is_available FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sendJson(exchange, 200, bookToJson(rs.getString("book_id"), rs.getString("title"),
                            rs.getString("author"), rs.getBoolean("is_available")));
                } else {
                    sendError(exchange, 404, "Book not found: " + bookId);
                }
            }
        }
    }

    private static void createBook(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String bookId = body.get("bookId");
        String title = body.get("title");
        String author = body.get("author");

        if (isBlank(bookId) || isBlank(title) || isBlank(author)) {
            sendError(exchange, 400, "Fields 'bookId', 'title' and 'author' are required");
            return;
        }

        String sql = "INSERT INTO books (book_id, title, author, is_available) VALUES (?, ?, ?, TRUE) " +
                "ON CONFLICT (book_id) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            stmt.setString(2, title);
            stmt.setString(3, author);
            if (stmt.executeUpdate() == 0) {
                sendError(exchange, 409, "Book with id '" + bookId + "' already exists");
                return;
            }
        }
        sendJson(exchange, 201, bookToJson(bookId, title, author, true));
    }

    private static void deleteBook(HttpExchange exchange, String bookId) throws IOException, SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            if (stmt.executeUpdate() == 0) {
                sendError(exchange, 404, "Book not found: " + bookId);
            } else {
                sendJson(exchange, 200, JsonUtil.object("message", JsonUtil.toJsonString("Book deleted: " + bookId)));
            }
        }
    }

    private static void setBookAvailability(HttpExchange exchange, String bookId, boolean newAvailability,
                                            String conflictMessage) throws IOException, SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String title;
            String author;
            boolean currentAvailability;

            String selectSql = "SELECT title, author, is_available FROM books WHERE book_id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, bookId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        sendError(exchange, 404, "Book not found: " + bookId);
                        return;
                    }
                    title = rs.getString("title");
                    author = rs.getString("author");
                    currentAvailability = rs.getBoolean("is_available");
                }
            }

            if (currentAvailability == newAvailability) {
                sendError(exchange, 409, conflictMessage);
                return;
            }

            String updateSql = "UPDATE books SET is_available = ? WHERE book_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setBoolean(1, newAvailability);
                updateStmt.setString(2, bookId);
                updateStmt.executeUpdate();
            }

            sendJson(exchange, 200, bookToJson(bookId, title, author, newAvailability));
        }
    }

    private static String bookToJson(String bookId, String title, String author, boolean available) {
        return JsonUtil.object(
                "bookId", JsonUtil.toJsonString(bookId),
                "title", JsonUtil.toJsonString(title),
                "author", JsonUtil.toJsonString(author),
                "available", String.valueOf(available)
        );
    }

    private static void handleMembersCollection(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> getAllMembers(exchange);
                case "POST" -> createMember(exchange);
                default -> sendError(exchange, 405, "Method not allowed");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private static void getAllMembers(HttpExchange exchange) throws IOException, SQLException {
        List<String> members = new ArrayList<>();
        String sql = "SELECT user_id, name FROM library_members ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                members.add(JsonUtil.object(
                        "userId", JsonUtil.toJsonString(rs.getString("user_id")),
                        "name", JsonUtil.toJsonString(rs.getString("name"))
                ));
            }
        }
        sendJson(exchange, 200, JsonUtil.array(members));
    }

    private static void createMember(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String userId = body.get("userId");
        String name = body.get("name");

        if (isBlank(userId) || isBlank(name)) {
            sendError(exchange, 400, "Fields 'userId' and 'name' are required");
            return;
        }

        String sql = "INSERT INTO library_members (user_id, name) VALUES (?, ?) ON CONFLICT (user_id) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, name);
            if (stmt.executeUpdate() == 0) {
                sendError(exchange, 409, "Member with id '" + userId + "' already exists");
                return;
            }
        }
        sendJson(exchange, 201, JsonUtil.object(
                "userId", JsonUtil.toJsonString(userId),
                "name", JsonUtil.toJsonString(name)
        ));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (var is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJson(exchange, statusCode, JsonUtil.object("error", JsonUtil.toJsonString(message)));
    }
}
