package com.example.lagrange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.lagrange.algorithm.InterpolationAlgorithm;
import com.example.lagrange.algorithm.LagrangeInterpolationAlgorithm;
import com.example.lagrange.algorithm.LinearSplineAlgorithm;
import com.example.lagrange.algorithm.NewtonInterpolationAlgorithm;
import com.example.lagrange.model.CalculationRequest;
import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.model.DataPoint;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InterpolationServiceTest {
    private InterpolationService service;

    @BeforeEach
    void setUp() {
        List<InterpolationAlgorithm> algorithms = List.of(
                new NewtonInterpolationAlgorithm(),
                new LagrangeInterpolationAlgorithm(),
                new LinearSplineAlgorithm());
        service = new InterpolationService(algorithms);
    }

    @Test
    void calculatesNewtonLinear() {
        CalculationResponse result = calculate("newton", "linear", points(0, 1, 1, 2), 2.0);
        assertEquals(3.0, result.getValue(), 1e-10);
        assertEquals(List.of(1.0, 1.0), result.getCoefficients());
    }

    @Test
    void calculatesNewtonQuadratic() {
        CalculationResponse result = calculate("newton", "quadratic", points(0, 1, 1, 2, 2, 5), 3.0);
        assertEquals(10.0, result.getValue(), 1e-10);
        assertEquals(List.of(1.0, 1.0, 1.0), result.getCoefficients());
    }

    @Test
    void calculatesNewtonPolynomialWithFourPoints() {
        CalculationResponse result = calculate("newton", "polynomial", points(0, 1, 1, 2, 2, 5, 3, 10), 4.0);
        assertEquals(17.0, result.getValue(), 1e-10);
        assertEquals(List.of(1.0, 1.0, 1.0, 0.0), result.getCoefficients());
    }

    @Test
    void calculatesNewtonPolynomialWithTheReportedFivePointDataset() {
        CalculationResponse result = calculate("newton", "polynomial",
                points(0, 9.81, 2, 9.7487, 4, 9.6879, 6, 9.6879, 8, 9.5682), 4.3);

        assertEquals(5, result.getCoefficients().size());
        assertEquals(9.81, result.getCoefficients().get(0), 1e-12);
        assertEquals(-0.03065, result.getCoefficients().get(1), 1e-12);
        assertEquals(0.0000625, result.getCoefficients().get(2), 1e-12);
        assertEquals(0.00125625, result.getCoefficients().get(3), 1e-12);
        assertEquals(-0.0006270833333333333, result.getCoefficients().get(4), 1e-12);
        assertEquals(9.685713364375, result.getValue(), 1e-12);
        assertEquals(5, result.getSelectedPointCount());
    }

    @Test
    void calculatesLagrangeLinear() {
        CalculationResponse result = calculate("lagrange", "linear", points(0, 1, 1, 2), 2.0);
        assertEquals(3.0, result.getValue(), 1e-10);
        assertEquals(2, result.getBasisFunctions().size());
        assertEquals(2.0, result.getBasisFunctions().get(1).getValueAtTarget(), 1e-10);
    }

    @Test
    void calculatesLagrangeQuadratic() {
        CalculationResponse result = calculate("lagrange", "quadratic", points(0, 1, 1, 2, 2, 5), 3.0);
        assertEquals(10.0, result.getValue(), 1e-10);
        assertEquals(3, result.getBasisFunctions().size());
    }

    @Test
    void calculatesLagrangePolynomialWithFourPoints() {
        CalculationResponse result = calculate("lagrange", "polynomial", points(0, 1, 1, 2, 2, 5, 3, 10), 4.0);
        assertEquals(17.0, result.getValue(), 1e-10);
        assertEquals(4, result.getBasisFunctions().size());
    }

    @Test
    void calculatesSortedSplineIntervals() {
        CalculationResponse result = calculate("spline", "linear", points(2, 3, 0, 0, 1, 2), 1.5);
        assertEquals(2.5, result.getValue(), 1e-10);
        assertEquals(2, result.getIntervals().size());
        assertEquals(0.0, result.getIntervals().get(0).getXStart(), 1e-10);
        assertEquals(1.0, result.getIntervals().get(0).getXEnd(), 1e-10);
        assertEquals(true, result.getIntervals().get(1).isContainsTarget());
    }

    @Test
    void allowsSplineFunctionsWithoutTargetValue() {
        CalculationResponse result = calculate("spline", "linear", points(0, 0, 1, 2), null);
        assertEquals(1, result.getIntervals().size());
        assertNull(result.getValue());
    }

    @Test
    void rejectsDuplicateXValues() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculate("newton", "linear", points(0, 1, 0, 2), 1.0));
        assertEquals("Selected X values must be unique.", error.getMessage());
    }

    @Test
    void rejectsMissingNumericCoordinates() {
        List<DataPoint> points = List.of(new DataPoint(0.0, 1.0), new DataPoint(1.0, null));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculate("newton", "linear", points, 2.0));
        assertEquals("Each selected point needs a numeric Y value.", error.getMessage());
    }

    @Test
    void rejectsFewerThanTwoSelectedPoints() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculate("newton", "polynomial", points(0, 1), 2.0));
        assertEquals("Select at least 2 data points.", error.getMessage());
    }

    @Test
    void rejectsWrongPointCountInsteadOfSelectingForTheUser() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculate("newton", "linear", points(0, 1, 1, 2, 2, 5), 2.0));
        assertEquals("Linear requires exactly 2 selected points.", error.getMessage());
    }

    @Test
    void rejectsWrongQuadraticPointCount() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculate("newton", "quadratic", points(0, 1, 1, 2), 2.0));
        assertEquals("Quadratic requires exactly 3 selected points.", error.getMessage());
    }

    @Test
    void rejectsSplineTargetOutsideThePointRange() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculate("spline", "linear", points(0, 0, 1, 2), 1.5));
        assertEquals("Target X must be within the selected X range.", error.getMessage());
    }

    private CalculationResponse calculate(String mode, String method, List<DataPoint> points, Double targetX) {
        CalculationRequest request = new CalculationRequest();
        request.setMode(mode);
        request.setMethod(method);
        request.setPoints(points);
        request.setTargetX(targetX);
        return service.calculate(request);
    }

    private List<DataPoint> points(double... values) {
        java.util.ArrayList<DataPoint> points = new java.util.ArrayList<>();
        for (int i = 0; i < values.length; i += 2) {
            points.add(new DataPoint(values[i], values[i + 1]));
        }
        return points;
    }
}
