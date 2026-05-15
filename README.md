## Intro
 
Roomer is a Pomona College housing platform designed to make room draw less opaque and easier to navigate. It combines a searchable room directory, historical room-selection data, a draw-time exchange, a marketplace for listed draw times, and a valuation layer that helps students reason about how valuable different draw times are. The project responds to a local campus problem: students often have unequal access to information about rooms, historical selection patterns, and informal exchange opportunities, even though those factors can meaningfully affect where they live.


## Needfinding

Our initial idea was to build a housing tool that made Pomona room draw feel less like a guessing game. We wanted students to be able to search rooms, compare dorm attributes, see historical selection information, and understand how their draw time affected their practical options. As we talked through the problem, we also became interested in the informal draw-time swapping that already happens through friends (the $1000 Sontag Incident in the CS Lounge), because that behavior suggested an unmet coordination need.

We spoke informally with rising juniors for the most-part, as they were the primary audience impacted by the demo of OldenBorg, students who had recently received later draw times than expected (ex: one of my mentees and his 9:18 PM registration time), students planning with roommate groups, and students who had heard about or considered draw-time swaps. We did not need real names for the project; the useful descriptors were their position in the housing process and the kind of uncertainty they faced.

Students were not only frusturated by bad draw times, but they were also frustrated by not knowing what a given draw time meant. A student might know they had a late slot, but not know which buildings were realistically still possible, whether AC or square footage tradeoffs mattered, or whether a friend with an earlier time could help them. This is because HRL only informs students of the last available draw time for a room type for a dorm (ex: Walker singles), rather than a room itself. Another takeaway was that informal swapping is inefficient and socially uneven. Students with larger networks hear about more opportunities, while students outside those networks have fewer options even if they might be willing to trade or pay for a better selection position.

Those findings pushed Roomer toward three connected features. First, the room directory addresses the information problem by making room data searchable and filterable. Second, the draw-time exchange addresses the coordination problem by making direct swaps explicit and checkable. Third, the marketplace and valuation features address the decision problem by helping users compare listings and estimate the relative value of a draw time.

Some problems we found are best not solved by software. Roomer should not decide who deserves housing, replace institutional housing policy, or pressure students into monetizing a basic need-- this harms low-income and FLI students the most, or greatly benefits students with better financial conditions. It also cannot solve scarcity created by a limited number of desirable rooms or by dorm closures. Those are policy and resource-allocation problems. Software can make information and coordination clearer, but it should not pretend that a market interface makes the underlying housing constraints fair. 


## Dataset Description

Roomer uses a cleaned CSV file, `master.csv`, derived from the group housing data spreadsheet. The CSV contains one row per room-like entry and stores square footage, the raw room name, historical draw-selection times from 2023, 2024, and 2025, occupancy, and AC status. The project also includes `CS62 Final Project Data.xlsx` as the source spreadsheet and `rooms.json` as a frontend-friendly JSON representation of the room inventory. Originally, we used Apache POI with an excel file, but we ended up choosing CSV because it is simple, portable, and easy to load with standard Java file I/O. A CSV file also made sense for this project because the data is tabular: each row is a room and each column is an attribute. `RoomDataLoader` reads the CSV line by line, skips incomplete rows, parses numeric fields and dates, and creates `Room` objects stored in a `Rooms` collection. The frontend JSON file is useful for browser display because JavaScript can parse it directly without additional conversion. The data used to develop and test Feature 2 consists of three seeded demo users initialized in the seedUsers() method of RoomerServer on startup. Each user is represented by a User object with an email address, a password, a draw time stored as a LocalDateTime, and a balance stored as a double. The three demo users are alicia.park@pomona.edu with a draw time of April 8, 2025 at 5:03 PM, bryson.young@pomona.edu with a draw time of April 8, 2025 at 6:42 PM, and carol.rivera@pomona.edu with a draw time of April 9, 2025 at 7:06 PM. Alicia has the earliest draw time and Carol has the latest, which makes them useful for verifying that the exchange correctly identifies which draw times are more desirable and that the sorted order in ListingTree reflects chronological order from earliest to latest. All exchange and marketplace operations in the GUI are demonstrated using these three users.


## Implementation Overview


Roomer is written in Java with a small browser frontend. The backend stores room information in domain classes under `roomer.interfaces`, loads CSV data through `RoomDataLoader`, manages draw-time listings through `DrawExchange` and `ListingTree`, and exposes browser-accessible JSON endpoints through `RoomerServer`. The frontend in `index.html` uses plain HTML, CSS, and JavaScript to present tabs for browsing rooms, searching, exchanging draw times, and using the marketplace. Ronnie originally set up the front-end template with Claude Code in HTML, and when Phu revised it to fix bugs and styling, he programmed it himself as he has prior experience with front end development.

The server uses Java's built-in `com.sun.net.httpserver` library rather than an external web framework. On startup, `RoomerServer` loads room data, creates demo users, initializes `DrawExchange`, registers endpoint handlers, and serves `index.html` at `http://localhost:8080`. Each handler reads URL query parameters, calls the appropriate backend method, serializes the result into JSON, and sends a response to the browser.

The draw time exchange allows registered users to interact with their draw times in two ways. The first is a direct trade, where two users swap their draw times with each other at no cost. The second is a marketplace purchase, where a user posts their draw time at a set price and another user with sufficient account balance buys it, receiving the seller's draw time in return. Both interactions are managed by DrawExchange.java, which uses ListingTree as its backing data structure.

ListingTree wraps a TreeMap keyed on LocalDateTime, which sorts all listings by draw time automatically using Java's natural ordering on LocalDateTime. Because LocalDateTime implements Comparable, the TreeMap maintains chronological order at all times without any additional sorting step. Alongside the TreeMap, ListingTree maintains a HashMap email index that maps each user's email to their draw time key in the tree. This index allows individual listings to be located and removed in constant time without traversing the tree. The combination of the TreeMap for sorted retrieval and the HashMap for fast individual access is what makes ListingTree well suited to the operations DrawExchange needs to perform.

When a user calls post(), DrawExchange first checks the email index to confirm the user does not already have an active listing. If they do, the method returns null. Otherwise, it creates a new Listing object with the user's email, draw time, and price, and inserts it into the tree. canTrade() performs two lookups in the email index to confirm both users currently have active listings, returning true only if both are present. executeTrade() calls canTrade() as a precondition, then retrieves both User objects from the Users HashMap, stores one draw time in a temporary variable, swaps the two draw times using setTime(), and removes both listings from the tree. purchase() locates the seller's listing through the email index, retrieves both User objects, verifies the buyer's balance is greater than or equal to the listing price, deducts the price from the buyer's balance, credits the seller, updates the buyer's draw time to the seller's, and removes the seller's listing. On any failed precondition, both executeTrade() and purchase() return false without modifying any state.

While not the hardest method from a data structures perspective, Valuation was the most difficult section in terms of design. Essentially, we wanted to give an estimate of how much each time slot in the tree should be, which posed questions, such as how much each timeslot should be, and how to calculate it.

To begin, we needed to bucket all times into 1 of 4 categories: Sontag/Dialynis, Senior, Junior, and Sophomore times. Sontag and Dialynis are the most popular dorms and the most sought after, frequently being filled up in the first 24 minutes of senior housing draw, and after reading social media posts/conducting interviews, we concluded that many students would be willing to pay a premium price to live in these dorms. Thus, a section of Senior times become Sontag/Dialynis times if a Senior time is before 5:24 (which is when it historically runs out).  After that, senior times are more valuable than junior times, since seniors draw the day before juniors (and juniors before sophomores). 

However, we don't have the actual dates for future room draws. Thus, to actually calculate it, we have the method inferBoundaries() which extracts all unique calendar dates found in the listing tree, sorts them, and assigns:
days[0] → senior day
days[1] → junior day
days[2] → sophomore day

For dates that may be outside of these parameters, we simply assign them to either senior (Sontag/Dialynis), or sophomore, depending on whether the date is before/after the 3 draw dates. This is not the best solution, but we assume that students do not misrepresent their information, meaning students’ drawtime date should only be one of three dates. 

The next challenge comes from estimating values - although some people are willing to pay 2000 for a “good” senior time, what constitutes good? In addition, how much would people be willing to pay for an “okay” senior time? What about juniors and sophomores? Based on our interviews and searching on social media, we roughly came up with the following pricing boundaries:

2400 to 2100 for Sontag/Dialynas
2000 to 1500 for Senior
1500 to 1000 for Junior
1000 to 500 for Sophomore

The upper bounds are generally more accurate than lower bounds (simply because more information was available), but the logic was that if students are willing to offer the ceiling price for a time, they would likely offer the same price for the floor price of the prior date (for ex, if someone wanted a 5:00 pm junior time, they would likely be willing to pay the same amount for a senior 10:30 pm time, since their room selection options would be roughly the same). 


To calculate valuations, we used the formula:
Multiplier = minutes after start / minutes from start to end
Value = ceiling - multiplier * (ceiling - floor)

## Data Structure Justification

The room directory is backed by a `HashMap` from cleaned room name to `Room` object. This makes direct search by room name efficient because a room can be retrieved in average-case O(1) time. The tradeoff is that filters such as building, occupancy, AC, and square-footage range require scanning all rooms, but that is acceptable for this project because filters naturally ask a question about the full inventory and the dataset size is manageable.

The draw-time exchange uses `ListingTree`, which combines a `TreeMap` and a `HashMap`. The `TreeMap` is keyed by `LocalDateTime` and stores listing buckets in chronological order, so marketplace listings can be displayed from earliest draw time to latest without sorting from scratch. The `HashMap` email index maps each owner email to its draw-time key, which allows fast checks for duplicate listings and fast lookup/removal by user email. This hybrid structure matches the feature needs better than either structure alone: the tree gives sorted order, while the hash index gives direct access.

The valuation feature reuses the sorted listing data from `ListingTree`. It infers draw-day boundaries from the distinct dates currently present in the listings, classifies draw times into Sontag/Dialynas, senior, junior, or sophomore buckets, and computes a continuous score within each bucket using linear interpolation across the selection window. This design keeps valuation close to the marketplace data it explains, rather than maintaining a separate duplicated ordering structure.

## Screenshots

Please see the screenshots folder of our repo. 

## Analysis

### Feature 1: Room Directory and Search

This feature allows users to browse all rooms, search for a room by cleaned name, and filter the inventory by attributes such as building, occupancy, AC, and square footage.

Preconditions: `master.csv` must exist, have the expected columns, and be readable. Room names must be processable into a known `Building` enum value. For browser use, the frontend/server must be available.

Edge cases: blank rows, missing historical draw times, malformed dates, unknown buildings, suite names with multiple hyphens, rooms with occupancy 0, invalid search strings, and filters that return no matches.

### Feature 2: Draw-Time Exchange

This feature lets users post draw times and perform direct swaps when both users have active listings.

Preconditions: both users must exist in `Users`. To trade, both must have active listings in `ListingTree`. A user cannot post more than one active listing at the same time.

Edge cases: duplicate posts, one or both users not posted, nonexistent user emails, same user entered twice, identical draw times, failed trade attempts, and reposting after a completed trade.

### Feature 3: Marketplace and Valuation

This feature lets a user post a draw time with a price, lets another user purchase it if they have enough balance, and displays valuation information for listings.

Preconditions: the seller must have an active listing. Buyer and seller must exist. The buyer must have a balance greater than or equal to the listing price. Valuation requires enough distinct draw dates to infer class-year boundaries.

Edge cases: insufficient balance, seller with no listing, buyer or seller not found, price 0 listings used as trade-only listings, negative or malformed prices from the URL, and too few distinct draw dates for valuation.

### GUI and Server Layer

Preconditions: `RoomerServer` must be running for exchange endpoints. The browser must be able to reach `localhost:8080`. Query parameters must use expected names and values.

Edge cases: missing query parameters, invalid enum values, server not running, frontend/backend state becoming stale after a trade or purchase, and JSON output containing unexpected values.


## Time and Space Complexity

Please see the attached tables in Time and Space Complexities Folder. 

## Testing

Testing for `DrawExchange` is implemented in `DrawExchangeTest.java` using print-based tests run from the `main` method. A `setup()` helper creates fresh `DrawExchange` and `Users` instances before each test so state does not leak between cases. The tests cover normal posting, duplicate post rejection, sorted listing order, trade checks, successful trades, failed trades, reposting after trade, successful purchases, exact-balance purchases, money transfer, buyer draw-time updates, insufficient-balance failure, unchanged balances after failure, and missing seller listings.

`RoomDataLoader`, `Rooms`, and `Room` each include `main`-method checks that print representative loading, parsing, searching, filtering, and formatting behavior. These tests are useful for screenshots because they demonstrate the data pipeline from CSV rows into `Room` objects and then into searchable/filterable collections. The GUI can also be tested manually by running `RoomerServer` and using each tab in the browser.

## Reflection

Overall, this project took the group a lot of time. The scope grew as we moved from the original room-directory idea into exchange, marketplace, valuation, and GUI work, maybe actually to a point of nearing our capabilites as programmers and exceeding it at some points :c. We mostly abided by our grading contract, but the integration effort was larger than expected because each feature depended on shared model classes and package organization.

The process was most difficult when separate pieces of the project had to be merged. Package names, imports, interfaces, and assumptions about where data lived caused late-stage compilation and integration issues. Working through those issues made the architecture clearer: `RoomDataLoader` owns CSV conversion, `Rooms` owns room storage and filtering, `ListingTree` owns sorted active listings, `DrawExchange` owns exchange behavior, `Valuation` owns scoring, and `RoomerServer` connects the backend to the browser.

We did not stick exactly with the original data-structure plan. The grading contract proposed HashMaps for the draw exchange and considered a balanced BST or priority queue for marketplace ranking. The final implementation uses a hybrid `ListingTree` with a `TreeMap` plus `HashMap` email index. This was a better fit because marketplace display needed chronological sorted order, while exchange operations still needed direct lookup by user email. We also implemented valuation as a scoring layer over the sorted listings instead of a full auction system, which kept the feature realistic for the project timeline.

If we had more time, we would strengthen the frontend/backend boundary, add real authentication, serve all frontend data consistently through the Java server, support persistent storage instead of seeded demo users, and add formal unit tests through JUnit tests. We would also want more user testing on the ethical framing of marketplace features, because the technical implementation is only one part of whether this tool should exist in a campus housing process.

One piece of feedback on the final project assignment is that the staged grading contract was helpful for planning, but it was difficult to know early which dataset and interfaces would survive the whole project. A little more flexibility in the early checkpoint requirements would help groups avoid locking into premature details before needfinding and data availability are settled (as said in the Project Check-In).

## Implementation Note

The GUI is implemented with plain HTML, CSS, and JavaScript in `index.html`, and the backend server uses Java's built-in HTTP server. Partial GUI development and stylistic support were assisted by Claude/Anthropic as documented in the source comments. The core data structures, backend behavior, and analysis in this writeup correspond to the project code in the public repository linked on the cover page.
