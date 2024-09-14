package org.example;

import java.util.Objects;

public class Point2D {
    public final int x;
    public final int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point2D point2D = (Point2D) o;
        return x == point2D.x && y == point2D.y;
    }

    @Override
    public int hashCode() {
        //return Objects.hash(x, y);
        // Optimized version: Using prime number 31 for efficient hashing
        return 31 * x + y;

    }

    @Override
    public String toString() {
        return "{" + x + "," + y + '}';
    }
}
