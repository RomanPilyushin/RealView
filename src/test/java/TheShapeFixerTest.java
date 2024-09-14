import org.example.Point2D;
import org.example.Shape2D;
import org.example.TheShapeFixer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class TheShapeFixerTest {

    @Test
    void testValidShape() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Valid shape: A triangle
        Shape2D validShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 0),
                new Point2D(0, 0)
        ));

        assertTrue(shapeFixer.isValid(validShape), "The shape should be valid");
    }

    @Test
    void testInvalidShapeTooFewPoints() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Invalid shape: Only 2 points
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1)
        ));

        assertFalse(shapeFixer.isValid(invalidShape), "The shape should be invalid due to too few points");

        // Attempt to repair and check if repair fails (cannot form a valid polygon)
        Shape2D repairedShape = shapeFixer.repair(invalidShape);
        assertFalse(shapeFixer.isValid(repairedShape), "The repaired shape should still be invalid due to too few points");
    }

    @Test
    void testInvalidShapeRepeatedPoints() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Invalid shape: Repeated points
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 1),
                new Point2D(0, 0)
        ));

        assertFalse(shapeFixer.isValid(invalidShape), "The shape should be invalid due to repeated points");

        // Repair the shape
        Shape2D repairedShape = shapeFixer.repair(invalidShape);

        // Since there are only 2 distinct points, we cannot form a valid polygon
        // The repaired shape should be empty
        Shape2D expectedShape = new Shape2D(); // Empty shape

        assertEquals(expectedShape, repairedShape, "The repaired shape should be empty due to insufficient points");
        assertFalse(shapeFixer.isValid(repairedShape), "The repaired shape should be invalid due to insufficient points");
    }

    @Test
    void testRepairShapeMissingClosure() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Invalid shape: Not closed
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 0)
        ));

        assertFalse(shapeFixer.isValid(invalidShape), "The shape should be invalid due to missing closure");

        // Repair the shape
        Shape2D repairedShape = shapeFixer.repair(invalidShape);

        // Expected shape after repair
        Shape2D expectedShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 0),
                new Point2D(0, 0)
        ));

        assertEquals(expectedShape, repairedShape, "The repaired shape should ensure closure");
        assertTrue(shapeFixer.isValid(repairedShape), "The repaired shape should be valid");
    }

    @Test
    void testSelfIntersectingShape() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Invalid shape: Self-intersecting (bowtie shape)
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(2, 2),
                new Point2D(0, 2),
                new Point2D(2, 0),
                new Point2D(0, 0)
        ));

        assertFalse(shapeFixer.isValid(invalidShape), "The shape should be invalid due to self-intersection");

        // Repair the shape
        Shape2D repairedShape = shapeFixer.repair(invalidShape);

        // Since repairing self-intersecting shapes is complex, we check for validity
        assertTrue(shapeFixer.isValid(repairedShape), "The repaired shape should be valid");
    }

    @Test
    void testShapeWithColinearPoints() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Valid shape: Colinear points along one edge
        Shape2D shape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 0),
                new Point2D(2, 0), // Colinear point
                new Point2D(2, 1),
                new Point2D(0, 1),
                new Point2D(0, 0)
        ));

        assertTrue(shapeFixer.isValid(shape), "The shape should be valid despite colinear points");
    }

    @Test
    void testShapeWithZeroArea() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Invalid shape: All points are colinear
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(2, 2),
                new Point2D(3, 3),
                new Point2D(0, 0)
        ));

        assertFalse(shapeFixer.isValid(invalidShape), "The shape should be invalid due to zero area");

        // Attempt to repair and check if repair fails
        Shape2D repairedShape = shapeFixer.repair(invalidShape);
        assertFalse(shapeFixer.isValid(repairedShape), "The repaired shape should still be invalid due to zero area");
    }

}
