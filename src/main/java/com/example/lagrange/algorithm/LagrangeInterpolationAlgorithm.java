package com.example.lagrange.algorithm;

import com.example.lagrange.model.BasisFunctionResult;
import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.model.DataPoint;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LagrangeInterpolationAlgorithm implements InterpolationAlgorithm {
    @Override
    public String getMode() { return "lagrange"; }

    @Override
    public CalculationResponse calculate(String method, List<DataPoint> points, Double targetX) {
        List<BasisFunctionResult> basisFunctions = new ArrayList<>();
        double value = 0.0;
        StringBuilder polynomial = new StringBuilder("f(x) = ");

        for (int i = 0; i < points.size(); i++) {
            DataPoint point = points.get(i);
            double denominator = 1.0;
            StringBuilder basis = new StringBuilder("L").append(i).append("(x) = ");
            for (int j = 0; j < points.size(); j++) {
                if (j == i) continue;
                double otherX = points.get(j).getX();
                denominator *= point.getX() - otherX;
                NumberText.requireFinite(denominator);
                basis.append("(x - ").append(NumberText.format(otherX)).append(')');
            }
            if (denominator == 0.0) throw new IllegalArgumentException("Selected X values must be unique.");
            if (points.size() > 1) {
                basis.append(" / ").append(NumberText.format(denominator));
            } else {
                basis.append('1');
            }

            Double basisValue = null;
            if (targetX != null) {
                double numerator = 1.0;
                for (int j = 0; j < points.size(); j++) {
                    if (j != i) numerator *= targetX - points.get(j).getX();
                }
                NumberText.requireFinite(numerator);
                basisValue = numerator / denominator;
                NumberText.requireFinite(basisValue);
                value += point.getY() * basisValue;
                NumberText.requireFinite(value);
            }
            if (i > 0) polynomial.append(" + ");
            polynomial.append('(').append(NumberText.format(point.getY())).append(")L").append(i).append("(x)");
            basisFunctions.add(new BasisFunctionResult(i, basis.toString(), basisValue));
        }

        CalculationResponse response = new CalculationResponse("lagrange", method, points.size(), targetX);
        response.setBasisFunctions(basisFunctions);
        response.setPolynomial(polynomial.toString());
        if (targetX != null) response.setValue(value);
        return response;
    }
}
