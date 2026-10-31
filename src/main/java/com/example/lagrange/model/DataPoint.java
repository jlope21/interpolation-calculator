package com.example.lagrange.model;

/** One numeric (x, y) sample used by an interpolation request. */
public class DataPoint {
    private Double x;
    private Double y;

    public DataPoint() { }

    public DataPoint(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
}
