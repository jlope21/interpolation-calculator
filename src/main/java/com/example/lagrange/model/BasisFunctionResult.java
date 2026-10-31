package com.example.lagrange.model;

public class BasisFunctionResult {
    private final int index;
    private final String polynomial;
    private final Double valueAtTarget;

    public BasisFunctionResult(int index, String polynomial, Double valueAtTarget) {
        this.index = index;
        this.polynomial = polynomial;
        this.valueAtTarget = valueAtTarget;
    }

    public int getIndex() { return index; }
    public String getPolynomial() { return polynomial; }
    public Double getValueAtTarget() { return valueAtTarget; }
}
