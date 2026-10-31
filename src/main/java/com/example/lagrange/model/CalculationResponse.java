package com.example.lagrange.model;

import java.util.List;

/** Result fields are populated by the selected interpolation algorithm. */
public class CalculationResponse {
    private String mode;
    private String method;
    private int selectedPointCount;
    private Double targetX;
    private Double value;
    private List<Double> coefficients;
    private String polynomial;
    private List<BasisFunctionResult> basisFunctions;
    private List<SplineIntervalResult> intervals;

    public CalculationResponse(String mode, String method, int selectedPointCount, Double targetX) {
        this.mode = mode;
        this.method = method;
        this.selectedPointCount = selectedPointCount;
        this.targetX = targetX;
    }

    public String getMode() { return mode; }
    public String getMethod() { return method; }
    public int getSelectedPointCount() { return selectedPointCount; }
    public Double getTargetX() { return targetX; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public List<Double> getCoefficients() { return coefficients; }
    public void setCoefficients(List<Double> coefficients) { this.coefficients = coefficients; }
    public String getPolynomial() { return polynomial; }
    public void setPolynomial(String polynomial) { this.polynomial = polynomial; }
    public List<BasisFunctionResult> getBasisFunctions() { return basisFunctions; }
    public void setBasisFunctions(List<BasisFunctionResult> basisFunctions) { this.basisFunctions = basisFunctions; }
    public List<SplineIntervalResult> getIntervals() { return intervals; }
    public void setIntervals(List<SplineIntervalResult> intervals) { this.intervals = intervals; }
}
