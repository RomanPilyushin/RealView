package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Shape2D {
    private List<Point2D> points;

    public Shape2D() {
        this.points = new ArrayList<>();
    }

    public Shape2D(List<Point2D> points) {
        this.points = points;
    }

    public List<Point2D> getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape2D shape2D = (Shape2D) o;
        return Objects.equals(points, shape2D.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }

    public void addPoint(Point2D point) {
        points.add(point);
    }


    @Override
    public String toString() {
        return "Shape2D{points=" + points + "}";
    }
}