package com.calabi.pixelator.util.shape;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.Rotate;
import com.calabi.pixelator.view.ToolSettings;

public final class ShapeMaster {

    /**
     * Return all points which lie between (x1, y1) and (x2, y2),
     * in a straight line. End points included.
     */
    public static PointArray getLinePoints(Point p1, Point p2, ToolSettings settings) {
        Check.notNull(settings.thick);
        Check.notNull(settings.bulge);

        PointArray line = LineHelper.getLinePoints(p1, p2);
        if (settings.thick == 1) {
            return line;
        }

        // Create mast to lay over each point in the line to create a thick line
        Point rotatedP2 = Rotate.rotateRight(p1, p2);
        PointArray mask = LineHelper.getStraightPath(p1, rotatedP2, settings.thick);

        // Move mask according to bulge
        int iOrigin = switch(settings.bulge) {
            case -1 -> 0;
            case 1 -> mask.size() - 1;
            default -> mask.size() / 2;
        };
        int xOrigin = mask.getX(iOrigin);
        int yOrigin = mask.getY(iOrigin);
        PointArray translatedMask = new PointArray();
        mask.forEach((x, y) -> translatedMask.add(x - xOrigin, y - yOrigin));

        // Combine line and mask
        PointArray thickLine = new PointArray();
        for (int i = 0; i < line.size(); i++) {
            for (int j = 0; j < translatedMask.size(); j++) {
                thickLine.add(line.getX(i) + translatedMask.getX(j), line.getY(i) + translatedMask.getY(j));
            }
        }
        return thickLine;
    }

    public static PointArray getRectanglePoints(Point p1, Point p2, ToolSettings settings) {
        PointArray points = RectangleHelper.getRectanglePoints(p1, p2, settings.fill);
        if (settings.thick == 1) {
            return points;
        }

        if (settings.bulge == -1 && settings.fill) {
            return points;
        }

        for (int i = 1; i < settings.thick; i++) {
            if (settings.bulge == -1 && Math.min(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getY() - p2.getY())) < i) {
                break;
            }

            int factor = switch(settings.bulge) {
                case -1 -> -i;
                case 1 -> i;
                default -> i <= settings.thick / 2 ? i : -i + settings.thick / 2;
            };
            int dX = p1.getX() <= p2.getX() ? -factor : factor;
            int dY = p1.getY() <= p2.getY() ? -factor : factor;
            points.add(RectangleHelper.getRectanglePoints(p1.getX() + dX, p1.getY() + dY, p2.getX() - dX, p2.getY() - dY, false));
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

        if (settings.thick == 1 || settings.bulge == -1) {
            if (rx == 0 || ry == 0) {
                return LineHelper.getLinePoints(p1, p2);
            } else if (Math.abs(p1.getX() - p2.getX()) == 1 || Math.abs(p1.getY() - p2.getY()) == 1) {
                return RectangleHelper.getRectanglePoints(p1, p2, false);
            }
        }
        if (settings.thick == 1 && settings.bulge == -1) {
            return getEllipse(cx, cy, rx, ry, stretchH, stretchV, settings.fill);
        }

        int factorOuter = switch(settings.bulge) {
            case -1 -> 0;
            case 1 -> settings.thick - 1;
            default -> (settings.thick - 1) / 2;
        };
        int factorInner = settings.thick - factorOuter;

        int rxOuter = rx + factorOuter * stretchH;
        int ryOuter = ry + factorOuter * stretchV;
        int rxInner = rx - factorInner * stretchH;
        int ryInner = ry - factorInner * stretchV;

        PointArray outer = getEllipse(cx, cy, rxOuter, ryOuter, stretchH, stretchV, true);
        if (settings.fill || rxInner < 0 || ryInner < 0) {
            return outer;
        }
        PointArray inner = getEllipse(cx, cy, rxInner, ryInner, stretchH, stretchV, true);
        return outer.subtract(inner);
    }

    private static PointArray getEllipse(int cx, int cy, int rx, int ry, int stretchH, int stretchV, boolean fill) {
        PointArray stretchedPoints = EllipseHelper.getEllipsePointsStretched(cx, cy, rx, ry, fill);
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
            activeSet = newActiveSet.copy();
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
