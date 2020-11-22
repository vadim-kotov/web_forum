package ru.webforum.model;

public class Image extends MessageObject
{
    private Integer width;
    private Integer height;

    public Integer getWidth() { return this.width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return this.height; }
    public void setHeight(Integer height) { this.height = height; }
}