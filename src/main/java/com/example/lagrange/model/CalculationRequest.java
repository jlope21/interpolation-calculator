package com.example.lagrange.model;

import java.util.List;

/** JSON input accepted by the interpolation endpoint. */
public class CalculationRequest {
    private String mode;
    private String method;
    private List<DataPoint> points;
    private Double targetX;

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public List<DataPoint> getPoints() { return points; }
    public void setPoints(List<DataPoint> points) { this.points = points; }
    public Double getTargetX() { return targetX; }
    public void setTargetX(Double targetX) { this.targetX = targetX; }
}
