package main.java.standard;

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
        return isNorth() || isSouth();
    }

    public boolean isHorizontal() {
        return isEast() || isWest();
    }

    public Double getRotate() {
        return rotate;
    }

}
