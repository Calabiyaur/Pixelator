package main.java.view.palette.partition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

import main.java.meta.Direction;
import main.java.meta.Point;

public class HilbertPartition implements Partition {

    private final Part root;

    public HilbertPartition() {
        root = new Part(0, Direction.NORTH, .5, .5, .5, .5, .5, .5);
    }

    @Override
    public void add(Color color) {
        add(root, color);
    }

    private void add(Part part, Color color) {
        if (part.isLeaf()) {
            if (!part.getColors().isEmpty()) {
                Color other = part.getColors().iterator().next();
                double dHue = Math.abs(other.getHue() - color.getHue()) / 360.;
                double dSaturation = Math.abs(other.getSaturation() - color.getSaturation());
                double dBrightness = Math.abs(other.getBrightness() - color.getBrightness());
                double dHs = Math.sqrt(Math.pow(dHue, 2) + Math.pow(dSaturation, 2));
                if (dHs > dBrightness) {
                    part.splitHs();
                } else {
                    part.splitB((other.getBrightness() + color.getBrightness()) / 2.);
                }
                add(part.getPartContaining(color), color);
            } else {
                part.addColor(color);
            }
        } else {
            add(part.getPartContaining(color), color);
        }
    }

    @Override
    public Map<Color, Point> createMapping() {
        Set<ColorPoint> set = addMapping(root, new HashSet<>());

        Set<Double> hsValues = new HashSet<>();
        Set<Double> bValues = new HashSet<>();
        for (ColorPoint colorPoint : set) {
            hsValues.add(colorPoint.hs);
            bValues.add(colorPoint.b);
        }
        List<Double> hsOrder = hsValues.stream().sorted().collect(Collectors.toList());
        List<Double> bOrder = bValues.stream().sorted().collect(Collectors.toList());

        Map<Color, Point> result = new HashMap<>();
        for (ColorPoint colorPoint : set) {
            int x = bOrder.indexOf(colorPoint.b);
            int y = hsOrder.indexOf(colorPoint.hs);
            result.put(colorPoint.color, new Point(x, y));
        }
        return result;
    }

    private Set<ColorPoint> addMapping(Part part, Set<ColorPoint> set) {
        if (part.isLeaf()) {
            if (!part.getColors().isEmpty()) {
                Color color = part.getColors().iterator().next();
                set.add(new ColorPoint(color, part.hs, part.brightness));
            }
        } else {
            for (Part child : part.getChildren()) {
                addMapping(child, set);
            }
        }
        return set;
    }

    private class Part {

        private final int depth;
        private final Direction orientation;
        private final double hs;
        private final double hue;
        private final double saturation;
        private final double hsTolerance;
        private final double brightness;
        private final double bTolerance;
        private Set<Color> colors;
        private List<Part> children;

        public Part(int depth, Direction orientation, double hs,
                double hue, double saturation, double hsTolerance, double brightness, double bTolerance) {
            this.orientation = orientation;
            this.depth = depth;
            this.hs = hs;
            this.hue = hue;
            this.saturation = saturation;
            this.hsTolerance = hsTolerance;
            this.brightness = brightness;
            this.bTolerance = bTolerance;
        }

        public boolean isLeaf() {
            return getChildren().isEmpty();
        }

        public void addColor(Color color) {
            getColors().add(color);
        }

        public boolean contains(double x, double y, double z) {
            return hue - hsTolerance <= x && hue + hsTolerance >= x
                    && saturation - hsTolerance <= y && saturation + hsTolerance >= y
                    && brightness - bTolerance <= z && brightness + bTolerance >= z;
        }

        public Part getPartContaining(Color color) {
            for (Part part : getChildren()) {
                if (part.contains(color.getHue() / 360., color.getSaturation(), color.getBrightness())) {
                    return part;
                }
            }
            return null;
        }

        public void splitHs() {
            double t = hsTolerance / 2.;
            double hs1 = hs - 1.5 * (t / 2.);
            double hs2 = hs - 0.5 * (t / 2.);
            double hs3 = hs + 0.5 * (t / 2.);
            double hs4 = hs + 1.5 * (t / 2.);
            switch(orientation) {
                case NORTH:
                    getChildren().add(new Part(depth + 1, Direction.EAST, hs1, hue - t, saturation + t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.NORTH, hs2, hue - t, saturation - t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.NORTH, hs3, hue + t, saturation - t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.WEST, hs4, hue + t, saturation + t, t, brightness, bTolerance));
                    break;
                case EAST:
                    getChildren().add(new Part(depth + 1, Direction.NORTH, hs1, hue - t, saturation + t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.EAST, hs2, hue + t, saturation + t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.EAST, hs3, hue + t, saturation - t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.SOUTH, hs4, hue - t, saturation - t, t, brightness, bTolerance));
                    break;
                case SOUTH:
                    getChildren().add(new Part(depth + 1, Direction.WEST, hs1, hue + t, saturation - t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.SOUTH, hs2, hue + t, saturation + t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.SOUTH, hs3, hue - t, saturation + t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.EAST, hs4, hue - t, saturation - t, t, brightness, bTolerance));
                    break;
                case WEST:
                    getChildren().add(new Part(depth + 1, Direction.SOUTH, hs1, hue + t, saturation - t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.WEST, hs2, hue - t, saturation - t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.WEST, hs3, hue - t, saturation + t, t, brightness, bTolerance));
                    getChildren().add(new Part(depth + 1, Direction.NORTH, hs4, hue + t, saturation + t, t, brightness, bTolerance));
                    break;
            }
            Color color = getColors().iterator().next();
            getPartContaining(color).addColor(color);
            validate();
        }

        public void splitB(double margin) {
            double lightBorder = brightness - bTolerance;
            double darkBorder = brightness + bTolerance;
            getChildren().add(new Part(depth + 1, orientation, hs, hue, saturation, hsTolerance,
                    (margin + lightBorder) / 2,
                    (margin - lightBorder) / 2));
            getChildren().add(new Part(depth + 1, orientation, hs, hue, saturation, hsTolerance,
                    (margin + darkBorder) / 2,
                    (darkBorder - margin) / 2));
            Color color = getColors().iterator().next();
            getPartContaining(color).addColor(color);
            validate();
        }

        private void validate() {
            for (Part child : getChildren()) {
                if (Objects.equals(child.orientation, orientation)
                        && Objects.equals(child.hs, hs)
                        && Objects.equals(child.hue, hue)
                        && Objects.equals(child.saturation, saturation)
                        && Objects.equals(child.hsTolerance, hsTolerance)
                        && Objects.equals(child.brightness, brightness)
                        && Objects.equals(child.bTolerance, bTolerance)) {
                    throw new IllegalStateException();
                }
            }
        }

        public Set<Color> getColors() {
            if (colors == null) {
                colors = new HashSet<>();
            }
            return colors;
        }

        public List<Part> getChildren() {
            if (children == null) {
                children = new ArrayList<>();
            }
            return children;
        }
    }

    private class ColorPoint {

        private final Color color;
        private final double hs;
        private final double b;

        public ColorPoint(Color color, double hs, double b) {
            this.color = color;
            this.hs = hs;
            this.b = b;
        }
    }

}
