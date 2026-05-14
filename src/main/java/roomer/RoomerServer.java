/**
 * HTTP server that exposes Roomer's backend as JSON endpoints
 * and serves the frontend at http://localhost:8080.
 *
 * Run this class, then open http://localhost:8080 in your browser.
 * GUI development assisted by Claude (Anthropic) — see course LLM policy.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */
package roomer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import roomer.interfaces.Building;
import roomer.interfaces.Listing;
import roomer.interfaces.Room;
import roomer.interfaces.Rooms;
import roomer.interfaces.User;
import roomer.interfaces.Users;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomerServer {

    private static Rooms rooms;
    private static Users users;
    private static DrawExchange exchange;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH);

    public static void main(String[] args) throws IOException {
        rooms = RoomDataLoader.load("CS62 Final Project Data.xlsx");
        users = new Users();
        exchange = new DrawExchange();
        seedUsers();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/rooms/filter",     new FilterHandler());
        server.createContext("/rooms/search",     new SearchHandler());
        server.createContext("/rooms",            new AllRoomsHandler());
        server.createContext("/exchange/slots",   new SlotsHandler());
        server.createContext("/exchange/post",    new PostSlotHandler());
        server.createContext("/exchange/check",   new CheckTradeHandler());
        server.createContext("/exchange/trade",   new TradeHandler());
        server.createContext("/exchange/purchase", new PurchaseHandler());
        server.createContext("/",                 new FrontendHandler());

        server.start();
        System.out.println("Roomer running at http://localhost:8080");
    }

    /** Seeds demo users for testing the exchange feature. */
    private static void seedUsers() {
        users.add(new User("alicia.park@pomona.edu", "pass",
            LocalDateTime.parse("Apr 8, 2025 5:03 PM", FORMATTER)));
        users.add(new User("bryson.young@pomona.edu", "pass",
            LocalDateTime.parse("Apr 8, 2025 6:42 PM", FORMATTER)));
        users.add(new User("carol.rivera@pomona.edu", "pass",
            LocalDateTime.parse("Apr 9, 2025 7:06 PM", FORMATTER)));
    }

    /** Sends an HTTP response with the given status, body, and content type. */
    private static void send(HttpExchange ex, int status, String body, String contentType) throws IOException {
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Sends a JSON response. */
    private static void json(HttpExchange ex, int status, String body) throws IOException {
        send(ex, status, body, "application/json");
    }

    /** Parses and URL-decodes query parameters from a URI into a Map. */
    private static Map<String, String> query(URI uri) {
        Map<String, String> map = new HashMap<>();
        String q = uri.getQuery();
        if (q == null) return map;
        for (String pair : q.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(
                    URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                );
            }
        }
        return map;
    }

    /** Converts a Room to a JSON string. */
    private static String roomJson(Room r) {
        return "{\"roomName\":\"" + r.getRoomName() + "\""
             + ",\"building\":\"" + r.getBuilding() + "\""
             + ",\"occupancy\":"  + r.getOccupancy()
             + ",\"sqft\":"       + r.getSquareFeet()
             + ",\"hasAC\":"      + r.hasAC() + "}";
    }

    /** Converts a Listing to a JSON string. */
    private static String listingJson(Listing l) {
        return "{\"ownerEmail\":\"" + l.getEmail() + "\""
             + ",\"drawTime\":\""   + l.getDrawTime() + "\""
             + ",\"price\":"        + l.getPrice() + "}";
    }

    /** Converts a list of Rooms to a JSON array, skipping invalid entries. */
    private static String roomListJson(List<Room> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Room r : list) {
            if (r.getOccupancy() == 0) continue;
            if (!first) sb.append(",");
            sb.append(roomJson(r));
            first = false;
        }
        return sb.append("]").toString();
    }

    // Serves index.html at http://localhost:8080
    static class FrontendHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (!ex.getRequestURI().getPath().equals("/")) {
                json(ex, 404, "{\"error\":\"Not found\"}");
                return;
            }
            try {
                byte[] bytes = Files.readAllBytes(Paths.get("index.html"));
                send(ex, 200, new String(bytes, StandardCharsets.UTF_8), "text/html");
            } catch (IOException e) {
                json(ex, 500, "{\"error\":\"Could not load index.html\"}");
            }
        }
    }

    // GET /rooms — returns all rooms
    static class AllRoomsHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            json(ex, 200, roomListJson(rooms.getAllRooms()));
        }
    }

    // GET /rooms/filter?building=X&occupancy=Y&ac=true
    static class FilterHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> p = query(ex.getRequestURI());
            Building building = null;
            Integer occupancy = null;
            Boolean hasAC = null;
            try { if (p.containsKey("building"))  building  = Building.valueOf(p.get("building")); }  catch (Exception ignored) {}
            try { if (p.containsKey("occupancy")) occupancy = Integer.parseInt(p.get("occupancy")); } catch (Exception ignored) {}
            if (p.containsKey("ac")) hasAC = p.get("ac").equalsIgnoreCase("true");
            json(ex, 200, roomListJson(rooms.filter(building, occupancy, hasAC, null, null)));
        }
    }

    // GET /rooms/search?name=Walker-719
    static class SearchHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> p = query(ex.getRequestURI());
            String name = p.get("name");
            if (name == null) { json(ex, 400, "{\"error\":\"Missing name\"}"); return; }
            Room room = rooms.get(name);
            if (room == null) json(ex, 404, "{\"error\":\"Room not found\"}");
            else              json(ex, 200, roomJson(room));
        }
    }

    // GET /exchange/slots — all active listings sorted by draw time
    static class SlotsHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            List<Listing> listings = exchange.getAllListings();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < listings.size(); i++) {
                sb.append(listingJson(listings.get(i)));
                if (i < listings.size() - 1) sb.append(",");
            }
            json(ex, 200, sb.append("]").toString());
        }
    }

    // GET /exchange/post?email=X&price=Y — posts a user's draw time
    static class PostSlotHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> p = query(ex.getRequestURI());
            String email = p.get("email");
            if (email == null) { json(ex, 400, "{\"error\":\"Missing email\"}"); return; }
            User user = users.get(email);
            if (user == null) { json(ex, 404, "{\"error\":\"User not found\"}"); return; }
            double price = 0.0;
            try { if (p.containsKey("price")) price = Double.parseDouble(p.get("price")); } catch (Exception ignored) {}
            Listing listing = exchange.post(user, price);
            if (listing == null) json(ex, 400, "{\"error\":\"User already has an active listing\"}");
            else                 json(ex, 200, listingJson(listing));
        }
    }

    // GET /exchange/check?emailA=X&emailB=Y — checks if a trade is possible
    static class CheckTradeHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> p = query(ex.getRequestURI());
            String a = p.get("emailA"), b = p.get("emailB");
            if (a == null || b == null) { json(ex, 400, "{\"error\":\"Missing emails\"}"); return; }
            json(ex, 200, "{\"canTrade\":" + exchange.canTrade(a, b) + "}");
        }
    }

    // GET /exchange/trade?emailA=X&emailB=Y — swaps draw times between two users
    static class TradeHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> p = query(ex.getRequestURI());
            String a = p.get("emailA"), b = p.get("emailB");
            if (a == null || b == null) { json(ex, 400, "{\"error\":\"Missing emails\"}"); return; }
            boolean ok = exchange.executeTrade(a, b, users);
            if (ok) json(ex, 200, "{\"success\":true}");
            else    json(ex, 400, "{\"success\":false,\"error\":\"Trade failed\"}");
        }
    }

    // GET /exchange/purchase?buyer=X&seller=Y — buys a draw time from the marketplace
    static class PurchaseHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> p = query(ex.getRequestURI());
            String buyer = p.get("buyer"), seller = p.get("seller");
            if (buyer == null || seller == null) { json(ex, 400, "{\"error\":\"Missing emails\"}"); return; }
            boolean ok = exchange.purchase(buyer, seller, users);
            if (ok) json(ex, 200, "{\"success\":true}");
            else    json(ex, 400, "{\"success\":false,\"error\":\"Purchase failed — insufficient balance or listing not found\"}");
        }
    }
}