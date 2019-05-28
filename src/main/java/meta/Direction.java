package main.java.meta;

import javafx.scene.Cursor;

public enum Direction {

    NONE(null),
    EAST(90d),
    NORTH_EAST(45d),
    NORTH(0d),
    NORTH_WEST(315d),
    WEST(270d),
    SOUTH_WEST(225d),
    SOUTH(180d),
    SOUTH_EAST(135d);

    private Double rotate;

    Direction(Double rotate) {
        this.rotate = rotate;
    }

    public static Direction getDirection(boolean east, boolean north, boolean west, boolean south) {
        if (east && west || north && south) {
            return null;
        }
        if (east) {
            if (north) {
                return NORTH_EAST;
            } else if (south) {
                return SOUTH_EAST;
            } else {
                return EAST;
            }
        } else if (west) {
            if (north) {
                return NORTH_WEST;
            } else if (south) {
                return SOUTH_WEST;
            } else {
                return WEST;
            }
        } else if (north) {
            return NORTH;
        } else if (south) {
            return SOUTH;
        }
        return null;
    }

    public static Direction getDirection(Cursor cursor) {
        if (cursor == null) {
            return NONE;
        }
        switch(cursor.toString()) {
            case "NW_RESIZE":
                return NORTH_WEST;
            case "N_RESIZE":
                return NORTH;
            case "NE_RESIZE":
                return NORTH_EAST;
            case "E_RESIZE":
                return EAST;
            case "SE_RESIZE":
                return SOUTH_EAST;
            case "S_RESIZE":
                return SOUTH;
            case "SW_RESIZE":
                return SOUTH_WEST;
            case "W_RESIZE":
                return WEST;
            default:
                return NONE;
        }
    }

    public boolean isSimple() {
        switch(this) {
            case EAST:
            case NORTH:
            case WEST:
            case SOUTH:
                return true;
            case NONE:
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_WEST:
            case SOUTH_EAST:
            default:
                return false;
        }
    }

    public boolean isEast() {
        return this == EAST || this == NORTH_EAST || this == SOUTH_EAST;
    }

    public boolean isNorth() {
        return this == NORTH || this == NORTH_EAST || this == NORTH_WEST;
    }

    public boolean isWest() {
        return this == WEST || this == NORTH_WEST || this == SOUTH_WEST;
    }

    public boolean isSouth() {
        return this == SOUTH || this == SOUTH_EAST || this == SOUTH_WEST;
    }

    public boolean isVertical() {
        switch(this) {
            case NORTH:
            case SOUTH:
                return true;
            default:
                return false;
        }
    }

    public boolean isHorizontal() {
        switch(this) {
            case EAST:
            case WEST:
                return true;
            default:
                return false;
        }
    }

    public boolean isDiagonal() {
        switch(this) {
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_EAST:
            case SOUTH_WEST:
                return true;
            default:
                return false;
        }
    }

    public Double getRotate() {
        return rotate;
    }

}
