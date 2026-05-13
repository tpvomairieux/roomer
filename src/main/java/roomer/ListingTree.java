/**
 * TreeMap object to store all user listings, supporting sorting by best time
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */

package roomer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import roomer.interfaces.Listing;

public class ListingTree {
    private final TreeMap<LocalDateTime, LinkedHashMap<String, Listing>> tree = new TreeMap<>();

    private final HashMap<String, LocalDateTime> emailIndex = new HashMap<>();

    private int size = 0;

    public void add(Listing listing) {
        if (listing == null) {
            throw new NullPointerException("Listing must not be null");
        }

        if (emailIndex.containsKey(listing.getEmail())) {
            throw new IllegalArgumentException("User: " + listing.getEmail() + " already has listing.");
        }

        tree.computeIfAbsent(listing.getDrawTime(), k -> new LinkedHashMap<>())
                .put(listing.getEmail(), listing);

        emailIndex.put(listing.getEmail(), listing.getDrawTime());
        size++;
    }

    public Listing remove(String email) {
        LocalDateTime key = emailIndex.remove(email);
        if (key == null)
            return null;

        LinkedHashMap<String, Listing> bucket = tree.get(key);
        Listing removed = bucket.remove(email);

        if (bucket.isEmpty())
            tree.remove(key);
        size--;
        return removed;
    }

    public Listing find(String email) {
        LocalDateTime key = emailIndex.get(email);
        if (key == null)
            return null;
        return tree.get(key).get(email);
    }

    public List<Listing> allSorted() {
        List<Listing> result = new ArrayList<>(size);
        for (LinkedHashMap<String, Listing> bucket : tree.values()) {
            result.addAll(bucket.values());
        }
        return Collections.unmodifiableList(result);
    }

    public boolean containsEmail(String email) {
        return emailIndex.containsKey(email);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return "ListingTree{size=" + size + ", listings=" + allSorted() + "}";
    }
}