package com.nxchien.chpmusic.model;

public class Rectangle {
    public int Left;
    public int Top;
    public int Width;
    public int Height;

    public Rectangle() {

    }

    public int getLeft() {
        return Left;
    }

    public int getTop() {
        return Top;
    }

    public int getWidth() {
        return Width;
    }

    public int getHeight() {
        return Height;
    }

    public void setLeft(int left) {
        Left = left;
    }

    public void setTop(int top) {
        Top = top;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public void setSize(int[] wh){
        Width = wh[0];
        Height = wh[1];
    }
}