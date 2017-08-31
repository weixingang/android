package com.seu.magicfilter.model;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by wxg on 16/06/21
 * 用于记录每个TextView的状态
 */
public class TextViewParams implements  Serializable {
    public String tag;
    public float textSize;
    public Point midPoint;
    public float rotation;
    public float scale;
    public String content;
    public int width;
    public int height;
    public float x;
    public float y;
    public int textColor;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return "TextViewParams{" +
                "tag='" + tag + '\'' +
                ", textSize=" + textSize +
                ", midPoint=" + midPoint +
                ", rotation=" + rotation +
                ", scale=" + scale +
                ", content='" + content + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", x=" + x +
                ", y=" + y +
                ", textColor=" + textColor +
                '}';
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public Point getMidPoint() {
        return midPoint;
    }

    public void setMidPoint(Point midPoint) {
        this.midPoint = midPoint;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}