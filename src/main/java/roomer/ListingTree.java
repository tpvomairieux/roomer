package roomer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import roomer.interfaces.Listing;

/**
 * A sorted collection of housing-draw {@link Listing}s, keyed by
 * {@link LocalDateTime}.
 *
 * <p>
 * Backed by a {@link TreeMap} (Red-Black BST), so all core operations run in
 * O(log n). Multiple listings may share the same draw time; they are stored in
 * insertion-order within a {@link LinkedHashMap} at each time key (keyed by
 * listing id for O(1) individual removal).
 *
 * <p>
 * Iteration via {@link #allSorted()} yields listings earliest-first.
 */
public class ListingTree {

    // TreeMap gives us a sorted BST over LocalDateTime keys.
    // Each key maps to a LinkedHashMap<listingId, Listing> so we can:
    // • handle draw-time collisions
    // • remove a specific listing by id in O(1) within the bucket
    private final TreeMap<LocalDateTime, LinkedHashMap<String, Listing>> tree = new TreeMap<>();

    // Secondary index: listingId → drawTime, for O(log n) removals by id.
    private final HashMap<String, LocalDateTime> idIndex = new HashMap<>();

    private int size = 0;

    // ── Mutators ─────────────────────────────────────────────────────────────

    /**
     * Inserts a listing. Duplicate ids are rejected.
     *
     * @throws IllegalArgumentException if a listing with the same id already exists
     */
    public void add(Listing listing) {
        Objects.requireNonNull(listing, "listing must not be null");

        if (idIndex.containsKey(listing.getId())) {
            throw new IllegalArgumentException("Listing id already present: " + listing.getId());
        }

        tree.computeIfAbsent(listing.getDrawTime(), k -> new LinkedHashMap<>())
                .put(listing.getId(), listing);

        idIndex.put(listing.getId(), listing.getDrawTime());
        size++;
    }

    /**
     * Removes the listing with the given id.
     *
     * @return the removed listing, or {@code null} if not found
     */
    public Listing removeById(String id) {
        LocalDateTime key = idIndex.remove(id);
        if (key == null)
            return null;

        LinkedHashMap<String, Listing> bucket = tree.get(key);
        Listing removed = bucket.remove(id);

        if (bucket.isEmpty())
            tree.remove(key);
        size--;
        return removed;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /**
     * Returns the listing with the earliest draw time, or {@code null} if empty.
     */
    public Listing peekEarliest() {
        if (tree.isEmpty())
            return null;
        return tree.firstEntry().getValue().values().iterator().next();
    }

    /** Returns the listing with the latest draw time, or {@code null} if empty. */
    public Listing peekLatest() {
        if (tree.isEmpty())
            return null;
        LinkedHashMap<String, Listing> bucket = tree.lastEntry().getValue();
        // Walk to the last entry in insertion order
        Listing last = null;
        for (Listing l : bucket.values())
            last = l;
        return last;
    }

    /**
     * Removes and returns the listing with the earliest draw time.
     *
     * @return the earliest listing, or {@code null} if empty
     */
    public Listing pollEarliest() {
        if (tree.isEmpty())
            return null;
        Map.Entry<LocalDateTime, LinkedHashMap<String, Listing>> entry = tree.firstEntry();
        Iterator<Map.Entry<String, Listing>> it = entry.getValue().entrySet().iterator();
        Map.Entry<String, Listing> first = it.next();
        it.remove(); // remove from bucket
        idIndex.remove(first.getKey());
        if (entry.getValue().isEmpty())
            tree.pollFirstEntry();
        size--;
        return first.getValue();
    }

    /**
     * Returns a snapshot list of all listings with draw times in [{@code from},
     * {@code to}],
     * sorted earliest-first.
     */
    public List<Listing> range(LocalDateTime from, LocalDateTime to) {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");

        List<Listing> result = new ArrayList<>();
        for (LinkedHashMap<String, Listing> bucket : tree.subMap(from, true, to, true).values()) {
            result.addAll(bucket.values());
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Looks up a listing by id in O(log n).
     *
     * @return the listing, or {@code null} if not found
     */
    public Listing findById(String id) {
        LocalDateTime key = idIndex.get(id);
        if (key == null)
            return null;
        return tree.get(key).get(id);
    }

    /**
     * Returns an unmodifiable snapshot of all listings, sorted earliest draw time
     * first.
     * Listings at the same draw time appear in insertion order.
     */
    public List<Listing> allSorted() {
        List<Listing> result = new ArrayList<>(size);
        for (LinkedHashMap<String, Listing> bucket : tree.values()) {
            result.addAll(bucket.values());
        }
        return Collections.unmodifiableList(result);
    }

    /** Returns {@code true} if the tree contains a listing with the given id. */
    public boolean containsId(String id) {
        return idIndex.containsKey(id);
    }

    /** Number of listings currently stored. */
    public int size() {
        return size;
    }

    /** Returns {@code true} if no listings are stored. */
    public boolean isEmpty() {
        return size == 0;
    }

    // ── Object override ───────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "ListingTree{size=" + size + ", listings=" + allSorted() + "}";
    }
}