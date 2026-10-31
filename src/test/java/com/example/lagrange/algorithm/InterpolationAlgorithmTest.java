package com.example.lagrange.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.model.DataPoint;
import java.util.List;
import org.junit.jupiter.api.Test;

class InterpolationAlgorithmTest {
    private static final double TOLERANCE = 1e-6;

    private final NewtonInterpolationAlgorithm newton = new NewtonInterpolationAlgorithm();
    private final LagrangeInterpolationAlgorithm lagrange = new LagrangeInterpolationAlgorithm();
    private final LinearSplineAlgorithm spline = new LinearSplineAlgorithm();

    @Test
    void allMethodsReproduceTheLinearFunction() {
        List<DataPoint> points = points(0, 1, 1, 3, 2, 5);

        assertValue(newton.calculate("polynomial", points, 1.5), 4.0);
        assertValue(lagrange.calculate("polynomial", points, 1.5), 4.0);
        assertValue(spline.calculate("linear", points, 1.5), 4.0);
    }

    @Test
    void polynomialMethodsAndSplineEvaluateTheQuadraticDataset() {
        List<DataPoint> points = points(0, 0, 1, 1, 2, 4);

        assertValue(newton.calculate("quadratic", points, 1.5), 2.25);
        assertValue(lagrange.calculate("quadratic", points, 1.5), 2.25);
        assertValue(spline.calculate("linear", points, 1.5), 2.5);
    }

    @Test
    void allMethodsReturnTheValueAtAnExactDataPoint() {
        List<DataPoint> points = points(0, 0, 1, 1, 2, 4);

        assertValue(newton.calculate("quadratic", points, 1.0), 1.0);
        assertValue(lagrange.calculate("quadratic", points, 1.0), 1.0);
        assertValue(spline.calculate("linear", points, 1.0), 1.0);
    }

    private static void assertValue(CalculationResponse response, double expected) {
        assertNotNull(response.getValue(), "Expected an evaluated result");
        assertEquals(expected, response.getValue(), TOLERANCE);
    }

    private static List<DataPoint> points(double... coordinates) {
        java.util.ArrayList<DataPoint> points = new java.util.ArrayList<>();
        for (int index = 0; index < coordinates.length; index += 2) {
            points.add(new DataPoint(coordinates[index], coordinates[index + 1]));
        }
        return points;
    }
}
