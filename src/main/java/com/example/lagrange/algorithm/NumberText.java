package com.example.lagrange.algorithm;

import java.math.BigDecimal;

final class NumberText {
    private NumberText() { }

    static String format(double value) {
        if (value == 0.0) return "0";
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    static void requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("The calculation exceeded the supported numeric range.");
        }
    }
}
