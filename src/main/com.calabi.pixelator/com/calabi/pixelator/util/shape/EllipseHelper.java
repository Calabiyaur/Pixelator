package com.calabi.pixelator.util.shape;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;

public final class EllipseHelper {

    /**
     * Return all points that form an ellipse around (cx|cy) with radii rx and ry.
     */
    public static PointArray getEllipse(int cx, int cy, int rx, int ry, int stretchH, int stretchV, boolean fill) {
        PointArray stretchedPoints = getEllipsePointsStretched(cx, cy, rx, ry, fill);
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

    private static PointArray getEllipsePointsStretched(int cx, int cy, int rx, int ry, boolean fill) {

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
            addPointsToEllipse(points, cx, cy, x, y, fill, 0, false);
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
            addPointsToEllipse(points, cx, cy, x, y, fill, yOff, true);
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

        PointArray linePoints = LineHelper.getLinePoints(new Point(lx1, ly1), new Point(lx2, ly2));
        for (Point point : linePoints.getPoints()) {
            addPointsToEllipse(points, cx, cy, point.getX(), point.getY(), false, 0, false);
        }

        return points;
    }

    private static void addPointsToEllipse(PointArray points, int cx, int cy, int x, int y, boolean fill, int yOffset, boolean useYOffset) {
        if (!fill) {
            addPoint(points, cx + x, cy + y);
        } else {
            if (!useYOffset) {
                for (int i = cx; i < cx + x + 1; i++) {
                    addPoint(points, i, cy + y);
                }
            } else {
                for (int i = cy + yOffset; i < cy + y + 1; i++) {
                    addPoint(points, cx + x, i);
                }
            }
        }

        if (x != 0) {
            if (!fill) {
                addPoint(points, cx - x, cy + y);
            } else {
                if (!useYOffset) {
                    for (int i = cx - x; i < cx; i++) {
                        addPoint(points, i, cy + y);
                    }
                } else {
                    for (int i = cy + yOffset; i < cy + y + 1; i++) {
                        addPoint(points, cx - x, i);
                    }
                }
            }
        }

        if ((x != 0 || y != 0) && (x == 0 || y != 0)) {
            if (!fill) {
                addPoint(points, cx - x, cy - y);
            } else {
                if (!useYOffset) {
                    for (int i = cx - x; i < cx; i++) {
                        addPoint(points, i, cy - y);
                    }
                } else {
                    for (int i = cy - y; i < cy - yOffset; i++) {
                        addPoint(points, cx - x, i);
                    }
                }
            }
        }

        if (x != 0 && y != 0) {
            if (!fill) {
                addPoint(points, cx + x, cy - y);
            } else {
                if (!useYOffset) {
                    for (int i = cx; i < cx + x + 1; i++) {
                        addPoint(points, i, cy - y);
                    }
                } else {
                    for (int i = cy - y; i < cy - yOffset; i++) {
                        addPoint(points, cx + x, i);
                    }
                }
            }
        }
    }

    private static void addPoint(PointArray points, int x, int y) {
        if (x >= 0 && y >= 0) {
            points.add(x, y);
        }
    }

}
