package com.calabi.pixelator.util;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;

import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.control.image.WritableImage;
import com.calabi.pixelator.meta.PixelArray;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.start.ExceptionHandler;

public class ImageUtil {

    public static Image get(PixelArray pixels) {
        int x1 = Integer.MAX_VALUE;
        int y1 = Integer.MAX_VALUE;
        int x2 = 0;
        int y2 = 0;
        for (int i = 0; i < pixels.size(); i++) {
            x1 = Math.min(pixels.getX(i), x1);
            y1 = Math.min(pixels.getY(i), y1);
            x2 = Math.max(pixels.getX(i), x2);
            y2 = Math.max(pixels.getY(i), y2);
        }
        if (x1 >= x2 || y1 >= y2) {
            throw new IllegalStateException("Failed converting pixels to Image");
        }
        WritableImage image = new WritableImage(x2 - x1 + 1, y2 - y1 + 1);
        PixelWriter writer = image.getPixelWriter();
        for (int i = 0; i < pixels.size(); i++) {
            writer.setColor(pixels.getX(i) - x1, pixels.getY(i) - y1, pixels.getColor(i));
        }
        return image;
    }

    public static boolean outOfBounds(Image image, int x, int y) {
        return x < 0 || x >= (int) image.getWidth() || y < 0 || y >= (int) image.getHeight();
    }

    public static boolean outOfBounds(Image image, Point point) {
        return outOfBounds(image, point.getX(), point.getY());
    }

    public static int countColors(Image image) {
        Set<Color> colors = new HashSet<>();
        PixelReader reader = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                colors.add(reader.getColor(i, j));
            }
        }
        return colors.size();
    }

    public static Image getFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                java.awt.Image transferData = (java.awt.Image) transferable.getTransferData(DataFlavor.imageFlavor);

                if (!(transferable instanceof RenderedImage)) {
                    BufferedImage bufferedImage = new BufferedImage(transferData.getWidth(null),
                            transferData.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = bufferedImage.createGraphics();
                    g.drawImage(transferData, 0, 0, null);
                    g.dispose();

                    transferData = bufferedImage;

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write((RenderedImage) transferData, "png", out);
                    out.flush();
                    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                    return new Image(in);
                }
            } catch (UnsupportedFlavorException | IOException e) {
                ExceptionHandler.handle(e);
            }
        }
        return null;
    }

    public static Image fromPlatformImage(PlatformImage platformImage) {
        com.sun.prism.Image prismImage = (com.sun.prism.Image) platformImage;
        int width = prismImage.getWidth();
        int height = prismImage.getHeight();
        WritableImage image = new WritableImage(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int argb = prismImage.getArgb(i, j);
                double a = (0xff & (argb >> 24)) / 255d;
                double r = (0xff & (argb >> 16)) / 255d;
                double g = (0xff & (argb >> 8)) / 255d;
                double b = (0xff & (argb)) / 255d;
                Color color = Color.color(r, g, b, a);
                image.getPixelWriter().setColor(i, j, color);
            }
        }

        return image;
    }

}
