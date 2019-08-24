package com.calabi.pixelator.util;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.view.ToolSettings;

public class ShapeUtil {

    /**
     * Return all points which lie between (x1, y1) and (x2, y2),
     * in a straight line. End points included.
     */
    public static PointArray getLinePoints(Point p1, Point p2, ToolSettings settings) {
        Check.notNull(settings.thick);
        Check.notNull(settings.bulge);

        PointArray points = new PointArray();
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();

        int yDiff = y1 - y2;
        int ySign = yDiff < 0 ? 1 : -1;
        int xDiff = x1 - x2;
        int xSign = xDiff < 0 ? 1 : -1;
        if (yDiff == 0 && xDiff == 0) {
            points.add(x1, y1);
            return points;
        }
        double slope = (double) yDiff / (double) xDiff;

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
     * Return all points that lie between (x1, y1) and (x2, y2),
     * in a rectangular shape.
     */
    public static PointArray getRectanglePoints(Point p1, Point p2, ToolSettings settings) {
        PointArray points = new PointArray();
        int x1 = p1.getX() < p2.getX() ? p1.getX() : p2.getX();
        int y1 = p1.getY() < p2.getY() ? p1.getY() : p2.getY();
        int x2 = p1.getX() < p2.getX() ? p2.getX() : p1.getX();
        int y2 = p1.getY() < p2.getY() ? p2.getY() : p1.getY();

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (settings.fill || x == x1 || x == x2 || y == y1 || y == y2) {
                    points.add(x, y);
                }
            }
        }
        return points;
    }

    public static PointArray getEllipsePoints(Point p1, Point p2, ToolSettings settings) {
        // If width is odd, stretch by factor 2 to avoid half-ints
        int stretchH = (p1.getX() + p2.getX()) % 2 == 0 ? 1 : 2;
        int stretchV = (p1.getY() + p2.getY()) % 2 == 0 ? 1 : 2;
        int cx = (p1.getX() + p2.getX()) / (2 / stretchH);
        int cy = (p1.getY() + p2.getY()) / (2 / stretchV);
        int rx = Math.abs(p1.getX() - p2.getX()) / (2 / stretchH);
        int ry = Math.abs(p1.getY() - p2.getY()) / (2 / stretchV);

        if (rx == 0 || ry == 0) {
            return getLinePoints(p1, p2, settings);
        }
        if (Math.abs(p1.getX() - p2.getX()) == 1 || Math.abs(p1.getY() - p2.getY()) == 1) {
            return getRectanglePoints(p1, p2, settings);
        }
        PointArray stretchedPoints = getEllipsePointsStretched(cx, cy, rx, ry, stretchH, stretchV, settings);
        PointArray points = new PointArray();
        stretchedPoints.forEach((x, y) -> {
            if (x % stretchH == 0 && y % stretchV == 0) {
                points.add(x / stretchH, y / stretchV);
            } else if (x % stretchH == 0 || y % stretchV == 0) {
                double x1 = ((double) x / (double) stretchH);
                double y1 = ((double) y / (double) stretchV);
                double x2 = x > cx ? Math.floor(x1) : Math.ceil(x1);
                double y2 = y > cy ? Math.floor(y1) : Math.ceil(y1);
                points.add((int) x2, (int) y2);
            }
        });
        return points;
    }

    /**
     * Return all points that form an ellipse around (cx|cy) with radii rx and ry.
     */
    private static PointArray getEllipsePointsStretched(int cx, int cy, int rx, int ry, int sH, int sV,
            ToolSettings settings) {

        PointArray points = new PointArray();

        int a = 2 * rx * rx;
        int b = 2 * ry * ry;

        int x = rx;
        int y = 0;
        int dx = ry * ry * (1 - 2 * rx);
        int dy = rx * rx;
        int ee = 0;
        int sx = b * rx;
        int sy = 0;

        int lx1 = 0;
        int ly1 = 0;

        while (sx > sy) {
            addPointsToEllipse(points, cx, cy, x, y, settings.fill, 0);
            lx1 = x;
            ly1 = y;

            y += 1;
            sy += a;
            ee += dy;
            dy += a;

            if ((2 * ee + dx > 0)) {
                x -= 1;
                sx -= b;
                ee += dx;
                dx += b;
            }
        }

        int yOff = y - 1;

        x = 0;
        y = ry;
        dx = ry * ry;
        dy = rx * rx * (1 - 2 * ry);
        ee = 0;
        sx = 0;
        sy = a * ry;

        int lx2 = 0;
        int ly2 = 0;

        while (sx < sy) {
            addPointsToEllipse(points, cx, cy, x, y, settings.fill, yOff);
            lx2 = x;
            ly2 = y;

            x += 1;
            sx += b;
            ee += dx;
            dx += b;

            if ((2 * ee + dy) > 0) {
                y -= 1;
                sy -= a;
                ee += dy;
                dy += a;
            }
        }

        PointArray linePoints = getLinePoints(new Point(lx1, ly1), new Point(lx2, ly2), settings);
        for (int i = 1; i < linePoints.size() - 1; i++) {
            addPointsToEllipse(points, cx, cy, linePoints.getX(i), linePoints.getY(i), false, 0);
        }

        return points;
    }

    private static void addPointsToEllipse(PointArray points, int cx, int cy, int x, int y, boolean fill, int yOffset) {
        if (!fill) {
            points.add(cx + x, cy + y);
        } else {
            if (yOffset == 0) {
                for (int i = cx; i < cx + x + 1; i++) {
                    points.add(i, cy + y);
                }
            } else {
                for (int i = cy + yOffset; i < cy + y + 1; i++) {
                    points.add(cx + x, i);
                }
            }
        }

        if (x != 0) {
            if (!fill) {
                points.add(cx - x, cy + y);
            } else {
                if (yOffset == 0) {
                    for (int i = cx - x; i < cx; i++) {
                        points.add(i, cy + y);
                    }
                } else {
                    for (int i = cy + yOffset; i < cy + y + 1; i++) {
                        points.add(cx - x, i);
                    }
                }
            }
        }

        if ((x != 0 || y != 0) && (x == 0 || y != 0)) {
            if (!fill) {
                points.add(cx - x, cy - y);
            } else {
                if (yOffset == 0) {
                    for (int i = cx - x; i < cx; i++) {
                        points.add(i, cy - y);
                    }
                } else {
                    for (int i = cy - y; i < cy - yOffset; i++) {
                        points.add(cx - x, i);
                    }
                }
            }
        }

        if (x != 0 && y != 0) {
            if (!fill) {
                points.add(cx + x, cy - y);
            } else {
                if (yOffset == 0) {
                    for (int i = cx; i < cx + x + 1; i++) {
                        points.add(i, cy - y);
                    }
                } else {
                    for (int i = cy - y; i < cy - yOffset; i++) {
                        points.add(cx + x, i);
                    }
                }
            }
        }
    }

    /**
     * Return all points that have a distance <= radius to (x, y).
     * (Using the 1-norm)
     */
    public static PointArray getDiamondPoints(int x, int y, int radius) {
        PointArray points = new PointArray();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (Math.abs(i - x) + Math.abs(j - y) <= radius) {
                    points.add(i, j);
                }
            }
        }
        return points;
    }

    /**
     * Return all points that have a distance <= width / 2 to (x, y).
     * (Using the 2-norm)
     */
    public static PointArray getCirclePoints(int x, int y, int width) {
        PointArray points = new PointArray();
        if (width == 1) {
            points.add(x, y);
            return points;
        }
        int radius = width / 2;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2)) <= radius) {
                    points.add(i, j);
                }
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

}
