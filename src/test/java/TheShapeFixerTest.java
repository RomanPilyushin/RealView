
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

        // Valid shape: A triangle with 3 points
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

        // Invalid shape: Only 2 points, not enough to form a polygon
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1)
        ));

        assertFalse(shapeFixer.isValid(invalidShape), "The shape should be invalid due to too few points");
    }

    @Test
    void testInvalidShapeRepeatedPoints() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Invalid shape: Contains repeated points (not just the start and end)
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 1),
                new Point2D(0, 0)
        ));

        assertFalse(shapeFixer.isValid(invalidShape), "The shape should be invalid due to repeated points");
    }

    @Test
    void testRepairShape() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Shape with repeated points
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 1),
                new Point2D(0, 0)
        ));

        Shape2D repairedShape = shapeFixer.repair(invalidShape);

        // Expected repaired shape: No repeated points and closed properly
        Shape2D expectedShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(0, 0)
        ));

        assertEquals(expectedShape, repairedShape, "The shape should be repaired to remove repeated points and ensure closure");
    }

    @Test
    void testRepairShapeMissingClosure() {
        TheShapeFixer shapeFixer = new TheShapeFixer();

        // Shape not closed (start and end points are different)
        Shape2D invalidShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 0)
        ));

        Shape2D repairedShape = shapeFixer.repair(invalidShape);

        // Expected repaired shape: Closed properly
        Shape2D expectedShape = new Shape2D(Arrays.asList(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(1, 0),
                new Point2D(0, 0)
        ));

        assertEquals(expectedShape, repairedShape, "The shape should be repaired to ensure closure");
    }
}
