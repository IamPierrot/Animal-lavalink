package com.animal.party.Canvas;

public class CropOption {
    private String imagePath;
    private Integer width;
    private Integer height;
    private boolean cropCenter;
    private int x;
    private int y;
    private boolean circle;
    private int borderRadius;

    public String getImagePath() {
        return imagePath;
    }

    public CropOption setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public CropOption setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public CropOption setHeight(Integer height) {

        this.height = height;
        return this;
    }

    public boolean isCropCenter() {
        return cropCenter;
    }

    public CropOption setCropCenter(boolean cropCenter) {
        this.cropCenter = cropCenter;
        return this;
    }

    public int getX() {
        return x;
    }

    public CropOption setX(int x) {

        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public CropOption setY(int y) {
        this.y = y;
        return this;
    }

    public boolean isCircle() {
        return circle;
    }

    public CropOption setCircle(boolean circle) {
        this.circle = circle;
        return this;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    public CropOption setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }
}
