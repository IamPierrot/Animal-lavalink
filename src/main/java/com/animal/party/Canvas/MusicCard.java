package com.animal.party.Canvas;

import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.batik.transcoder.TranscoderException;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MusicCard {
    public static ByteArrayInputStream generateImage(MusicCardOption option) throws IOException, TranscoderException {
        if (option.getProgress() == null) option.setProgress(10);
        if (option.getName() == null) option.setName("IamPierrot");
        if (option.getAuthor() == null) option.setAuthor("By Pierrot");

        if (option.getProgressBarColor() == null) option.setProgressBarColor("#5F2D00");
        if (option.getProgressColor() == null) option.setProgressColor("#FF7A00");
        if (option.getBackgroundColor() == null) option.setBackgroundColor("#070707");
        if (option.getNameColor() == null) option.setNameColor("#FF7A00");
        if (option.getAuthorColor() == null) option.setAuthorColor("#FFFFFF");
        if (option.getImageDarkness() == null) option.setImageDarkness(10);

        String noImageSvg = ImageUtils.generateSvg("<svg width=\"837\" height=\"837\" viewBox=\"0 0 837 837\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "    <rect width=\"837\" height=\"837\" fill=\"" + option.getProgressColor() + "\"/>\n" +
                "    <path d=\"M419.324 635.912C406.035 635.912 394.658 631.18 385.195 621.717C375.732 612.254 371 600.878 371 587.589C371 574.3 375.732 562.923 385.195 553.46C394.658 543.997 406.035 539.265 419.324 539.265C432.613 539.265 443.989 543.997 453.452 553.46C462.915 562.923 467.647 574.3 467.647 587.589C467.647 600.878 462.915 612.254 453.452 621.717C443.989 631.18 432.613 635.912 419.324 635.912ZM371 490.941V201H467.647V490.941H371Z\" fill=\"" + option.getBackgroundColor() + "\"/>\n" +
                "    </svg>");

        if (option.getThumbnailImage() == null) {
            option.setThumbnailImage(noImageSvg);
        }

        BufferedImage thumbnail;

        try {
            thumbnail = ImageUtils.cropImage(new CropOption()
                    .setImagePath(option.getThumbnailImage())
                    .setCircle(true)
                    .setWidth(400)
                    .setHeight(400)
                    .setX(0)
                    .setY(0)
                    .setCropCenter(true));
        } catch (Exception e) {
            thumbnail = ImageUtils.cropImage(new CropOption()
                    .setImagePath(noImageSvg)
                    .setCircle(true)
                    .setWidth(400)
                    .setHeight(400)
                    .setX(0)
                    .setY(0)
                    .setCropCenter(true));
        }

        if (option.getProgress() < 10) {
            option.setProgress(10);
        } else if (option.getProgress() >= 100) {
            option.setProgress(100);
        }

        if (option.getName().length() > 20) {
            option.setName(option.getName().substring(0, 20) + "...");
        }

        if (option.getAuthor().length() > 20) {
            option.setAuthor(option.getAuthor().substring(0, 20) + "...");
        }

        BufferedImage canvas = new BufferedImage(2367, 520, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx = canvas.createGraphics();

        if (option.getBackgroundImage() != null) {
            try {
                String darknessSvg = ImageUtils.generateSvg("<svg width=\"2367\" height=\"520\" viewBox=\"0 0 2367 520\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                        "                <path d=\"M0 0H2367V520H0V0Z\" fill=\"#070707\" fill-opacity=\"" + (option.getImageDarkness() / 100.0) + "\"/>\n" +
                        "                </svg>");

                BufferedImage image = ImageUtils.cropImage(new CropOption()
                        .setImagePath(option.getBackgroundImage())
                        .setX(0)
                        .setY(0)
                        .setWidth(2367)
                        .setHeight(520)
                        .setCropCenter(true)
                );
                ctx.drawImage(image, 0, 0, null);
                ctx.drawImage(ImageUtils.loadImage(darknessSvg), 0, 0, null);
            } catch (Exception e) {
                String backgroundSvg = ImageUtils.generateSvg("<svg width=\"2367\" height=\"520\" viewBox=\"0 0 2367 520\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                        "                <path d=\"M0 260C0 116.406 116.406 0 260 0H2107C2250.59 0 2367 116.406 2367 260V260C2367 403.594 2250.59 520 2107 520H260C116.406 520 0 403.594 0 260V260Z\" fill=\"" + option.getBackgroundColor() + "\"/>\n" +
                        "                </svg>");

                BufferedImage background = ImageUtils.loadImage(backgroundSvg);
                ctx.drawImage(background, 0, 0, null);
            }
        } else {
            String backgroundSvg = ImageUtils.generateSvg("<svg width=\"2367\" height=\"520\" viewBox=\"0 0 2367 520\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                    "    <path d=\"M0 260C0 116.406 116.406 0 260 0H2107C2250.59 0 2367 116.406 2367 260V260C2367 403.594 2250.59 520 2107 520H260C116.406 520 0 403.594 0 260V260Z\" fill=\"" + option.getBackgroundColor() + "\"/>\n" +
                    "    </svg>");

            BufferedImage background = ImageUtils.loadImage(backgroundSvg);
            ctx.drawImage(background, 0, 0, null);
        }

        ctx.drawImage(thumbnail, 69, 61, null);

        ctx.setColor(Color.decode(option.getProgressBarColor()));
        ctx.setStroke(new BasicStroke(35));
        ctx.drawOval(1945, 105, 310, 310);

        double angle = (option.getProgress() / 100.0) * 2 * Math.PI;

        ctx.setColor(Color.decode(option.getProgressColor()));
        ctx.setStroke(new BasicStroke(35));
        ctx.drawArc(1945, 105, 310, 310, -90, (int) Math.toDegrees(angle));

        ctx.setColor(Color.decode(option.getNameColor()));
        ctx.setFont(new Font("Extrabold", Font.BOLD, 100));
        ctx.drawString(option.getName(), 550, 240);

        ctx.setColor(Color.decode(option.getAuthorColor()));
        ctx.setFont(new Font("Semibold", Font.BOLD, 70));
        ctx.drawString(option.getAuthor(), 550, 350);

        ctx.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(canvas, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static FileUpload getMusicCard(TrackInfo trackInfo) throws TranscoderException, IOException {
        String[] colorPalette = {
                "#070707",
                "#0D0D0D",
                "#1A1A1A",
                "#262626",
                "#333333",
                "#404040"
        };
        var artworkImage = trackInfo.getArtworkUrl();

        Random random = new Random();
        String backGroundColor = colorPalette[random.nextInt(colorPalette.length)];
        List<Color> lightColors = getLightColors(artworkImage);
        Color dominantColor = lightColors.getFirst();
        MusicCardOption option = getMusicCardOption(dominantColor, artworkImage, backGroundColor, trackInfo);

        return FileUpload.fromData(generateImage(option), "card.png");
    }

    private static @NotNull MusicCardOption getMusicCardOption(Color dominantColor, String artworkImage, String backGroundColor, TrackInfo trackInfo) {
        String dominantColorHex = rgbToHex(dominantColor.getRed(), dominantColor.getGreen(), dominantColor.getBlue());

        MusicCardOption option = new MusicCardOption();
        option.setBackgroundImage(artworkImage);
        option.setImageDarkness(80);
        option.setThumbnailImage(artworkImage);
        option.setBackgroundColor(backGroundColor);
        option.setProgress(1);
        option.setProgressColor(dominantColorHex);
        option.setProgressBarColor("#9C9C9C");
        option.setName(trackInfo.getTitle());
        option.setNameColor(dominantColorHex);
        option.setAuthor(trackInfo.getAuthor());
        option.setAuthorColor("#696969");
        option.setStartTime("0:10");
        option.setEndTime("3:45");
        option.setTimeColor(dominantColorHex);
        return option;
    }

    private static List<Color> getLightColors(String imageUrl) throws TranscoderException, IOException {
        BufferedImage image = ImageUtils.loadImage(imageUrl);
        List<Color> lightColors = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = new Color(image.getRGB(x, y));
                if (isLightColor(color)) {
                    lightColors.add(color);
                    break;
                }
            }
            if (!lightColors.isEmpty()) break;
        }

        return lightColors;
    }

    private static boolean isLightColor(Color color) {
        double brightness = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return brightness > 0.7;
    }


    // Method to convert RGB to Hex
    public static String rgbToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

}
