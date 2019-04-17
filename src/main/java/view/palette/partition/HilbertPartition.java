package main.java.view.palette.partition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

import main.java.meta.Direction;
import main.java.meta.Frac;
import main.java.meta.Point;
import org.apache.commons.lang3.tuple.Pair;

import static main.java.meta.Direction.EAST;
import static main.java.meta.Direction.NORTH;
import static main.java.meta.Direction.NORTH_EAST;
import static main.java.meta.Direction.NORTH_WEST;
import static main.java.meta.Direction.SOUTH;
import static main.java.meta.Direction.SOUTH_EAST;
import static main.java.meta.Direction.SOUTH_WEST;
import static main.java.meta.Direction.WEST;

public class HilbertPartition implements Partition {

    private final static Frac TWO = Frac.of(2);

    private final Part root;

    public HilbertPartition() {
        Frac f = Frac.of(.5);
        root = new Part(0, NORTH, f, f, f, f, f, f, f);
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
                    part.splitHs((other.getHue() + color.getHue()) / 720.,
                            (other.getSaturation() + color.getSaturation()) / 2.);
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
                set.add(new ColorPoint(color, part.progress.doubleValue(), color.getBrightness()));
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
        private final Frac progress;
        private final Frac hue;
        private final Frac hTolerance;
        private final Frac saturation;
        private final Frac sTolerance;
        private final Frac brightness;
        private final Frac bTolerance;
        private Set<Color> colors;
        private List<Part> children;

        public Part(int depth, Direction orientation, Frac progress,
                Frac hue, Frac hTolerance, Frac saturation, Frac sTolerance,
                Frac brightness, Frac bTolerance) {
            this.orientation = orientation;
            this.depth = depth;
            this.progress = progress;
            this.hue = hue;
            this.hTolerance = hTolerance;
            this.saturation = saturation;
            this.sTolerance = sTolerance;
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
            Frac xF = Frac.of(x);
            Frac yF = Frac.of(y);
            Frac zF = Frac.of(z);
            return hue.subtract(hTolerance).compareTo(xF) <= 0 && hue.add(hTolerance).compareTo(xF) >= 0
                    && saturation.subtract(sTolerance).compareTo(yF) <= 0 && saturation.add(sTolerance).compareTo(yF) >= 0
                    && brightness.subtract(bTolerance).compareTo(zF) <= 0 && brightness.add(bTolerance).compareTo(zF) >= 0;
        }

        public Part getPartContaining(Color color) {
            for (Part part : getChildren()) {
                if (part.contains(color.getHue() / 360., color.getSaturation(), color.getBrightness())) {
                    return part;
                }
            }
            return null;
        }

        public void splitHs(double hMargin, double sMargin) {
            Frac hMarginF = Frac.of(hMargin);
            Frac sMarginF = Frac.of(sMargin);
            if (hue.subtract(hMarginF).abs().divideBy(hTolerance)
                    .compareTo(saturation.subtract(sMarginF).abs().divideBy(sTolerance)) < 0) {
                sMarginF = saturation;
            } else {
                hMarginF = hue;
            }

            switch(orientation) {
                case NORTH:
                    splitHs(hMarginF, sMarginF,
                            Arrays.asList(EAST, NORTH, NORTH, WEST),
                            Arrays.asList(SOUTH_WEST, NORTH_WEST, NORTH_EAST, SOUTH_EAST));
                    break;
                case EAST:
                    splitHs(hMarginF, sMarginF,
                            Arrays.asList(NORTH, EAST, EAST, SOUTH),
                            Arrays.asList(SOUTH_WEST, SOUTH_EAST, NORTH_EAST, NORTH_WEST));
                    break;
                case SOUTH:
                    splitHs(hMarginF, sMarginF,
                            Arrays.asList(WEST, SOUTH, SOUTH, EAST),
                            Arrays.asList(NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST));
                    break;
                case WEST:
                    splitHs(hMarginF, sMarginF,
                            Arrays.asList(SOUTH, WEST, WEST, NORTH),
                            Arrays.asList(NORTH_EAST, NORTH_WEST, SOUTH_WEST, SOUTH_EAST));
                    break;
            }
            Color color = getColors().iterator().next();
            getPartContaining(color).addColor(color);
            validate();
        }

        private void splitHs(Frac hMargin, Frac sMargin, List<Direction> orientations, List<Direction> positions) {

            Frac hMin = hue.subtract(hTolerance);
            Frac hMax = hue.add(hTolerance);
            Frac sMin = saturation.subtract(sTolerance);
            Frac sMax = saturation.add(sTolerance);

            Map<Direction, Frac> progressRange = new HashMap<>();
            progressRange.put(NORTH_EAST, hMax.subtract(hMargin).multiplyBy(sMargin.subtract(sMin)));
            progressRange.put(NORTH_WEST, hMargin.subtract(hMin).multiplyBy(sMargin.subtract(sMin)));
            progressRange.put(SOUTH_WEST, hMargin.subtract(hMin).multiplyBy(sMax.subtract(sMargin)));
            progressRange.put(SOUTH_EAST, hMax.subtract(hMargin).multiplyBy(sMax.subtract(sMargin)));

            Map<Direction, Pair<Frac, Frac>> hueMap = new HashMap<>();
            hueMap.put(NORTH_EAST, Pair.of(hMax.add(hMargin).divideBy(TWO), hMax.subtract(hMargin).divideBy(TWO)));
            hueMap.put(NORTH_WEST, Pair.of(hMargin.add(hMin).divideBy(TWO), hMargin.subtract(hMin).divideBy(TWO)));
            hueMap.put(SOUTH_WEST, Pair.of(hMargin.add(hMin).divideBy(TWO), hMargin.subtract(hMin).divideBy(TWO)));
            hueMap.put(SOUTH_EAST, Pair.of(hMax.add(hMargin).divideBy(TWO), hMax.subtract(hMargin).divideBy(TWO)));

            Map<Direction, Pair<Frac, Frac>> satMap = new HashMap<>();
            satMap.put(NORTH_EAST, Pair.of((sMargin.add(sMin)).divideBy(TWO), sMargin.subtract(sMin).divideBy(TWO)));
            satMap.put(NORTH_WEST, Pair.of((sMargin.add(sMin)).divideBy(TWO), sMargin.subtract(sMin).divideBy(TWO)));
            satMap.put(SOUTH_WEST, Pair.of((sMax.add(sMargin)).divideBy(TWO), sMax.subtract(sMargin).divideBy(TWO)));
            satMap.put(SOUTH_EAST, Pair.of((sMax.add(sMargin)).divideBy(TWO), sMax.subtract(sMargin).divideBy(TWO)));

            Frac pMin = progress.subtract(TWO.multiplyBy(hTolerance).multiplyBy(sTolerance));
            for (int i = 0; i < 4; i++) {
                Direction o = orientations.get(i);
                Direction p = positions.get(i);
                Frac pr = progressRange.get(p);
                Pair<Frac, Frac> huePair = hueMap.get(p);
                Pair<Frac, Frac> satPair = satMap.get(p);
                getChildren().add(new Part(depth + 1, o, pMin.add(pr.divideBy(TWO)),
                        huePair.getLeft(), huePair.getRight(),
                        satPair.getLeft(), satPair.getRight(), brightness, bTolerance));
                pMin = pMin.add(pr);
            }
        }

        public void splitB(double margin) {
            Frac marginF = Frac.of(margin);
            Frac bMin = brightness.subtract(bTolerance);
            Frac bMax = brightness.add(bTolerance);
            getChildren().add(new Part(depth + 1, orientation, progress, hue, hTolerance, saturation, sTolerance,
                    marginF.add(bMin).divideBy(TWO),
                    marginF.subtract(bMin).divideBy(TWO)));
            getChildren().add(new Part(depth + 1, orientation, progress, hue, hTolerance, saturation, sTolerance,
                    bMax.add(marginF).divideBy(TWO),
                    bMax.subtract(marginF).divideBy(TWO)));
            Color color = getColors().iterator().next();
            getPartContaining(color).addColor(color);
            validate();
        }

        private void validate() {
            // Unique
            for (Part child : getChildren()) {
                if (Objects.equals(child.orientation, orientation)
                        && Objects.equals(child.progress, progress)
                        && Objects.equals(child.hue, hue)
                        && Objects.equals(child.hTolerance, hTolerance)
                        && Objects.equals(child.saturation, saturation)
                        && Objects.equals(child.sTolerance, sTolerance)
                        && Objects.equals(child.brightness, brightness)
                        && Objects.equals(child.bTolerance, bTolerance)) {
                    throw new IllegalStateException();
                }
            }
            // Complete
            //if (getChildren().size() == 4) {
            //    List<Part> sorted = getChildren().stream().sorted((c1, c2) -> {
            //        int cSat = c1.saturation.compareTo(c2.saturation);
            //        if (cSat != 0) {
            //            return cSat;
            //        }
            //        return c1.hue.compareTo(c2.hue);
            //    }).collect(Collectors.toList());
            //    Part topLeft = sorted.get(0);
            //    Part topRight = sorted.get(1);
            //    Part botLeft = sorted.get(2);
            //    Part botRight = sorted.get(3);
            //    Check.ensureEquals(topLeft.hue, botLeft.hue);
            //    Check.ensureEquals(topLeft.hTolerance, botLeft.hTolerance);
            //    Check.ensureEquals(botRight.hue, botRight.hue);
            //    Check.ensureEquals(botRight.hTolerance, botRight.hTolerance);
            //    Check.ensureEquals(hue.subtract(hTolerance), topLeft.hue.subtract(topLeft.hTolerance));
            //    Check.ensureEquals(topLeft.hue.add(topLeft.hTolerance), topRight.hue.subtract(topRight.hTolerance));
            //    Check.ensureEquals(topRight.hue.add(topRight.hTolerance), hue.add(hTolerance));
            //    Check.ensureEquals(topLeft.saturation, topRight.saturation);
            //    Check.ensureEquals(topLeft.sTolerance, topRight.sTolerance);
            //    Check.ensureEquals(botLeft.saturation, botRight.saturation);
            //    Check.ensureEquals(botLeft.sTolerance, botRight.sTolerance);
            //    Check.ensureEquals(saturation.subtract(sTolerance), topLeft.saturation.subtract(topLeft.sTolerance));
            //    Check.ensureEquals(topLeft.saturation.add(topLeft.sTolerance),
            //            botLeft.saturation.subtract(botLeft.sTolerance));
            //    Check.ensureEquals(botLeft.saturation.add(botLeft.sTolerance), saturation.add(sTolerance));
            //}
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
