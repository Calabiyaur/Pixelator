package main.java.util;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import main.java.meta.Point;
import main.java.meta.PointArray;

public class ShapeUtil {

    /**
     * Return all points which lie between (x1, y1) and (x2, y2),
     * in a straight line. End points included.
     */
    public static PointArray getLinePoints(Point p1, Point p2) {
        PointArray points = new PointArray();
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();

        Integer yDiff = y1 - y2;
        int ySign = yDiff < 0 ? 1 : -1;
        Integer xDiff = x1 - x2;
        int xSign = xDiff < 0 ? 1 : -1;
        if (yDiff == 0 && xDiff == 0) {
            points.add(x1, y1);
            return points;
        }
        Double slope = yDiff.doubleValue() / xDiff.doubleValue();

        if (Math.abs(slope) <= 1) {
            int x, y;
            for (int i = 0; i <= Math.abs(xDiff); i++) {
                x = x1 + xSign * i;
                y = y1 + (int) Math.round(ySign * i * Math.abs(slope));
                points.add(x, y);
            }
        } else {
            int x, y;
            for (int i = 0; i <= Math.abs(yDiff); i++) {
                x = x1 + (int) Math.round(xSign * i / Math.abs(slope));
                y = y1 + ySign * i;
                points.add(x, y);
            }
        }

        return points;
    }

    /**
     * Return all points that are directly connected to the
     * starting point through color.
     *
     * @param point the starting point
     * @param cStart color of the starting point
     */
    public static PointArray getFillPoints(Point point, Color cStart, PixelReader reader, int width, int height) {

        PointArray result = new PointArray();

        Boolean[][] activeMap = new Boolean[width][height];
        activeMap[point.getX()][point.getY()] = true;
        PointArray activeSet = new PointArray();
        PointArray newActiveSet = new PointArray();
        activeSet.add(point.getX(), point.getY());

        boolean done = false;
        while (!done) {
            done = true;
            newActiveSet.reset();
            for (int i = 0; i < activeSet.size(); i++) {
                int x = activeSet.getX(i);
                int y = activeSet.getY(i);
                if (activeMap[x][y]) {

                    result.add(x, y);

                    for (int j = 0; j < 4; j++) {

                        int newX = j % 2 == 1 ? x : x + 1 - j;
                        int newY = j % 2 == 0 ? y : y - 2 + j;

                        try {
                            if (activeMap[newX][newY] == null) {
                                if (cStart.equals(reader.getColor(newX, newY))) {
                                    activeMap[newX][newY] = true;
                                    newActiveSet.add(newX, newY);
                                    done = false;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            // no new points here ;)
                        }
                    }
                    activeMap[x][y] = false;
                }
            }
            activeSet = newActiveSet.clone();
        }
        return result;
    }

    /**
     * Return all points in the image that have the given color.
     */
    public static PointArray getPointsOfColor(Color color, PixelReader reader, int width, int height) {
        PointArray result = new PointArray();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (reader.getColor(i, j).equals(color)) {
                    result.add(i, j);
                }
            }
        }
        return result;
    }

    /**
     * Return all points that lie between (x1, y1) and (x2, y2),
     * in a rectangular shape.
     */
    public static PointArray getRectanglePoints(Point p1, Point p2, boolean fill) {
        PointArray points = new PointArray();
        int x1 = p1.getX() < p2.getX() ? p1.getX() : p2.getX();
        int y1 = p1.getY() < p2.getY() ? p1.getY() : p2.getY();
        int x2 = p1.getX() < p2.getX() ? p2.getX() : p1.getX();
        int y2 = p1.getY() < p2.getY() ? p2.getY() : p1.getY();

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (fill || x == x1 || x == x2 || y == y1 || y == y2) {
                    points.add(x, y);
                }
            }
        }
        return points;
    }

    /**
     * Return all points that lie between (x1, y1) and (x2, y2),
     * in an elliptic shape.
     */
    public static PointArray getEllipsePoints(Point p1, Point p2, boolean fill) {
        PointArray points = new PointArray();
        int cx = (p1.getX() + p2.getX());
        int cy = (p1.getY() + p2.getY());
        int rx = Math.abs(p1.getX() - p2.getX());
        int ry = Math.abs(p1.getY() - p2.getY());

        if (rx == 0 || ry == 0) {
            return getLinePoints(p1, p2);
        }

        int a = 2 * rx * rx;
        int b = 2 * ry * ry;
        int x = rx;
        int y = 0;
        int dx = ry * ry * (1 - 2 * rx);
        int dy = rx * rx;
        int ee = 0;
        int sx = b * rx;
        int sy = 0;

        while (sx >= sy) {
            points.add((int) Math.floor((cx + x) / 2f), (int) Math.floor((cy + y) / 2f));
            points.add((int) Math.ceil((cx - x) / 2f), (int) Math.floor((cy + y) / 2f));
            points.add((int) Math.ceil((cx - x) / 2f), (int) Math.ceil((cy - y) / 2f));
            points.add((int) Math.floor((cx + x) / 2f), (int) Math.ceil((cy - y) / 2f));

            y += 2;
            sy += 2 * a;
            ee += 2 * dy;
            dy += 2 * a;

            if ((2 * ee + dx > 0)) {
                x -= 2;
                sx -= 2 * b;
                ee += 2 * dx;
                dx += 2 * b;
            }
        }

        x = 0;
        y = ry;
        dx = ry * ry;
        dy = rx * rx * (1 - 2 * ry);
        ee = 0;
        sx = 0;
        sy = a * ry;

        while (sx <= sy) {
            points.add((int) Math.floor((cx + x) / 2f), (int) Math.floor((cy + y) / 2f));
            points.add((int) Math.ceil((cx - x) / 2f), (int) Math.floor((cy + y) / 2f));
            points.add((int) Math.ceil((cx - x) / 2f), (int) Math.ceil((cy - y) / 2f));
            points.add((int) Math.floor((cx + x) / 2f), (int) Math.ceil((cy - y) / 2f));

            x += 2;
            sx += 2 * b;
            ee += 2 * dx;
            dx += 2 * b;

            if ((2 * ee + dy) > 0) {
                y -= 2;
                sy -= 2 * a;
                ee += 2 * dy;
                dy += 2 * a;
            }
        }

        return points;
    }

    /**
     * Return all points that have a distance <= width to (x, y).
     * (Using the 1-norm)
     */
    public static PointArray getDiamondPoints(int x, int y, int width) {
        PointArray points = new PointArray();
        for (int i = x - width; i <= x + width; i++) {
            for (int j = y - width; j <= y + width; j++) {
                if (Math.abs(i - x) + Math.abs(j - y) <= width) {
                    points.add(i, j);
                }
            }
        }
        return points;
    }

}

//package main.java.util;
//
//        import javafx.scene.image.PixelReader;
//        import javafx.scene.paint.Color;
//
//        import main.java.standard.Point;
//        import main.java.standard.PointArray;
//
//public class ShapeUtil {
//
//    /**
//     * Return all points which lie between (x1, y1) and (x2, y2),
//     * in a straight line. End points included.
//     */
//    public static PointArray getLinePoints(Point p1, Point p2) {
//        PointArray points = new PointArray();
//        int x1 = p1.getX();
//        int y1 = p1.getY();
//        int x2 = p2.getX();
//        int y2 = p2.getY();
//
//        Integer yDiff = y1 - y2;
//        int ySign = yDiff < 0 ? 1 : -1;
//        Integer xDiff = x1 - x2;
//        int xSign = xDiff < 0 ? 1 : -1;
//        if (yDiff == 0 && xDiff == 0) {
//            points.add(x1, y1);
//            return points;
//        }
//        Double slope = yDiff.doubleValue() / xDiff.doubleValue();
//
//        if (Math.abs(slope) <= 1) {
//            int x, y;
//            for (int i = 0; i <= Math.abs(xDiff); i++) {
//                x = x1 + xSign * i;
//                y = y1 + (int) Math.round(ySign * i * Math.abs(slope));
//                points.add(x, y);
//            }
//        } else {
//            int x, y;
//            for (int i = 0; i <= Math.abs(yDiff); i++) {
//                x = x1 + (int) Math.round(xSign * i / Math.abs(slope));
//                y = y1 + ySign * i;
//                points.add(x, y);
//            }
//        }
//
//        return points;
//    }
//
//    /**
//     * Return all points that are directly connected to the
//     * starting point through color.
//     *
//     * @param point the starting point
//     * @param cStart color of the starting point
//     */
//    public static PointArray getFillPoints(Point point, Color cStart, PixelReader reader, int width, int height) {
//
//        PointArray result = new PointArray();
//
//        Boolean[][] activeMap = new Boolean[width][height];
//        activeMap[point.getX()][point.getY()] = true;
//        PointArray activeSet = new PointArray();
//        PointArray newActiveSet = new PointArray();
//        activeSet.add(point.getX(), point.getY());
//
//        boolean done = false;
//        while (!done) {
//            done = true;
//            newActiveSet.reset();
//            for (int i = 0; i < activeSet.size(); i++) {
//                int x = activeSet.getX(i);
//                int y = activeSet.getY(i);
//                if (activeMap[x][y]) {
//
//                    result.add(x, y);
//
//                    for (int j = 0; j < 4; j++) {
//
//                        int newX = j % 2 == 1 ? x : x + 1 - j;
//                        int newY = j % 2 == 0 ? y : y - 2 + j;
//
//                        try {
//                            if (activeMap[newX][newY] == null) {
//                                if (cStart.equals(reader.getColor(newX, newY))) {
//                                    activeMap[newX][newY] = true;
//                                    newActiveSet.add(newX, newY);
//                                    done = false;
//                                }
//                            }
//                        } catch (IndexOutOfBoundsException e) {
//                            // no new points here ;)
//                        }
//                    }
//                    activeMap[x][y] = false;
//                }
//            }
//            activeSet = newActiveSet.clone();
//        }
//        return result;
//    }
//
//    /**
//     * Return all points in the image that have the given color.
//     */
//    public static PointArray getPointsOfColor(Color color, PixelReader reader, int width, int height) {
//        PointArray result = new PointArray();
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                if (reader.getColor(i, j).equals(color)) {
//                    result.add(i, j);
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * Return all points that lie between (x1, y1) and (x2, y2),
//     * in a rectangular shape.
//     */
//    public static PointArray getRectanglePoints(Point p1, Point p2, boolean fill) {
//        PointArray points = new PointArray();
//        int x1 = p1.getX() < p2.getX() ? p1.getX() : p2.getX();
//        int y1 = p1.getY() < p2.getY() ? p1.getY() : p2.getY();
//        int x2 = p1.getX() < p2.getX() ? p2.getX() : p1.getX();
//        int y2 = p1.getY() < p2.getY() ? p2.getY() : p1.getY();
//
//        for (int x = x1; x <= x2; x++) {
//            for (int y = y1; y <= y2; y++) {
//                if (fill || x == x1 || x == x2 || y == y1 || y == y2) {
//                    points.add(x, y);
//                }
//            }
//        }
//        return points;
//    }
//
//    /**
//     * Return all points that lie between (x1, y1) and (x2, y2),
//     * in an elliptic shape.
//     */
//    public static PointArray getEllipsePoints(Point p1, Point p2, boolean fill) {
//        PointArray points = new PointArray();
//        int cx = (p1.getX() + p2.getX()) / 2;
//        int cy = (p1.getY() + p2.getY()) / 2;
//        int rx = Math.abs(p1.getX() - p2.getX()) / 2;
//        int ry = Math.abs(p1.getY() - p2.getY()) / 2;
//
//        if (rx == 0 || ry == 0) {
//            return getLinePoints(p1, p2);
//        }
//
//        int a = 2 * rx * rx;
//        int b = 2 * ry * ry;
//        int x = rx;
//        int y = 0;
//        int dx = ry * ry * (1 - 2 * rx);
//        int dy = rx * rx;
//        int ee = 0;
//        int sx = b * rx;
//        int sy = 0;
//
//        while (sx >= sy) {
//            points.add(cx + x, cy + y);
//            points.add(cx - x, cy + y);
//            points.add(cx - x, cy - y);
//            points.add(cx + x, cy - y);
//
//            y++;
//            sy += a;
//            ee += dy;
//            dy += a;
//
//            if ((2 * ee + dx > 0)) {
//                x--;
//                sx -= b;
//                ee += dx;
//                dx += b;
//            }
//        }
//
//        x = 0;
//        y = ry;
//        dx = ry * ry;
//        dy = rx * rx * (1 - 2 * ry);
//        ee = 0;
//        sx = 0;
//        sy = a * ry;
//
//        while (sx <= sy) {
//            points.add(cx + x, cy + y);
//            points.add(cx - x, cy + y);
//            points.add(cx - x, cy - y);
//            points.add(cx + x, cy - y);
//
//            x++;
//            sx += b;
//            ee += dx;
//            dx += b;
//
//            if ((2 * ee + dy) > 0) {
//                y--;
//                sy -= a;
//                ee += dy;
//                dy += a;
//            }
//        }
//
//        return points;
//    }
//
//    /**
//     * Return all points that have a distance <= width to (x, y).
//     * (Using the 1-norm)
//     */
//    public static PointArray getDiamondPoints(int x, int y, int width) {
//        PointArray points = new PointArray();
//        for (int i = x - width; i <= x + width; i++) {
//            for (int j = y - width; j <= y + width; j++) {
//                if (Math.abs(i - x) + Math.abs(j - y) <= width) {
//                    points.add(i, j);
//                }
//            }
//        }
//        return points;
//    }
//
//}
