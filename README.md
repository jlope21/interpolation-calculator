# Interpolation Calculator

## 1. Project Overview

Interpolation Calculator is a web-based application for estimating values from a set of data points using numerical interpolation methods. Users enter X and Y coordinates, choose a method, and optionally evaluate the interpolation at a target X value.

## 2. Features

- **Newton interpolation** with Linear, Quadratic, and Polynomial methods.
- **Lagrange interpolation** with Linear, Quadratic, and Polynomial methods.
- **Linear spline interpolation** using piecewise linear functions.
- Editable X and Y values in a data-point table.
- Select individual data points or all points for a calculation.
- Add and delete data-point rows.
- Enter an optional Target X value.
- View method-specific results: Newton coefficients and polynomial equations, Lagrange basis functions and interpolation results, or spline interval functions and results.
- Keyboard navigation in the data table and Ctrl+Z undo for table edits.

Newton and Lagrange Linear methods require exactly two selected points; Quadratic requires exactly three; Polynomial accepts two or more. Spline supports the Linear method and requires at least two points. A target value for a spline must lie within the selected X range.

## 3. Technologies Used

- Java 21
- Spring Boot 4.1.2
- Maven, with the Maven Wrapper included
- HTML
- CSS
- JavaScript

## 4. Project Structure

```text
.
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/lagrange/
    │   │   ├── algorithm/
    │   │   │   ├── InterpolationAlgorithm.java
    │   │   │   ├── LagrangeInterpolationAlgorithm.java
    │   │   │   ├── LinearSplineAlgorithm.java
    │   │   │   ├── NewtonInterpolationAlgorithm.java
    │   │   │   └── NumberText.java
    │   │   ├── controller/
    │   │   │   ├── ApiExceptionHandler.java
    │   │   │   └── InterpolationController.java
    │   │   ├── model/
    │   │   │   ├── ApiError.java
    │   │   │   ├── BasisFunctionResult.java
    │   │   │   ├── CalculationRequest.java
    │   │   │   ├── CalculationResponse.java
    │   │   │   ├── DataPoint.java
    │   │   │   └── SplineIntervalResult.java
    │   │   ├── service/
    │   │   │   └── InterpolationService.java
    │   │   └── LagrangeApplication.java
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           ├── index.html
    │           ├── script.js
    │           └── style.css
    └── test/java/com/example/lagrange/
        ├── LagrangeApplicationTests.java
        └── service/InterpolationServiceTest.java
```

## 5. Prerequisites

- Java 21
- A terminal or command prompt

The project includes Maven Wrapper scripts, which can run the build without a separately installed Maven command.

## 6. How to Run the Project

From the project root, start the application with the appropriate command for your operating system.

**Windows PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS/Linux:**

```bash
./mvnw spring-boot:run
```

When the application has started, open [http://localhost:8080](http://localhost:8080) in a browser.

## 7. How to Use

1. Open the application at [http://localhost:8080](http://localhost:8080).
2. Enter X and Y values in the data-point table. Add rows as needed and select the points to include.
3. Select an interpolation mode and one of its available methods.
4. Enter a Target X value if an evaluated result is needed. The field may be left empty to view method equations without evaluating at a target.
5. Click **Calculate**.
6. Review the method-specific equations and, when a target was provided, the final evaluated result in the Results section.

## 8. Project Purpose

This project is an educational application for exploring and experimenting with numerical interpolation methods using user-supplied data points.
