package com.example.lagrange.algorithm;

import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.model.DataPoint;
import java.util.List;

/** Common contract used by the service to select an algorithm polymorphically. */
public interface InterpolationAlgorithm {
    String getMode();

    CalculationResponse calculate(String method, List<DataPoint> points, Double targetX);
}
