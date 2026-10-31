package com.example.lagrange.service;

import com.example.lagrange.algorithm.InterpolationAlgorithm;
import com.example.lagrange.model.CalculationRequest;
import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.model.DataPoint;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class InterpolationService {
    private final List<InterpolationAlgorithm> algorithms;

    public InterpolationService(List<InterpolationAlgorithm> algorithms) {
        this.algorithms = algorithms;
    }

    public CalculationResponse calculate(CalculationRequest request) {
        if (request == null) throw new IllegalArgumentException("Calculation input is required.");
        String mode = normalize(request.getMode(), "Select an interpolation mode.");
        String method = normalize(request.getMethod(), "Select a calculation method.");
        List<DataPoint> points = request.getPoints();
        if (points == null || points.size() < 2) {
            throw new IllegalArgumentException("Select at least 2 data points.");
        }
        if (request.getTargetX() != null && !Double.isFinite(request.getTargetX())) {
            throw new IllegalArgumentException("Target X must be a finite number.");
        }
        validatePoints(points);
        validateMethodAndCount(mode, method, points.size());

        if (mode.equals("spline") && request.getTargetX() != null) {
            double minX = points.stream().mapToDouble(DataPoint::getX).min().orElseThrow();
            double maxX = points.stream().mapToDouble(DataPoint::getX).max().orElseThrow();
            if (request.getTargetX() < minX || request.getTargetX() > maxX) {
                throw new IllegalArgumentException("Target X must be within the selected X range.");
            }
        }

        InterpolationAlgorithm algorithm = algorithms.stream()
                .filter(candidate -> candidate.getMode().equals(mode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Choose Newton, Lagrange, or Spline mode."));
        return algorithm.calculate(method, points, request.getTargetX());
    }

    private String normalize(String value, String errorMessage) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(errorMessage);
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private void validatePoints(List<DataPoint> points) {
        for (int i = 0; i < points.size(); i++) {
            DataPoint point = points.get(i);
            if (point == null || point.getX() == null || !Double.isFinite(point.getX())) {
                throw new IllegalArgumentException("Each selected point needs a numeric X value.");
            }
            if (point.getY() == null || !Double.isFinite(point.getY())) {
                throw new IllegalArgumentException("Each selected point needs a numeric Y value.");
            }
            for (int previous = 0; previous < i; previous++) {
                if (point.getX().doubleValue() == points.get(previous).getX().doubleValue()) {
                    throw new IllegalArgumentException("Selected X values must be unique.");
                }
            }
        }
    }

    private void validateMethodAndCount(String mode, String method, int count) {
        if (mode.equals("spline")) {
            if (!method.equals("linear")) throw new IllegalArgumentException("Spline only supports the Linear method.");
            return;
        }
        if (!mode.equals("newton") && !mode.equals("lagrange")) {
            throw new IllegalArgumentException("Choose Newton, Lagrange, or Spline mode.");
        }
        switch (method) {
            case "linear" -> {
                if (count != 2) throw new IllegalArgumentException("Linear requires exactly 2 selected points.");
            }
            case "quadratic" -> {
                if (count != 3) throw new IllegalArgumentException("Quadratic requires exactly 3 selected points.");
            }
            case "polynomial" -> {
                if (count < 2) throw new IllegalArgumentException("Polynomial requires at least 2 selected points.");
            }
            default -> throw new IllegalArgumentException("Choose Linear, Quadratic, or Polynomial method.");
        }
    }
}
