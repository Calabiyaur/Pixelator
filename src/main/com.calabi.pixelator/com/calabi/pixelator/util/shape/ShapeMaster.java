package com.calabi.pixelator.util.shape;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.Move;
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

        PointArray line = LineHelper.getLinePoints(p1, p2, settings.maxX, settings.maxY);
        if (settings.thick == 1) {
            return line;
        }

        // Create mast to lay over each point in the line to create a thick line
        Point rotatedP2 = Rotate.rotateRight(p1, p2);
        PointArray mask = LineHelper.getStraightPath(p1, rotatedP2, settings.thick);

        // Find origin according to bulge
        Point origin = switch(settings.bulge) {
            case -1 -> p1;
            case 1 -> Move.towards(p1, rotatedP2, settings.thick - 1);
            default -> Move.towards(p1, rotatedP2, (settings.thick - 1) / 2);
        };

        // Combine line and mask
        PointArray thickLine = new PointArray();
        for (Point linePoint : line.getPoints()) {
            for (Point maskPoint : mask.getPoints()) {
                int x = linePoint.getX() + maskPoint.getX() - origin.getX();
                int y = linePoint.getY() + maskPoint.getY() - origin.getY();
                if (0 <= x && x < settings.maxX && 0 <= y && y < settings.maxY) {
                    thickLine.add(x, y);
                }
            }
        }
        return thickLine;
    }

    public static PointArray getRectanglePoints(Point p1, Point p2, ToolSettings settings) {
        PointArray points = RectangleHelper.getRectanglePoints(p1, p2, settings.fill, settings.maxX, settings.maxY);
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
            points.add(RectangleHelper.getRectanglePoints(p1.getX() + dX, p1.getY() + dY, p2.getX() - dX, p2.getY() - dY, false, settings.maxX, settings.maxY));
        }
        return points;
    }

    public static PointArray getEllipsePoints(Point p1, Point p2, ToolSettings settings) {
        // If width (height) is odd, stretch by factor 2 to avoid half-ints
        int stretchH = (p1.getX() + p2.getX()) % 2 == 0 ? 1 : 2;
        int stretchV = (p1.getY() + p2.getY()) % 2 == 0 ? 1 : 2;
        int cx = (p1.getX() + p2.getX()) / (2 / stretchH);
        int cy = (p1.getY() + p2.getY()) / (2 / stretchV);
        int rx = Math.abs(p1.getX() - p2.getX()) / (2 / stretchH);
        int ry = Math.abs(p1.getY() - p2.getY()) / (2 / stretchV);

        if (settings.thick == 1 || settings.bulge == -1) {
            if (rx == 0 || ry == 0) {
                return LineHelper.getLinePoints(p1, p2, settings.maxX, settings.maxY);
            } else if (Math.abs(p1.getX() - p2.getX()) == 1 || Math.abs(p1.getY() - p2.getY()) == 1) {
                return RectangleHelper.getRectanglePoints(p1, p2, false, settings.maxX, settings.maxY);
            }
        }
        if (settings.thick == 1 && settings.bulge == -1) {
            return EllipseHelper.getEllipse(cx, cy, rx, ry, stretchH, stretchV, settings.fill, settings.maxX, settings.maxY);
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

        PointArray outer = EllipseHelper.getEllipse(cx, cy, rxOuter, ryOuter, stretchH, stretchV, true, settings.maxX, settings.maxY);
        if (settings.fill || rxInner < 0 || ryInner < 0) {
            return outer;
        }
        PointArray inner = EllipseHelper.getEllipse(cx, cy, rxInner, ryInner, stretchH, stretchV, true, settings.maxX, settings.maxY);
        outer.subtract(inner);
        return outer;
    }

    /**
     * Return all points that are directly connected to the
     * starting point through color.
     *
     * @param point the starting point
     * @param cStart color of the starting point
     */
    public static PointArray getFillPoints(Point point, Color cStart, PixelReader reader, int width, int height,
            ToolSettings settings) {

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
            for (Point activePoint : activeSet.getPoints()) {
                int x = activePoint.getX();
                int y = activePoint.getY();
                if (activeMap[x][y]) {

                    result.add(x, y);

                    for (int j = 0; j < 4; j++) {

                        int newX = j % 2 == 1 ? x : x + 1 - j;
                        int newY = j % 2 == 0 ? y : y - 2 + j;

                        try {
                            if (activeMap[newX][newY] == null) {
                                if (isEqualOrSimilar(cStart, reader.getColor(newX, newY), settings)) {
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
    public static PointArray getPointsOfColor(Color color, PixelReader reader, int width, int height,
            ToolSettings settings) {
        PointArray result = new PointArray();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isEqualOrSimilar(color, reader.getColor(i, j), settings)) {
                    result.add(i, j);
                }
            }
        }
        return result;
    }

    private static boolean isEqualOrSimilar(Color color, Color otherColor, ToolSettings settings) {
        return (settings.tolerance == 0 && color.equals(otherColor))
                || ColorUtil.compare(color, otherColor) * 100 <= settings.tolerance;
    }

}
