package com.calabi.pixelator.meta;

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
        return switch(cursor.toString()) {
            case "NW_RESIZE" -> NORTH_WEST;
            case "N_RESIZE" -> NORTH;
            case "NE_RESIZE" -> NORTH_EAST;
            case "E_RESIZE" -> EAST;
            case "SE_RESIZE" -> SOUTH_EAST;
            case "S_RESIZE" -> SOUTH;
            case "SW_RESIZE" -> SOUTH_WEST;
            case "W_RESIZE" -> WEST;
            default -> NONE;
        };
    }

    public boolean isSimple() {
        return switch(this) {
            case EAST, NORTH, WEST, SOUTH -> true;
            default -> false;
        };
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
        return switch(this) {
            case NORTH, SOUTH -> true;
            default -> false;
        };
    }

    public boolean isHorizontal() {
        return switch(this) {
            case EAST, WEST -> true;
            default -> false;
        };
    }

    public boolean isDiagonal() {
        return switch(this) {
            case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST -> true;
            default -> false;
        };
    }

    public Double getRotate() {
        return rotate;
    }

}
