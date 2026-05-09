package roomer;

import java.time.LocalDateTime;
import java.util.List;

import roomer.interfaces.Listing;

/**
 * Quick smoke-test / usage demo for ListingTree.
 * Run with: javac *.java && java ListingTreeTest
 */
public class ListingTreeTest {

    public static void main(String[] args) {

        ListingTree tree = new ListingTree();

        // ── Build some listings ───────────────────────────────────────────────
        Listing a = new Listing("L001", "alice@uni.edu", LocalDateTime.of(2026, 3, 10, 8, 0));
        Listing b = new Listing("L002", "bob@uni.edu", LocalDateTime.of(2026, 3, 10, 9, 30));
        Listing c = new Listing("L003", "carol@uni.edu", LocalDateTime.of(2026, 3, 10, 8, 0)); // same time as alice
        Listing d = new Listing("L004", "dave@uni.edu", LocalDateTime.of(2026, 3, 11, 14, 0));
        Listing e = new Listing("L005", "eve@uni.edu", LocalDateTime.of(2026, 3, 12, 7, 45));

        tree.add(a);
        tree.add(b);
        tree.add(c);
        tree.add(d);
        tree.add(e);

        System.out.println("=== All listings (sorted) ===");
        tree.allSorted().forEach(System.out::println);

        System.out.println("\n=== Earliest ===");
        System.out.println(tree.peekEarliest()); // L001 (alice)

        System.out.println("\n=== Latest ===");
        System.out.println(tree.peekLatest()); // L005 (eve)

        System.out.println("\n=== Range: March 10 08:00 – March 11 14:00 ===");
        List<Listing> rangeResult = tree.range(
                LocalDateTime.of(2026, 3, 10, 8, 0),
                LocalDateTime.of(2026, 3, 11, 14, 0));
        rangeResult.forEach(System.out::println);

        System.out.println("\n=== Find by id 'L003' ===");
        System.out.println(tree.findById("L003"));

        System.out.println("\n=== Remove L001 (alice) ===");
        tree.removeById("L001");
        System.out.println("New earliest: " + tree.peekEarliest()); // L003 (carol)

        System.out.println("\n=== Poll earliest ===");
        Listing polled = tree.pollEarliest();
        System.out.println("Polled: " + polled); // L003 (carol)
        System.out.println("New earliest: " + tree.peekEarliest()); // L002 (bob)

        System.out.println("\n=== Final tree ===");
        System.out.println(tree);
    }
}