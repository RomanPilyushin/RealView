package org.example;

public class Main {
    public static void main(String[] args) {
        Shape2D shape = new Shape2D();
        shape.addPoint(new Point2D(0, 0));
        shape.addPoint(new Point2D(1, 0));
        shape.addPoint(new Point2D(1, 1));
        shape.addPoint(new Point2D(0, 1));
        shape.addPoint(new Point2D(0, 0)); // Closing the shape

        TheShapeFixer fixer = new TheShapeFixer();

        System.out.println("Is the shape valid? " + fixer.isValid(shape));

        Shape2D invalidShape = new Shape2D();
        invalidShape.addPoint(new Point2D(0, 0));
        invalidShape.addPoint(new Point2D(1, 0));
        invalidShape.addPoint(new Point2D(1, 1));
        invalidShape.addPoint(new Point2D(0, 1));
        invalidShape.addPoint(new Point2D(0, 0));
        invalidShape.addPoint(new Point2D(1, 1)); // Duplicate point

        System.out.println("Is the shape valid? " + fixer.isValid(invalidShape));
        Shape2D repairedShape = fixer.repair(invalidShape);
        System.out.println("Repaired shape: " + repairedShape);
    }
}
