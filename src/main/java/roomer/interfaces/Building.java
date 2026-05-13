/**
 * Object that represents a building
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer.interfaces;

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
