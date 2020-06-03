package com.calabi.pixelator.util.shape;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.view.ToolSettings;

public final class EllipseHelper {


    /**
     * Return all points that form an ellipse around (cx|cy) with radii rx and ry.
     */
    public static PointArray getEllipsePointsStretched(int cx, int cy, int rx, int ry, int sH, int sV,
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

        PointArray linePoints = ShapeMaster.getLinePoints(new Point(lx1, ly1), new Point(lx2, ly2), settings);
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

}
