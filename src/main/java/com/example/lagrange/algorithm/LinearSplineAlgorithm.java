package com.example.lagrange.algorithm;

import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.model.DataPoint;
import com.example.lagrange.model.SplineIntervalResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LinearSplineAlgorithm implements InterpolationAlgorithm {
    @Override
    public String getMode() { return "spline"; }

    @Override
    public CalculationResponse calculate(String method, List<DataPoint> points, Double targetX) {
        List<DataPoint> sorted = new ArrayList<>(points);
        sorted.sort((left, right) -> Double.compare(left.getX(), right.getX()));
        List<SplineIntervalResult> intervals = new ArrayList<>();
        boolean targetIntervalFound = false;
        for (int i = 0; i < sorted.size() - 1; i++) {
            DataPoint left = sorted.get(i);
            DataPoint right = sorted.get(i + 1);
            double intervalWidth = right.getX() - left.getX();
            NumberText.requireFinite(intervalWidth);
            if (intervalWidth == 0.0) throw new IllegalArgumentException("Selected X values must be unique.");
            double slope = (right.getY() - left.getY()) / intervalWidth;
            NumberText.requireFinite(slope);
            String function = "f" + (i + 1) + "(x) = " + NumberText.format(left.getY()) + " + ("
                    + NumberText.format(slope) + ")(x - " + NumberText.format(left.getX()) + ")";
            boolean containsTarget = targetX != null && !targetIntervalFound
                    && targetX >= left.getX() && targetX <= right.getX();
            if (containsTarget) targetIntervalFound = true;
            intervals.add(new SplineIntervalResult(left.getX(), right.getX(), slope, function, containsTarget));
        }

        CalculationResponse response = new CalculationResponse("spline", "linear", sorted.size(), targetX);
        response.setIntervals(intervals);
        if (targetX != null) {
            for (int i = 0; i < sorted.size() - 1; i++) {
                DataPoint left = sorted.get(i);
                DataPoint right = sorted.get(i + 1);
                if (targetX >= left.getX() && targetX <= right.getX()) {
                    double slope = intervals.get(i).getSlope();
                    double value = left.getY() + slope * (targetX - left.getX());
                    NumberText.requireFinite(value);
                    response.setValue(value);
                    break;
                }
            }
        }
        return response;
    }
}
