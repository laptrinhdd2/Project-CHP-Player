package com.nxchien.chpmusic.model.coordinate;

public class MCoordinate {
    public MCoordinate() {}
    public static class MPoint {
        public MPoint() {
            X = Y = 0;
        }
        public MPoint(double x,double y) {
            X = x;
            Y = y;
        }
        public double X;

        public double getX() {
            return X;
        }

        public void setX(double x) {
            X = x;
        }

        public double getY() {
            return Y;
        }

        public void setY(int y) {
            Y = y;
        }

        public double Y;
    }
}
