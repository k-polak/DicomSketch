package com.kpolak.view;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class SceneGestures {
    private DragContext sceneDragContext = new DragContext();

    PanAndZoomPane panAndZoomPane;

    public SceneGestures(PanAndZoomPane canvas) {
        this.panAndZoomPane = canvas;
    }

    public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
        return onMouseClickedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            sceneDragContext.mouseAnchorX = event.getX();
            sceneDragContext.mouseAnchorY = event.getY();

            sceneDragContext.translateAnchorX = panAndZoomPane.getTranslateX();
            sceneDragContext.translateAnchorY = panAndZoomPane.getTranslateY();

        }

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            if (event.getButton().equals(MouseButton.MIDDLE)) {
                panAndZoomPane.setTranslateX(sceneDragContext.translateAnchorX + event.getX() - sceneDragContext.mouseAnchorX);
                panAndZoomPane.setTranslateY(sceneDragContext.translateAnchorY + event.getY() - sceneDragContext.mouseAnchorY);

                event.consume();
            }
        }
    };

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {

            double delta = PanAndZoomPane.DEFAULT_DELTA;

            double scale = panAndZoomPane.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            panAndZoomPane.setDeltaY(event.getDeltaY());
            if (panAndZoomPane.deltaY.get() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }

            double f = (scale / oldScale) - 1;
            double dx = (event.getX() - (panAndZoomPane.getBoundsInParent().getWidth() / 2 + panAndZoomPane.getBoundsInParent().getMinX()));
            double dy = (event.getY() - (panAndZoomPane.getBoundsInParent().getHeight() / 2 + panAndZoomPane.getBoundsInParent().getMinY()));

            panAndZoomPane.setPivot(f * dx, f * dy, scale);

            event.consume();

        }
    };

    /**
     * Mouse click handler
     */
    private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    panAndZoomPane.resetZoom();
                }
            }
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                if (event.getClickCount() == 2) {
                    panAndZoomPane.fitWidth();
                }
            }
        }
    };
}
