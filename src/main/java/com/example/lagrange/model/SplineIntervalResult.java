package com.example.lagrange.model;

public class SplineIntervalResult {
    private final double xStart;
    private final double xEnd;
    private final double slope;
    private final String function;
    private final boolean containsTarget;

    public SplineIntervalResult(double xStart, double xEnd, double slope, String function, boolean containsTarget) {
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.slope = slope;
        this.function = function;
        this.containsTarget = containsTarget;
    }

    public double getXStart() { return xStart; }
    public double getXEnd() { return xEnd; }
    public double getSlope() { return slope; }
    public String getFunction() { return function; }
    public boolean isContainsTarget() { return containsTarget; }
}
