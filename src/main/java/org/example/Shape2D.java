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

    public void addPoint(Point2D point) {
        points.add(point);
    }

    @Override
    public boolean equals(Object o) {
        // If the two references point to the exact same object in memory
        // then they are trivially equal, so the method returns true.
        if (this == o) return true;
        // This checks if the object o is null or not of the same class as the current object (Shape2D).
        if (o == null || getClass() != o.getClass()) return false;
        // Once the type check has been passed, the object o is cast to the Shape2D class,
        // allowing us to compare its fields directly.
        Shape2D shape2D = (Shape2D) o;

        // This compares the points field (a list of Point2D objects) of the two Shape2D objects.
        // Objects.equals is used here because it safely handles null values,
        // returning true if both lists are null or equal, and false otherwise.
        // It delegates the actual equality check to the List.equals() method,
        // which ensures that the lists are compared element by element.
        return Objects.equals(points, shape2D.points);
    }

    @Override
    public int hashCode() {
        // return Objects.hash(points);
        // OR
        // return hash = 31 * hash + point.hashCode();
        // The manual hash code calculation using 31 can be more efficient in some cases,
        // especially when working with large lists.
        // The number 31 is often used in hash code implementations because it has certain mathematical properties
        // 1. The number 31 is prime, which helps in spreading out the generated hash codes more uniformly,
        // reducing the likelihood of collisions.
        // 2. In Java, multiplying by 31 can be optimized by the compiler into a bit shift and subtraction operation
        // 31 * hash is equivalent to (hash << 5) - hash, which is faster to compute than a general multiplication.
        int hash = 7;
        for (Point2D point : points) {
            hash = 31 * hash + point.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Shape2D{points=" + points + "}";
    }
}
