package com.example.lagrange.algorithm;

import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.model.DataPoint;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NewtonInterpolationAlgorithm implements InterpolationAlgorithm {
    @Override
    public String getMode() { return "newton"; }

    @Override
    public CalculationResponse calculate(String method, List<DataPoint> points, Double targetX) {
        int count = points.size();
        double[] dividedDifferences = new double[count];
        double[] xValues = new double[count];
        for (int i = 0; i < count; i++) {
            xValues[i] = points.get(i).getX();
            dividedDifferences[i] = points.get(i).getY();
        }

        List<Double> coefficients = new ArrayList<>();
        coefficients.add(dividedDifferences[0]);
        for (int order = 1; order < count; order++) {
            for (int index = count - 1; index >= order; index--) {
                double denominator = xValues[index] - xValues[index - order];
                NumberText.requireFinite(denominator);
                if (denominator == 0.0) throw new IllegalArgumentException("Selected X values must be unique.");
                dividedDifferences[index] = (dividedDifferences[index] - dividedDifferences[index - 1]) / denominator;
                NumberText.requireFinite(dividedDifferences[index]);
            }
            coefficients.add(dividedDifferences[order]);
        }

        CalculationResponse response = new CalculationResponse("newton", method, count, targetX);
        response.setCoefficients(coefficients);
        response.setPolynomial(createPolynomial(coefficients, xValues));
        if (targetX != null) {
            double value = coefficients.get(count - 1);
            for (int i = count - 2; i >= 0; i--) {
                value = coefficients.get(i) + (targetX - xValues[i]) * value;
                NumberText.requireFinite(value);
            }
            response.setValue(value);
        }
        return response;
    }

    private String createPolynomial(List<Double> coefficients, double[] xValues) {
        StringBuilder formula = new StringBuilder("f(x) = ").append(NumberText.format(coefficients.get(0)));
        for (int i = 1; i < coefficients.size(); i++) {
            formula.append(" + (").append(NumberText.format(coefficients.get(i))).append(')');
            for (int factor = 0; factor < i; factor++) {
                formula.append("(x - ").append(NumberText.format(xValues[factor])).append(')');
            }
        }
        return formula.toString();
    }
}
