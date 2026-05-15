/**
 * Valuation estimates for posted times. See README.md for more details
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */

package roomer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import roomer.interfaces.Listing;

public class Valuation {

    private static final LocalTime start = LocalTime.of(17, 0); // Draws start at 5:00 pm
    private static final LocalTime end = LocalTime.of(22, 30); // Draws end at 10:30 pm
    private static final LocalTime sdCutoff = LocalTime.of(17, 24); // Sontag Dialynis cutoff at 5:24 pm

    private static final long window = java.time.Duration.between(start, end).toMinutes();
    private static final long sdWindow = java.time.Duration.between(start, sdCutoff).toMinutes();

    public enum Bucket {
        SNTG_DIAL("Sontag/Dialynis", 2400),
        SENIOR("Senior Time", 2000),
        JUNIOR("Junior Time", 1500),
        SOPHOMORE("Sophomore Time", 1000);

        private final String label;
        private final double val;

        Bucket(String label, double val) {
            this.label = label;
            this.val = val;
        }

        public String getLabel() {
            return label;
        }

        public double getValue() {
            return val;
        }
    }

    // Inferred values - see README.md
    private LocalDate seniorDay;
    private LocalDate juniorDay;
    private LocalDate sophomoreDay;

    /**
     * Evaluates every listing in the tree and returns a map of owner email ->
     * valuation
     *
     * @param tree the populated ListingTree
     * @return map of ownerEmail to valuation result
     * @throws IllegalStateException if fewer than 2 distinct draw dates are found
     */
    public Map<String, Result> evaluate(ListingTree tree) {
        List<Listing> all = tree.allSorted();
        if (all.isEmpty())
            return Collections.emptyMap();

        inferBoundaries(all);

        Map<String, Result> results = new LinkedHashMap<>();
        for (Listing l : all) {
            Bucket bucket = classify(l.getDrawTime());
            double score = score(l.getDrawTime());
            results.put(l.getEmail(), new Result(l, bucket, score));
        }
        return results;
    }

    /**
     * Classifies User drawtime date as either senior, junior, sophmore, or
     * sontag/dialynis based on inferred senior, junior, and sophomore dates
     *
     * @throws IllegalStateException if boundaries have not been inferred yet
     */
    public Bucket classify(LocalDateTime drawTime) {
        if (seniorDay == null)
            throw new IllegalStateException("Call evaluate() first.");
        LocalDate day = drawTime.toLocalDate();

        if (day.equals(seniorDay)) {
            if (drawTime.toLocalTime().isBefore(sdCutoff)) {
                return Bucket.SNTG_DIAL;
            }
            return Bucket.SENIOR;
        }
        if (day.equals(juniorDay))
            return Bucket.JUNIOR;
        if (day.equals(sophomoreDay))
            return Bucket.SOPHOMORE;

        if (day.isBefore(seniorDay)) // Usually will not end up with these cases - see README
            return Bucket.SNTG_DIAL;
        return Bucket.SOPHOMORE;
    }

    /**
     * Returns a continuous score for a draw time within its bucket.
     */
    public double score(LocalDateTime drawTime) {
        Bucket bucket = classify(drawTime);
        double ceiling = bucket.getValue();
        double floor;
        double multiplier;

        if (bucket.getLabel().equals("Sontag/Dialynis")) {
            floor = 2100;
        } else {
            floor = ceiling - 500;
        }

        LocalTime time = drawTime.toLocalTime();

        if (!time.isAfter(start)) {
            return ceiling;
        }
        if (!time.isBefore(end))
            return floor;

        long minutesIn = java.time.Duration.between(start, time).toMinutes();

        if (bucket.getLabel().equals("Sontag/Dialynis")) {
            multiplier = (double) minutesIn / sdWindow; // 0.0 → 1.0
        } else {
            multiplier = (double) minutesIn / window; // 0.0 → 1.0
        }

        return ceiling - multiplier * (ceiling - floor); // linear interpolation
    }

    /**
     * Tries to infer which date a drawtime date belongs to
     * 
     * @param listings // Sorted list of all listings
     */
    private void inferBoundaries(List<Listing> listings) {
        List<LocalDate> days = listings.stream()
                .map(l -> l.getDrawTime().toLocalDate()).distinct().sorted().collect(Collectors.toList());

        if (days.size() < 2) {
            throw new IllegalStateException(
                    "Warning: Need at least 2 distinct days to infer senior/junior/sophmore draw date");
        }

        seniorDay = days.get(0);
        juniorDay = days.get(1);
        sophomoreDay = days.size() >= 3 ? days.get(2) : null;
    }

    public LocalDate getSeniorDay() {
        return seniorDay;
    }

    public LocalDate getJuniorDay() {
        return juniorDay;
    }

    public LocalDate getSophomoreDay() {
        return sophomoreDay;
    }

    /**
     * Holds the valuation outcome for a single listing.
     */
    public static final class Result {
        private final Listing listing;
        private final Bucket bucket;
        private final double score;

        Result(Listing listing, Bucket bucket, double score) {
            this.listing = listing;
            this.bucket = bucket;
            this.score = score;
        }

        public double getScore() {
            return score;
        }

        public Bucket getBucket() {
            return bucket;
        }

        @Override
        public String toString() {
            String output = listing.getEmail() + " " + bucket.getLabel() + " " + Math.round(score) + " "
                    + listing.getDrawTime();
            return output;
        }
    }

    // ── Demo ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        ListingTree tree = new ListingTree();

        // Senior day: Apr 8
        tree.add(new Listing("alicia.park@pomona.edu", LocalDateTime.of(2026, 4, 8, 17, 0), 0));
        tree.add(new Listing("bryson.young@pomona.edu", LocalDateTime.of(2026, 4, 8, 17, 20), 0));
        tree.add(new Listing("carol.rivera@pomona.edu", LocalDateTime.of(2026, 4, 8, 17, 27), 0));
        tree.add(new Listing("dana.kim@pomona.edu", LocalDateTime.of(2026, 4, 8, 18, 0), 0));
        tree.add(new Listing("eli.santos@pomona.edu", LocalDateTime.of(2026, 4, 8, 19, 45), 0));

        // Junior day: Apr 9
        tree.add(new Listing("fiona.ng@pomona.edu", LocalDateTime.of(2026, 4, 7, 17, 15), 0));
        tree.add(new Listing("george.wu@pomona.edu", LocalDateTime.of(2026, 4, 8, 18, 0), 0));
        tree.add(new Listing("hana.patel@pomona.edu", LocalDateTime.of(2026, 4, 9, 17, 30), 0));

        // Sophomore day: Apr 10
        tree.add(new Listing("ivan.osei@pomona.edu", LocalDateTime.of(2026, 4, 10, 18, 0), 0));
        tree.add(new Listing("julia.chen@pomona.edu", LocalDateTime.of(2026, 4, 10, 20, 0), 0));

        Valuation val = new Valuation();
        Map<String, Result> results = val.evaluate(tree);

        System.out.println("=== Inferred draw schedule ===");
        System.out.println("  Senior day    : " + val.getSeniorDay());
        System.out.println("  Junior day    : " + val.getJuniorDay());
        System.out.println("  Sophomore day : " + val.getSophomoreDay());
        System.out.println("  Good-senior cutoff (fixed): before " + sdCutoff);

        System.out.println("\n=== Valuations ===");
        results.values().forEach(System.out::println);
    }
}