/**
 * Object that represents a building
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer.interfaces;

/**
 * All buildings have constants to abide by: if they have air conditioning, and if they are north-campus or south campus. Room constants gathered from personal observation
 * 
 * and also the Pomona dorm information website, at the following: https://www.pomona.edu/administration/housing-residence-life/housing-selection-and-processes/residence-hall-descriptions.
 * 
 */
public enum Building {
    Lyon(false, false),
    Blaisdell(true, false),
    Mudd(true, false),
    Wig(false, false),
    Gibson(false, false),
    Harwood(false, false),
    Oldenborg(true, false),
    Smiley(false, false),
    ClarkI(false, true),
    ClarkIII(false, true),
    ClarkV(false, true),
    Lawry(false, true),
    Norton(false, true),
    Dialynas(true, true),
    Sontag(true, true),
    Walker(false, true);

    private final boolean ac;
    private final boolean north;

    Building(boolean ac, boolean north) {
        this.ac = ac;
        this.north = north;
    }

    public boolean hasAC() {
        return ac;
    }

    public boolean isNorth() {
        return north;
    }
}
