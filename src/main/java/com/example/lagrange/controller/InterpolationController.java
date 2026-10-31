package com.example.lagrange.controller;

import com.example.lagrange.model.CalculationRequest;
import com.example.lagrange.model.CalculationResponse;
import com.example.lagrange.service.InterpolationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interpolation")
public class InterpolationController {
    private final InterpolationService interpolationService;

    public InterpolationController(InterpolationService interpolationService) {
        this.interpolationService = interpolationService;
    }

    @PostMapping("/calculate")
    public CalculationResponse calculate(@RequestBody CalculationRequest request) {
        return interpolationService.calculate(request);
    }
}
