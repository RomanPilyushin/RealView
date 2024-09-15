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
        // This line checks if the two references (this and o) are pointing to the same object in memory.
        if (this == o) return true;
        // getClass() != o.getClass(): It ensures that both objects are of the type Point2D.
        // getClass() returns the runtime class of the object.
        if (o == null || getClass() != o.getClass()) return false;
        // After ensuring that the objects are of the same class, the method casts o to Point2D
        Point2D point2D = (Point2D) o;
        // The method then compares the x and y coordinates of the two points
        return x == point2D.x && y == point2D.y;
    }

    @Override
    public int hashCode() {
        // return Objects.hash(x, y);
        // OR
        // return hash = 31 * hash + point.hashCode();
        // The manual hash code calculation using 31 can be more efficient in some cases,
        // especially when working with large lists.
        // The number 31 is often used in hash code implementations because it has certain mathematical properties
        // 1. The number 31 is prime, which helps in spreading out the generated hash codes more uniformly,
        // reducing the likelihood of collisions.
        // 2. In Java, multiplying by 31 can be optimized by the compiler into a bit shift and subtraction operation
        // 31 * hash is equivalent to (hash << 5) - hash, which is faster to compute than a general multiplication.
        return 31 * x + y;

    }

    @Override
    public String toString() {
        return "{" + x + "," + y + '}';
    }
}
