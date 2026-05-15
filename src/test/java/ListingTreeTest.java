
/**
 * Tests for ListingTree — run the main method to see results.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */

import roomer.interfaces.Listing;
import roomer.interfaces.User;
import roomer.interfaces.Users;
import roomer.ListingTree;

import java.time.LocalDateTime;
import java.util.List;

public class ListingTreeTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        testPost();
        testDuplicatePost();
        testGetAllListingsSorted();
        testCanTrade();
        testCanTradeOnlyOnePosted();
        testExecuteTradeSwaps();
        testExecuteTradeRemovesListings();
        testExecuteTradeFailsIfNotPosted();
        testTimesUnchangedOnFailedTrade();
        testCanRepostAfterTrade();
        testPurchaseSuccess();
        testPurchaseExactBalance();
        testPurchaseTransfersMoney();
        testPurchaseBuyerGetsTime();
        testPurchaseInsufficientBalance();
        testPurchaseNoListing();

        System.out.println("\n--- Results: " + passed + " passed, " + failed + " failed ---");
    }

    // ---- helpers ----

    static void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + testName);
            passed++;
        } else {
            System.out.println("FAIL: " + testName);
            failed++;
        }
    }

    // returns a fresh exchange + users every time so tests don't interfere
    static Object[] setup() {
        ListingTree tree = new ListingTree();
        Users users = new Users();

        User alicia = new User("alicia.park@pomona.edu", "pass",
                LocalDateTime.of(2025, 4, 8, 17, 3));
        User bryson = new User("bryson.young@pomona.edu", "pass",
                LocalDateTime.of(2025, 4, 8, 18, 42));
        User carol = new User("carol.rivera@pomona.edu", "pass",
                LocalDateTime.of(2025, 4, 9, 19, 6));

        users.add(alicia);
        users.add(bryson);
        users.add(carol);

        return new Object[] { tree, users, alicia, bryson, carol };
    }

    // ---- post() ----

    static void testAdd() {
        Object[] s = setup();
        ListingTree tree = new ListingTree();
        User alicia = (User) s[2];

        Listing l = exchange.post(alicia, 0.0);
        check("post returns a listing", l != null);
        check("post stores correct email", "alicia.park@pomona.edu".equals(l.getEmail()));
        check("post stores correct price", l.getPrice() == 0.0);
    }

    static void testDuplicatePost() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        User alicia = (User) s[2];

        exchange.post(alicia, 0.0);
        Listing second = exchange.post(alicia, 50.0);
        check("duplicate post returns null", second == null);
        check("duplicate post doesn't grow list", exchange.getAllListings().size() == 1);
    }

    // ---- getAllListings() ----

    static void testGetAllListingsSorted() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        User alicia = (User) s[2];
        User bryson = (User) s[3];
        User carol = (User) s[4];

        // post in reverse order — tree should sort by draw time
        exchange.post(carol, 0.0);
        exchange.post(bryson, 0.0);
        exchange.post(alicia, 0.0);

        List<Listing> sorted = exchange.getAllListings();
        check("listings sorted earliest first",
                "alicia.park@pomona.edu".equals(sorted.get(0).getEmail()) &&
                        "bryson.young@pomona.edu".equals(sorted.get(1).getEmail()) &&
                        "carol.rivera@pomona.edu".equals(sorted.get(2).getEmail()));
    }

    // ---- canTrade() ----

    static void testCanTrade() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        exchange.post(alicia, 0.0);
        exchange.post(bryson, 0.0);
        check("canTrade true when both posted",
                exchange.canTrade(alicia.getEmail(), bryson.getEmail()));
    }

    static void testCanTradeOnlyOnePosted() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        exchange.post(alicia, 0.0);
        check("canTrade false when only one posted",
                !exchange.canTrade(alicia.getEmail(), bryson.getEmail()));
        check("canTrade false when neither posted",
                !exchange.canTrade(bryson.getEmail(), alicia.getEmail()));
    }

    // ---- executeTrade() ----

    static void testExecuteTradeSwaps() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        LocalDateTime aliciaTime = alicia.getDrawTime();
        LocalDateTime brysonTime = bryson.getDrawTime();

        exchange.post(alicia, 0.0);
        exchange.post(bryson, 0.0);
        boolean ok = exchange.executeTrade(alicia.getEmail(), bryson.getEmail(), users);

        check("executeTrade returns true on success", ok);
        check("alicia gets bryson's time", brysonTime.equals(alicia.getDrawTime()));
        check("bryson gets alicia's time", aliciaTime.equals(bryson.getDrawTime()));
    }

    static void testExecuteTradeRemovesListings() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        exchange.post(alicia, 0.0);
        exchange.post(bryson, 0.0);
        exchange.executeTrade(alicia.getEmail(), bryson.getEmail(), users);
        check("listings removed after trade", exchange.getAllListings().size() == 0);
    }

    static void testExecuteTradeFailsIfNotPosted() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        exchange.post(alicia, 0.0);
        check("trade fails if one user hasn't posted",
                !exchange.executeTrade(alicia.getEmail(), bryson.getEmail(), users));
    }

    static void testTimesUnchangedOnFailedTrade() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        LocalDateTime aliciaTime = alicia.getDrawTime();
        exchange.post(alicia, 0.0);
        exchange.executeTrade(alicia.getEmail(), bryson.getEmail(), users);
        check("draw time unchanged after failed trade", aliciaTime.equals(alicia.getDrawTime()));
    }

    static void testCanRepostAfterTrade() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        exchange.post(alicia, 0.0);
        exchange.post(bryson, 0.0);
        exchange.executeTrade(alicia.getEmail(), bryson.getEmail(), users);
        check("user can repost after trade", exchange.post(alicia, 0.0) != null);
    }

    // ---- purchase() ----

    static void testPurchaseSuccess() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        alicia.addMoney(100.0);
        exchange.post(bryson, 50.0);
        check("purchase succeeds with enough balance",
                exchange.purchase(alicia.getEmail(), bryson.getEmail(), users));
    }

    static void testPurchaseExactBalance() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        alicia.addMoney(50.0); // exactly the price
        exchange.post(bryson, 50.0);
        check("purchase succeeds with exact balance",
                exchange.purchase(alicia.getEmail(), bryson.getEmail(), users));
    }

    static void testPurchaseTransfersMoney() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        alicia.addMoney(100.0);
        exchange.post(bryson, 50.0);
        exchange.purchase(alicia.getEmail(), bryson.getEmail(), users);
        check("buyer balance decreases", alicia.getBalance() == 50.0);
        check("seller balance increases", bryson.getBalance() == 50.0);
    }

    static void testPurchaseBuyerGetsTime() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        LocalDateTime brysonTime = bryson.getDrawTime();
        alicia.addMoney(100.0);
        exchange.post(bryson, 50.0);
        exchange.purchase(alicia.getEmail(), bryson.getEmail(), users);
        check("buyer gets seller's draw time", brysonTime.equals(alicia.getDrawTime()));
    }

    static void testPurchaseInsufficientBalance() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        alicia.addMoney(10.0); // not enough
        exchange.post(bryson, 50.0);
        check("purchase fails with insufficient balance",
                !exchange.purchase(alicia.getEmail(), bryson.getEmail(), users));
        check("balances unchanged on failed purchase",
                alicia.getBalance() == 10.0 && bryson.getBalance() == 0.0);
    }

    static void testPurchaseNoListing() {
        Object[] s = setup();
        DrawExchange exchange = (DrawExchange) s[0];
        Users users = (Users) s[1];
        User alicia = (User) s[2];
        User bryson = (User) s[3];

        alicia.addMoney(100.0);
        // bryson never posted
        check("purchase fails when no listing exists",
                !exchange.purchase(alicia.getEmail(), bryson.getEmail(), users));
    }
}