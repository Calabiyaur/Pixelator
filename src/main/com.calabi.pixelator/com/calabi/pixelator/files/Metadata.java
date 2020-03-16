package com.calabi.pixelator.files;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageInputStream;

public final class Metadata {

    // Node:
    private static final String GRAPHIC_CONTROL_EXTENSION = "GraphicControlExtension";
    // Attributes:
    private static final String DISPOSAL_METHOD = "disposalMethod";
    private static final String USER_INPUT_FLAG = "userInputFlag";
    private static final String DELAY_TIME = "delayTime";
    private static final String TRANSPARENT_COLOR_INDEX = "transparentColorIndex";

    // Node:
    private static final String APPLICATION_EXTENSIONS = "ApplicationExtensions";
    // Sub-node:
    private static final String APPLICATION_EXTENSION = "ApplicationExtension";
    // Attributes:
    private static final String APPLICATION_ID = "applicationID";
    private static final String AUTHENTICATION_CODE = "authenticationCode";

    // Default metadata:
    private static final Metadata DEFAULT = new Metadata();

    static {
        DEFAULT.disposalMethod = "restoreToBackgroundColor";
        DEFAULT.userInputFlag = "FALSE";
        DEFAULT.delayTime = "6";
        DEFAULT.transparentColorIndex = "0";
        DEFAULT.applicationID = "NETSCAPE";
        DEFAULT.authenticationCode = "2.0";
        DEFAULT.userObject = new byte[] { 0x1, 0x0, 0x0 };
    }

    private String disposalMethod;
    private String userInputFlag;
    private String delayTime;
    private String transparentColorIndex;
    private String applicationID;
    private String authenticationCode;
    private byte[] userObject;

    private Metadata() {
    }

    public static Metadata read(File input) {

        Metadata metadata;

        try {
            ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
            reader.setInput(new FileImageInputStream(input));

            IIOMetadata iioMetadata = reader.getImageMetadata(0);
            IIOMetadataNode root = (IIOMetadataNode) iioMetadata.getAsTree(iioMetadata.getNativeMetadataFormatName());

            metadata = new Metadata();

            for (int i = 0; i < root.getChildNodes().getLength(); i++) {
                IIOMetadataNode child = (IIOMetadataNode) root.getChildNodes().item(i);
                if (GRAPHIC_CONTROL_EXTENSION.equals(child.getNodeName())) {
                    metadata.delayTime = child.getAttribute(DELAY_TIME);
                    break;
                }
            }
        } catch (IOException ignored) {
            return null;
        }

        return metadata;
    }

    public static IIOMetadata write(ImageWriter writer, Metadata metadata) throws IOException {

        if (metadata == null) {
            metadata = DEFAULT;
        }

        ImageTypeSpecifier type = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
        IIOMetadata iioMetadata = writer.getDefaultImageMetadata(type, writer.getDefaultWriteParam());

        String formatName = iioMetadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) iioMetadata.getAsTree(formatName);

        IIOMetadataNode graphicControlExtensions = getNode(root, GRAPHIC_CONTROL_EXTENSION);
        graphicControlExtensions.setAttribute(DISPOSAL_METHOD, DEFAULT.disposalMethod);
        graphicControlExtensions.setAttribute(USER_INPUT_FLAG, DEFAULT.userInputFlag);
        graphicControlExtensions.setAttribute(DELAY_TIME, metadata.delayTime);
        graphicControlExtensions.setAttribute(TRANSPARENT_COLOR_INDEX, DEFAULT.transparentColorIndex);

        IIOMetadataNode applicationExtensions = getNode(root, APPLICATION_EXTENSIONS);
        IIOMetadataNode child = new IIOMetadataNode(APPLICATION_EXTENSION);
        child.setAttribute(APPLICATION_ID, DEFAULT.applicationID);
        child.setAttribute(AUTHENTICATION_CODE, DEFAULT.authenticationCode);
        child.setUserObject(DEFAULT.userObject);
        applicationExtensions.appendChild(child);

        iioMetadata.setFromTree(formatName, root);

        return iioMetadata;
    }

    private static IIOMetadataNode getNode(IIOMetadataNode parent, String name) {
        IIOMetadataNode node = (IIOMetadataNode) parent.getFirstChild();
        while (!name.equalsIgnoreCase(node.getNodeName())) {
            node = (IIOMetadataNode) node.getNextSibling();
            if (node == null) {
                break;
            }
        }
        if (node == null) {
            node = new IIOMetadataNode(name);
            parent.appendChild(node);
        }
        return node;
    }

    /**
     * Return delay time in milliseconds.
     * (Conversion from String representing delay time in centiseconds)
     */
    public int getDelayTime() {
        return Integer.parseInt(delayTime) * 10;
    }

    /**
     * Set delay time in milliseconds.
     * (Conversion to String representing delay time in centiseconds)
     */
    public void setDelayTime(int delayTime) {
        this.delayTime = Integer.toString(delayTime / 10);
    }

}
