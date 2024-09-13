package org.example;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TheShapeFixer {
    // Method to check if a shape is valid
    public boolean isValid(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        if (points.size() < 3) {
            return false; // A valid polygon must have at least 3 points
        }

        // The first and last points must be the same
        Point2D first = points.get(0);
        Point2D last = points.get(points.size() - 1);
        if (!first.equals(last)) {
            return false;
        }

        // Check for repeated points (except the first and last)
        Set<Point2D> pointSet = new HashSet<>(points.subList(0, points.size() - 1));
        if (pointSet.size() != points.size() - 1) {
            return false; // Shape has repeated points
        }

        // Additional logic can be added to check for intersection of lines
        // and other geometric validation for non-simple polygons

        return true;
    }

    // Method to repair an invalid shape
    public Shape2D repair(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        List<Point2D> fixedPoints = new ArrayList<>();
        Set<Point2D> uniquePoints = new HashSet<>();

        for (Point2D point : points) {
            if (!uniquePoints.contains(point)) {
                uniquePoints.add(point);
                fixedPoints.add(point);
            }
        }

        // Ensure the shape starts and ends with the same point
        if (!fixedPoints.get(0).equals(fixedPoints.get(fixedPoints.size() - 1))) {
            fixedPoints.add(fixedPoints.get(0));
        }

        return new Shape2D(fixedPoints);
    }
}
