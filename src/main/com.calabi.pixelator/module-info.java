module com.calabi.pixelator {

    requires java.desktop;
    requires java.prefs;

    requires javafx.controls;
    requires javafx.graphics;

    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j;

    exports com.calabi.pixelator.ui.image;
    exports com.calabi.pixelator.files;
    exports com.calabi.pixelator.res;
    exports com.calabi.pixelator.start;
}
