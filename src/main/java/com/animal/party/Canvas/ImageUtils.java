package com.animal.party.Canvas;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtils {

    public static BufferedImage loadImage(String imagePath) throws IOException, TranscoderException {
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return loadImageFromUrl(imagePath);
        } else if (imagePath.startsWith("data:image")) {
            String base64Image = imagePath.split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

            if (imagePath.startsWith("data:image/svg+xml")) {
                return loadSvgImage(bis);
            } else {
                return ImageIO.read(bis);
            }
        } else if (imagePath.endsWith(".svg")) {
            return loadSvgImage(new FileInputStream(imagePath));
        } else {
            return ImageIO.read(new File(imagePath));
        }
    }

    private static BufferedImage loadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    private static BufferedImage loadSvgImage(InputStream inputStream) throws TranscoderException, IOException {
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outputStream);
        transcoder.transcode(input, output);
        outputStream.flush();

        byte[] imageBytes = outputStream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bis);
    }

    public static BufferedImage cropImage(CropOption option) throws IOException, TranscoderException {
        BufferedImage image = loadImage(option.getImagePath());
        int width = option.getWidth() != null ? option.getWidth() : image.getWidth();
        int height = option.getHeight() != null ? option.getHeight() : image.getHeight();

        double scaleWidth = (double) width / image.getWidth();
        double scaleHeight = (double) height / image.getHeight();
        double scaleFactor = Math.max(scaleWidth, scaleHeight);

        int scaledWidth = (int) (image.getWidth() * scaleFactor);
        int scaledHeight = (int) (image.getHeight() * scaleFactor);

        int x = 0, y = 0;
        if (option.isCropCenter()) {
            x = (width - scaledWidth) / 2;
            y = (height - scaledHeight) / 2;
        } else {
            x = (int) (option.getX() - (width - image.getWidth() * scaleFactor) / 2);
            y = (int) (option.getY() - (height - image.getHeight() * scaleFactor) / 2);
        }

        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        if (option.isCircle()) {
            g2d.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, width, height));
        } else if (option.getBorderRadius() > 0) {
            g2d.setClip(new RoundRectangle2D.Double(0, 0, width, height, option.getBorderRadius(), option.getBorderRadius()));
        }

        g2d.drawImage(image, x, y, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return canvas;
    }

    private static String correctSvgNamespace(String svgContent) {
        return svgContent.replace("http://www.w3.org.2000/svg", "http://www.w3.org/2000/svg");
    }

    public static String generateSvg(String svgContent) {
        // Correct SVG namespace
        svgContent = correctSvgNamespace(svgContent);
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(svgContent.getBytes());
    }

    public static void saveImage(BufferedImage image, String formatName, String filePath) throws IOException {
        File file = new File(filePath);
        ImageOutputStream ios = ImageIO.createImageOutputStream(file);
        ImageWriter writer = ImageIO.getImageWritersByFormatName(formatName).next();
        writer.setOutput(ios);
        writer.write(image);
        writer.dispose();
        ios.close();
    }

    public static void saveImage(byte[] imageData, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(imageData);
        }
    }
}
