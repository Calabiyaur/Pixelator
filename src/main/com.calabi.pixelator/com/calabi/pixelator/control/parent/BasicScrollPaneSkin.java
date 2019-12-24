package com.calabi.pixelator.control.parent;

import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import com.sun.javafx.util.Utils;

import static com.sun.javafx.scene.control.skin.Utils.boundedSize;

public class BasicScrollPaneSkin extends SkinBase<BasicScrollPane> {

    private static final double DEFAULT_SB_BREADTH = 8.;
    private static final double DEFAULT_PREF_SIZE = 100.;
    private static final double DEFAULT_MIN_SIZE = 36.;

    private Node scrollNode;
    private double nodeWidth;
    private double nodeHeight;
    private boolean nodeSizeInvalid = true;

    private double posX;
    private double posY;

    private boolean hsbVis;
    private boolean vsbVis;
    private double hsbHeight;
    private double vsbWidth;

    private StackPane viewRect;
    private StackPane viewContent;
    private double contentWidth;
    private double contentHeight;
    private ScrollBar hsb;
    private ScrollBar vsb;
    private final InvalidationListener nodeListener = new InvalidationListener() {
        @Override public void invalidated(Observable valueModel) {
            if (!nodeSizeInvalid) {
                final Bounds scrollNodeBounds = scrollNode.getLayoutBounds();
                final double scrollNodeWidth = scrollNodeBounds.getWidth();
                final double scrollNodeHeight = scrollNodeBounds.getHeight();

                if (vsbVis != determineVerticalSBVisible() || hsbVis != determineHorizontalSBVisible() ||
                        (scrollNodeWidth != 0.0 && nodeWidth != scrollNodeWidth) ||
                        (scrollNodeHeight != 0.0 && nodeHeight != scrollNodeHeight)) {
                    getSkinnable().requestLayout();
                } else {
                    updateVerticalSB();
                    updateHorizontalSB();
                }
            }
        }
    };
    private final ChangeListener<Bounds> boundsChangeListener = new ChangeListener<>() {
        @Override public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {

            double oldHeight = oldBounds.getHeight();
            double newHeight = newBounds.getHeight();
            if (oldHeight > 0 && oldHeight != newHeight) {
                double oldPositionY = (snapPositionY(
                        snappedTopInset() - posY / (vsb.getMax() - vsb.getMin()) * (oldHeight - contentHeight)));
                double newPositionY = (snapPositionY(
                        snappedTopInset() - posY / (vsb.getMax() - vsb.getMin()) * (newHeight - contentHeight)));

                double newValueY = (oldPositionY / newPositionY) * vsb.getValue();
                if (newValueY < 0.0) {
                    vsb.setValue(0.0);
                } else if (newValueY < 1.0) {
                    vsb.setValue(newValueY);
                } else if (newValueY > 1.0) {
                    vsb.setValue(1.0);
                }
            }

            double oldWidth = oldBounds.getWidth();
            double newWidth = newBounds.getWidth();
            if (oldWidth > 0 && oldWidth != newWidth) {
                double oldPositionX = (snapPositionX(
                        snappedLeftInset() - posX / (hsb.getMax() - hsb.getMin()) * (oldWidth - contentWidth)));
                double newPositionX = (snapPositionX(
                        snappedLeftInset() - posX / (hsb.getMax() - hsb.getMin()) * (newWidth - contentWidth)));

                double newValueX = (oldPositionX / newPositionX) * hsb.getValue();
                if (newValueX < 0.0) {
                    hsb.setValue(0.0);
                } else if (newValueX < 1.0) {
                    hsb.setValue(newValueX);
                } else if (newValueX > 1.0) {
                    hsb.setValue(1.0);
                }
            }
        }
    };
    private Rectangle clipRect;

    private double dragStartX;
    private double dragStartY;
    private boolean dragging;

    private Double lastMouseX;
    private Double lastMouseY;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    public BasicScrollPaneSkin(BasicScrollPane control) {
        super(control);

        initialize();

        Consumer<ObservableValue<?>> viewportSizeHintConsumer = e -> getSkinnable().requestLayout();
        registerChangeListener(control.contentProperty(), e -> {
            if (scrollNode != getSkinnable().getContent()) {
                if (scrollNode != null) {
                    scrollNode.layoutBoundsProperty().removeListener(nodeListener);
                    scrollNode.layoutBoundsProperty().removeListener(boundsChangeListener);
                    viewContent.getChildren().remove(scrollNode);
                }
                scrollNode = getSkinnable().getContent();
                if (scrollNode != null) {
                    nodeWidth = snapSizeX(scrollNode.getLayoutBounds().getWidth());
                    nodeHeight = snapSizeY(scrollNode.getLayoutBounds().getHeight());
                    viewContent.getChildren().setAll(scrollNode);
                    scrollNode.layoutBoundsProperty().addListener(nodeListener);
                    scrollNode.layoutBoundsProperty().addListener(boundsChangeListener);
                }
            }
            getSkinnable().requestLayout();
        });
        registerChangeListener(control.fitToWidthProperty(), e -> {
            getSkinnable().requestLayout();
            viewRect.requestLayout();
        });
        registerChangeListener(control.fitToHeightProperty(), e -> {
            getSkinnable().requestLayout();
            viewRect.requestLayout();
        });
        registerChangeListener(control.hbarPolicyProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.vbarPolicyProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.hvalueProperty(), e -> hsb.setValue(getSkinnable().getHvalue()));
        registerChangeListener(control.hmaxProperty(), e -> hsb.setMax(getSkinnable().getHmax()));
        registerChangeListener(control.hminProperty(), e -> hsb.setMin(getSkinnable().getHmin()));
        registerChangeListener(control.vvalueProperty(), e -> vsb.setValue(getSkinnable().getVvalue()));
        registerChangeListener(control.vmaxProperty(), e -> vsb.setMax(getSkinnable().getVmax()));
        registerChangeListener(control.vminProperty(), e -> vsb.setMin(getSkinnable().getVmin()));
        registerChangeListener(control.prefViewportWidthProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.prefViewportHeightProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.minViewportWidthProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.minViewportHeightProperty(), viewportSizeHintConsumer);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
            double leftInset) {
        final ScrollPane sp = getSkinnable();

        double vsbWidth = computeVsbSizeHint(sp);
        double minWidth = vsbWidth + snappedLeftInset() + snappedRightInset();

        if (sp.getPrefViewportWidth() > 0) {
            return (sp.getPrefViewportWidth() + minWidth);
        } else if (sp.getContent() != null) {
            return (sp.getContent().prefWidth(height) + minWidth);
        } else {
            return Math.max(minWidth, DEFAULT_PREF_SIZE);
        }
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
            double leftInset) {
        final ScrollPane sp = getSkinnable();

        double hsbHeight = computeHsbSizeHint(sp);
        double minHeight = hsbHeight + snappedTopInset() + snappedBottomInset();

        if (sp.getPrefViewportHeight() > 0) {
            return (sp.getPrefViewportHeight() + minHeight);
        } else if (sp.getContent() != null) {
            return (sp.getContent().prefHeight(width) + minHeight);
        } else {
            return Math.max(minHeight, DEFAULT_PREF_SIZE);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset,
            double leftInset) {
        final ScrollPane sp = getSkinnable();

        double vsbWidth = computeVsbSizeHint(sp);
        double minWidth = vsbWidth + snappedLeftInset() + snappedRightInset();

        if (sp.getMinViewportWidth() > 0) {
            return (sp.getMinViewportWidth() + minWidth);
        } else {
            return DEFAULT_MIN_SIZE;
        }
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
            double leftInset) {
        final ScrollPane sp = getSkinnable();

        double hsbHeight = computeHsbSizeHint(sp);
        double minHeight = hsbHeight + snappedTopInset() + snappedBottomInset();

        if (sp.getMinViewportHeight() > 0) {
            return (sp.getMinViewportHeight() + minHeight);
        } else {
            return DEFAULT_MIN_SIZE;
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final ScrollPane control = getSkinnable();
        final Insets padding = control.getPadding();
        final double rightPadding = snapSizeX(padding.getRight());
        final double leftPadding = snapSizeX(padding.getLeft());
        final double topPadding = snapSizeY(padding.getTop());
        final double bottomPadding = snapSizeY(padding.getBottom());

        vsb.setMin(control.getVmin());
        vsb.setMax(control.getVmax());

        hsb.setMin(control.getHmin());
        hsb.setMax(control.getHmax());

        contentWidth = w;
        contentHeight = h;

        double oldNodeWidth = nodeWidth;
        double oldNodeHeight = nodeHeight;

        // we want the scrollbars to go right to the border
        double hsbWidth = 0;
        double vsbHeight = 0;

        computeScrollNodeSize(contentWidth, contentHeight);
        computeScrollBarSize();

        for (int i = 0; i < 2; ++i) {
            vsbVis = determineVerticalSBVisible();
            hsbVis = determineHorizontalSBVisible();

            if (vsbVis) {
                contentWidth = w - vsbWidth;
            }
            hsbWidth = w + leftPadding + rightPadding - (vsbVis ? vsbWidth : 0);
            if (hsbVis) {
                contentHeight = h - hsbHeight;
            }
            vsbHeight = h + topPadding + bottomPadding - (hsbVis ? hsbHeight : 0);
        }

        if (scrollNode != null && scrollNode.isResizable()) {
            // maybe adjust size now that scrollbars may take up space
            if (vsbVis && hsbVis) {
                // adjust just once to accommodate
                computeScrollNodeSize(contentWidth, contentHeight);
            } else if (hsbVis) {
                computeScrollNodeSize(contentWidth, contentHeight);
                vsbVis = determineVerticalSBVisible();
                if (vsbVis) {
                    // now both are visible
                    contentWidth -= vsbWidth;
                    hsbWidth -= vsbWidth;
                    computeScrollNodeSize(contentWidth, contentHeight);
                }
            } else if (vsbVis) {
                computeScrollNodeSize(contentWidth, contentHeight);
                hsbVis = determineHorizontalSBVisible();
                if (hsbVis) {
                    // now both are visible
                    contentHeight -= hsbHeight;
                    vsbHeight -= hsbHeight;
                    computeScrollNodeSize(contentWidth, contentHeight);
                }
            }
        }

        // figure out the content area that is to be filled
        double cx = snappedLeftInset() - leftPadding;
        double cy = snappedTopInset() - topPadding;

        // Make the content node stay at mouse position
        double mouseX = lastMouseX == null ? w / 2 : lastMouseX;
        double mouseY = lastMouseY == null ? h / 2 : lastMouseY;

        double effectiveWidth = w - (vsb.isVisible() ? vsbWidth : 0);
        double effectiveHeight = h - (hsb.isVisible() ? hsbHeight : 0);

        double newHsbValue = hsb.getValue();
        double newVsbValue = vsb.getValue();

        if (oldNodeWidth > effectiveWidth && oldNodeWidth != nodeWidth) {

            double zoomFactor = oldNodeWidth / nodeWidth;
            double oldZoomFactor = effectiveWidth / oldNodeWidth;
            zoomFactor = Math.min(1, zoomFactor); //TODO: Remove this line
            double oldLeftBorder = -viewContent.getLayoutX();
            double weight = (zoomFactor * (1 - oldZoomFactor)) / (1 - zoomFactor * oldZoomFactor);

            newHsbValue = (weight) * hsb.getValue() + (1 - weight) * ((mouseX + oldLeftBorder) / oldNodeWidth);

        } else if (oldNodeWidth <= effectiveWidth) {

            newHsbValue = mouseX / oldNodeWidth;
        }

        if (oldNodeHeight > effectiveHeight && oldNodeHeight != nodeHeight) {

            double zoomFactor = oldNodeHeight / nodeHeight;
            double oldZoomFactor = effectiveHeight / oldNodeHeight;
            zoomFactor = Math.min(1, zoomFactor); //TODO: Remove this line
            double oldTopBorder = -viewContent.getLayoutY();
            double weight = (zoomFactor * (1 - oldZoomFactor)) / (1 - zoomFactor * oldZoomFactor);

            newVsbValue = (weight) * vsb.getValue() + (1 - weight) * ((mouseY + oldTopBorder) / oldNodeHeight);

        } else if (oldNodeHeight <= effectiveHeight) {

            newVsbValue = mouseY / oldNodeHeight;
        }

        vsb.setVisible(vsbVis);
        if (vsbVis) {
            vsb.setValue(newVsbValue);
            vsb.resizeRelocate(snappedLeftInset() + w - vsbWidth + (rightPadding < 1 ? 0 : rightPadding - 1), cy - 1,
                    vsbWidth, vsbHeight + 2);
        }
        updateVerticalSB();

        hsb.setVisible(hsbVis);
        if (hsbVis) {
            hsb.setValue(newHsbValue);
            hsb.resizeRelocate(cx - 1, snappedTopInset() + h - hsbHeight + (bottomPadding < 1 ? 0 : bottomPadding - 1),
                    hsbWidth + 2, hsbHeight);
        }
        updateHorizontalSB();

        viewRect.resizeRelocate(snappedLeftInset(), snappedTopInset(), snapSizeX(contentWidth), snapSizeY(contentHeight));
        resetClip();

        control.setViewportBounds(new BoundingBox(snapPositionX(viewContent.getLayoutX()),
                snapPositionY(viewContent.getLayoutY()), snapSizeX(contentWidth), snapSizeY(contentHeight)));
    }

    private void initialize() {
        BasicScrollPane control = getSkinnable();
        scrollNode = control.getContent();

        viewRect = new StackPane() {
            @Override protected void layoutChildren() {
                viewContent.resize(getWidth(), getHeight());
                updatePosX();
                updatePosY();
            }
        };

        viewRect.setManaged(false);
        viewRect.setCache(true);
        viewRect.getStyleClass().add("viewport");

        clipRect = new Rectangle();
        viewRect.setClip(clipRect);

        hsb = new BasicScrollBar();
        vsb = new BasicScrollBar();
        vsb.setOrientation(Orientation.VERTICAL);

        EventHandler<MouseEvent> barHandler = ev -> {
            if (getSkinnable().isFocusTraversable()) {
                getSkinnable().requestFocus();
            }
        };

        hsb.addEventFilter(MouseEvent.MOUSE_PRESSED, barHandler);
        vsb.addEventFilter(MouseEvent.MOUSE_PRESSED, barHandler);

        viewContent = new StackPane() {
            @Override public void requestLayout() {
                // if scrollNode requested layout, will want to recompute
                nodeSizeInvalid = true;

                super.requestLayout(); // add as layout root for next layout pass

                // Need to layout the ScrollPane as well in case scrollbars
                // appeared or disappeared.
                BasicScrollPaneSkin.this.getSkinnable().requestLayout();
            }

            @SuppressWarnings(value = "deprecation")
            @Override protected void layoutChildren() {
                if (nodeSizeInvalid) {
                    computeScrollNodeSize(getWidth(), getHeight());
                }
                if (scrollNode != null && scrollNode.isResizable()) {
                    scrollNode.resize(snapSize(nodeWidth), snapSize(nodeHeight));
                    if (vsbVis != determineVerticalSBVisible() || hsbVis != determineHorizontalSBVisible()) {
                        getSkinnable().requestLayout();
                    }
                }
                if (scrollNode != null) {
                    scrollNode.relocate(0, 0);
                }
            }
        };
        viewRect.getChildren().add(viewContent);

        if (scrollNode != null) {
            viewContent.getChildren().add(scrollNode);
            viewRect.nodeOrientationProperty().bind(scrollNode.nodeOrientationProperty());
        }

        getChildren().setAll(viewRect, vsb, hsb);

        vsb.valueProperty().addListener(valueModel -> {
            posY = Utils.clamp(getSkinnable().getVmin(), vsb.getValue(), getSkinnable().getVmax());
            updatePosY();
        });

        hsb.valueProperty().addListener(valueModel -> {
            posX = Utils.clamp(getSkinnable().getHmin(), hsb.getValue(), getSkinnable().getHmax());
            updatePosX();
        });

        /*
         * listen for ScrollEvents over the whole of the ScrollPane
         * area, the above dispatcher having removed the ScrollBars
         * scroll event handling.
         *
         * Note that we use viewRect here, rather than setting the eventHandler
         * on the ScrollPane itself. This is for RT-31582, and effectively
         * allows for us to prioritise handling (and consuming) the event
         * internally, before it is made available to users listening to events
         * on the control. This is consistent with the VirtualFlow-based controls.
         */
        viewRect.addEventHandler(ScrollEvent.SCROLL, event -> {

            if (!event.isControlDown()) {
                if (control.getOnRawScroll() != null) {
                    control.getOnRawScroll().handle(event);
                }
                if (!control.isScrollByMouse()) {
                    return;
                }
            }

            /*
             ** if we're completely visible then do nothing....
             ** we only consume an event that we've used.
             */
            if (vsb.getVisibleAmount() < vsb.getMax()) {
                double vRange = getSkinnable().getVmax() - getSkinnable().getVmin();
                double vPixelValue;
                if (nodeHeight > 0.0) {
                    vPixelValue = vRange / nodeHeight;
                } else {
                    vPixelValue = 0.0;
                }
                double newValue = vsb.getValue() + (-event.getDeltaY()) * vPixelValue;
                if ((event.getDeltaY() > 0.0 && vsb.getValue() > vsb.getMin()) ||
                        (event.getDeltaY() < 0.0 && vsb.getValue() < vsb.getMax())) {
                    vsb.setValue(newValue);
                    event.consume();
                }

            }

            if (hsb.getVisibleAmount() < hsb.getMax()) {
                double hRange = getSkinnable().getHmax() - getSkinnable().getHmin();
                double hPixelValue;
                if (nodeWidth > 0.0) {
                    hPixelValue = hRange / nodeWidth;
                } else {
                    hPixelValue = 0.0;
                }

                double newValue = hsb.getValue() + (-event.getDeltaX()) * hPixelValue;
                if ((event.getDeltaX() > 0.0 && hsb.getValue() > hsb.getMin()) ||
                        (event.getDeltaX() < 0.0 && hsb.getValue() < hsb.getMax())) {
                    hsb.setValue(newValue);
                    event.consume();
                }

            }
        });

        viewRect.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (MouseButton.MIDDLE.equals(e.getButton())) {
                if (hsb.isVisible() || vsb.isVisible()) {
                    dragStartX = e.getX();
                    dragStartY = e.getY();
                    dragging = true;
                }
                e.consume();
            }
        });

        viewRect.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            //lastMouseX = e.getX() - viewContent.getLayoutX(); //TODO: Is this needed?
            //lastMouseY = e.getY() - viewContent.getLayoutY();
            if (dragging && MouseButton.MIDDLE.equals(e.getButton())) {
                if (hsb.isVisible() || vsb.isVisible()) {
                    double dX = (dragStartX - e.getX()) / (nodeWidth * (1 - hsb.getVisibleAmount()));
                    double dY = (dragStartY - e.getY()) / (nodeHeight * (1 - vsb.getVisibleAmount()));
                    hsb.setValue(Utils.clamp(hsb.getMin(), hsb.getValue() + dX, hsb.getMax()));
                    vsb.setValue(Utils.clamp(vsb.getMin(), vsb.getValue() + dY, vsb.getMax()));
                    dragStartX = e.getX();
                    dragStartY = e.getY();
                }
                e.consume();
            }
        });

        viewRect.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (dragging && MouseButton.MIDDLE.equals(e.getButton())) {
                dragging = false;
                e.consume();
            }
        });

        viewRect.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            lastMouseX = e.getX() - Math.max(0, viewContent.getLayoutX());
            lastMouseY = e.getY() - Math.max(0, viewContent.getLayoutY());
        });

        viewRect.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            lastMouseX = null;
            lastMouseY = null;
        });

        consumeMouseEvents(false);
        scrollNode.addEventHandler(MouseEvent.ANY, e -> e.consume());

        hsb.setValue(control.getHvalue());
        vsb.setValue(control.getVvalue());
    }

    private void computeScrollNodeSize(double contentWidth, double contentHeight) {
        if (scrollNode != null) {
            if (scrollNode.isResizable()) {
                ScrollPane control = getSkinnable();
                Orientation bias = scrollNode.getContentBias();
                if (bias == null) {
                    nodeWidth = snapSizeX(boundedSize(control.isFitToWidth() ? contentWidth : scrollNode.prefWidth(-1),
                            scrollNode.minWidth(-1), scrollNode.maxWidth(-1)));
                    nodeHeight = snapSizeY(boundedSize(control.isFitToHeight() ? contentHeight : scrollNode.prefHeight(-1),
                            scrollNode.minHeight(-1), scrollNode.maxHeight(-1)));

                } else if (bias == Orientation.HORIZONTAL) {
                    nodeWidth = snapSizeX(boundedSize(control.isFitToWidth() ? contentWidth : scrollNode.prefWidth(-1),
                            scrollNode.minWidth(-1), scrollNode.maxWidth(-1)));
                    nodeHeight =
                            snapSizeY(boundedSize(control.isFitToHeight() ? contentHeight : scrollNode.prefHeight(nodeWidth),
                                    scrollNode.minHeight(nodeWidth), scrollNode.maxHeight(nodeWidth)));

                } else { // bias == VERTICAL
                    nodeHeight = snapSizeY(boundedSize(control.isFitToHeight() ? contentHeight : scrollNode.prefHeight(-1),
                            scrollNode.minHeight(-1), scrollNode.maxHeight(-1)));
                    nodeWidth =
                            snapSizeX(boundedSize(control.isFitToWidth() ? contentWidth : scrollNode.prefWidth(nodeHeight),
                                    scrollNode.minWidth(nodeHeight), scrollNode.maxWidth(nodeHeight)));
                }

            } else {
                nodeWidth = snapSizeX(scrollNode.getLayoutBounds().getWidth());
                nodeHeight = snapSizeY(scrollNode.getLayoutBounds().getHeight());
            }
            nodeSizeInvalid = false;
        }
    }

    private boolean determineVerticalSBVisible() {
        final ScrollPane sp = getSkinnable();

        ScrollPane.ScrollBarPolicy vbarPolicy = sp.getVbarPolicy();
        switch(vbarPolicy) {
            case NEVER:
                return false;
            case ALWAYS:
                return true;
            default:
                if (sp.isFitToHeight() && scrollNode != null && scrollNode.isResizable()) {
                    return nodeHeight > contentHeight && scrollNode.minHeight(-1) > contentHeight;
                }
                return nodeHeight > contentHeight;
        }
    }

    private boolean determineHorizontalSBVisible() {
        final ScrollPane sp = getSkinnable();

        ScrollPane.ScrollBarPolicy hbarPolicy = sp.getHbarPolicy();
        switch(hbarPolicy) {
            case NEVER:
                return false;
            case ALWAYS:
                return true;
            default:
                if (sp.isFitToWidth() && scrollNode != null && scrollNode.isResizable()) {
                    return nodeWidth > contentWidth && scrollNode.minWidth(-1) > contentWidth;
                }
                return nodeWidth > contentWidth;
        }
    }

    private void updatePosY() {
        final ScrollPane sp = getSkinnable();
        double minY = Math.min((-posY / (vsb.getMax() - vsb.getMin()) * (nodeHeight - contentHeight)), 0);

        double extraY = 0;
        if (viewRect.getHeight() > nodeHeight) {
            extraY = (viewRect.getHeight() - nodeHeight) / 2;
        }
        viewContent.setLayoutY(snapPositionY(minY + extraY));

        if (!sp.vvalueProperty().isBound()) {
            sp.setVvalue(Utils.clamp(sp.getVmin(), posY, sp.getVmax()));
        }
    }

    private void updatePosX() {
        final ScrollPane sp = getSkinnable();
        double x = isReverseNodeOrientation() ? (hsb.getMax() - (posX - hsb.getMin())) : posX;
        double minX = Math.min((-x / (hsb.getMax() - hsb.getMin()) * (nodeWidth - contentWidth)), 0);

        double extraX = 0;
        if (viewRect.getWidth() > nodeWidth) {
            extraX = (viewRect.getWidth() - nodeWidth) / 2;
        }
        viewContent.setLayoutX(snapPositionX(minX + extraX));

        if (!sp.hvalueProperty().isBound()) {
            sp.setHvalue(Utils.clamp(sp.getHmin(), posX, sp.getHmax()));
        }
    }

    private boolean isReverseNodeOrientation() {
        return scrollNode != null
                && getSkinnable().getEffectiveNodeOrientation() != scrollNode.getEffectiveNodeOrientation();
    }

    private void updateVerticalSB() {
        double contentRatio = nodeHeight * (vsb.getMax() - vsb.getMin());
        if (contentRatio > 0.0) {
            vsb.setVisibleAmount(contentHeight / contentRatio);
            vsb.setBlockIncrement(0.9 * vsb.getVisibleAmount());
            vsb.setUnitIncrement(0.1 * vsb.getVisibleAmount());
        } else {
            vsb.setVisibleAmount(0.0);
            vsb.setBlockIncrement(0.0);
            vsb.setUnitIncrement(0.0);
        }

        if (vsb.isVisible()) {
            updatePosY();
        } else {
            if (nodeHeight > contentHeight) {
                updatePosY();
            } else {
                viewContent.setLayoutY(0);
            }
        }
    }

    private void updateHorizontalSB() {
        double contentRatio = nodeWidth * (hsb.getMax() - hsb.getMin());
        if (contentRatio > 0.0) {
            hsb.setVisibleAmount(contentWidth / contentRatio);
            hsb.setBlockIncrement(0.9 * hsb.getVisibleAmount());
            hsb.setUnitIncrement(0.1 * hsb.getVisibleAmount());
        } else {
            hsb.setVisibleAmount(0.0);
            hsb.setBlockIncrement(0.0);
            hsb.setUnitIncrement(0.0);
        }

        if (hsb.isVisible()) {
            updatePosX();
        } else {
            if (nodeWidth > contentWidth) {
                updatePosX();
            } else {
                viewContent.setLayoutX(0);
            }
        }
    }

    private double computeVsbSizeHint(ScrollPane sp) {
        if ((sp.getVbarPolicy() == ScrollPane.ScrollBarPolicy.ALWAYS)
                || ((sp.getVbarPolicy() == ScrollPane.ScrollBarPolicy.AS_NEEDED)
                && ((sp.getPrefViewportWidth() > 0) || (sp.getMinViewportWidth() > 0)))) {
            return vsb.prefWidth(ScrollBar.USE_COMPUTED_SIZE);
        } else {
            return 0;
        }
    }

    private double computeHsbSizeHint(ScrollPane sp) {
        if ((sp.getHbarPolicy() == ScrollPane.ScrollBarPolicy.ALWAYS)
                || (sp.getHbarPolicy() == ScrollPane.ScrollBarPolicy.AS_NEEDED
                && (sp.getPrefViewportHeight() > 0 || sp.getMinViewportHeight() > 0))) {
            return hsb.prefHeight(ScrollBar.USE_COMPUTED_SIZE);
        } else {
            return 0;
        }
    }

    private void computeScrollBarSize() {
        vsbWidth = snapSizeX(vsb.prefWidth(-1));
        if (vsbWidth == 0) {
            vsbWidth = DEFAULT_SB_BREADTH;
        }
        hsbHeight = snapSizeY(hsb.prefHeight(-1));
        if (hsbHeight == 0) {
            hsbHeight = DEFAULT_SB_BREADTH;
        }
    }

    private void resetClip() {
        clipRect.setWidth(snapSizeX(contentWidth));
        clipRect.setHeight(snapSizeY(contentHeight));
    }

}
