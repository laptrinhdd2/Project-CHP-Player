package com.nxchien.chpmusic.ui.widget;

import android.graphics.PointF;

public class CPointF extends PointF {
    public CPointF getPointAround(float distance, float degree) {
        degree = -degree + 90;
        CPointF p = new CPointF();
        p.x = (float) (x + distance * Math.cos(Math.toRadians(degree)));
        p.y = (float) (y - distance * Math.sin(Math.toRadians(degree)));
        return p;
    }

    public float fromCPointFToDegree_From0h(CPointF point) {
        float angle = (float) getAngle(point);
        angle += 90;
        if (angle > 360) angle %= 360;
        if (angle > 180) angle = -(360 - angle);
        return angle;
    }

    public float fromCPointFToDegree_From0h(float s_x, float s_y) {
        float angle = (float) getAngle(s_x, s_y);
        angle += 90;
        if (angle > 360) angle %= 360;
        if (angle > 180) angle = -(360 - angle);
        return angle;
    }

    public double getAngle(CPointF screenPoint) {
        double dx = screenPoint.x - x;
        double dy = -(screenPoint.y - y);
        double inRads = Math.atan2(dy, dx);
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return Math.toDegrees(inRads);
    }

    public double getAngle(float s_x, float s_y) {
        double dx = s_x - x;
        double dy = -(s_y - y);
        double inRads = Math.atan2(dy, dx);
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return Math.toDegrees(inRads);
    }

    private CPointF() {

    }

    public CPointF(final CPointF c) {
        this.x = c.x;
        this.y = c.y;
    }

    public CPointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public double getDistance(CPointF c) {
        return Math.sqrt(Math.pow(x - c.x, 2) + Math.pow((y - c.y), 2));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
