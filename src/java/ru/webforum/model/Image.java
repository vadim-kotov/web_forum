package ru.webforum.model;

public class Image extends MessageObject
{
    private int width;
    private int height;

    public int getWidth() { return this.width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return this.height; }
    public void setHeight(int height) { this.height = height; }
}