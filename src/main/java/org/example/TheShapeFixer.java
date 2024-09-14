package org.example;

import java.util.*;

/**
 * TheShapeFixer is a utility class that provides methods to validate and repair 2D shapes represented
 * as a sequence of connected points.
 *
 * A valid shape must be a closed polygon without self-intersections, repeated points (other than
 * the first and last point for closure), and must have a positive area.
 *
 * The class includes methods to check the validity of a shape and to attempt to repair invalid shapes.
 */
public class TheShapeFixer {

    /**
     * Checks if the given 2D shape is valid.
     *
     * A valid shape must:
     * - Have at least 3 distinct points plus a closing point (total of at least 4 points).
     * - Be closed (first and last points are the same).
     * - Not have repeated points (excluding the closing point).
     * - Not have self-intersecting edges.
     * - Have a positive area.
     *
     * @param shape The {@link Shape2D} object to validate.
     * @return {@code true} if the shape is valid; {@code false} otherwise.
     *
     */
    public boolean isValid(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        int n = points.size();

        // Check if the shape has enough points to form a polygon (minimum 3 points + closing point)
        if (n < 4) {
            return false;
        }

        // Verify that the shape is closed by checking if the first and last points are the same
        if (!points.get(0).equals(points.get(n - 1))) {
            return false;
        }

        // Check for repeated points in the shape (excluding the closing point)
        Set<Point2D> pointSet = new HashSet<>(points.subList(0, n - 1));
        if (pointSet.size() != n - 1) {
            return false; // Shape contains repeated points
        }

        // Check for self-intersecting edges by examining all pairs of edges
        for (int i = 0; i < n - 1; i++) {
            Point2D a1 = points.get(i);
            Point2D a2 = points.get(i + 1);

            for (int j = i + 1; j < n - 1; j++) {
                // Skip adjacent edges and edges sharing a vertex to avoid false positives
                if (Math.abs(i - j) <= 1 || (i == 0 && j == n - 2)) {
                    continue;
                }

                Point2D b1 = points.get(j);
                Point2D b2 = points.get(j + 1);

                // Check if the two edges intersect
                if (edgesIntersect(a1, a2, b1, b2)) {
                    return false; // Edges intersect, shape is invalid
                }
            }
        }

        // Calculate the area of the shape to ensure it's not zero (i.e., the shape is not degenerate)
        long area = calculateArea(points);
        if (area == 0) {
            return false; // Shape has zero area, invalid
        }

        // All checks passed, the shape is valid
        return true;
    }

    /**
     * Calculates the area of a polygon using the Shoelace formula.
     *
     * Since we're using integer coordinates, the area calculated will be twice the actual area.
     * We avoid dividing by 2 to prevent loss of precision due to integer division.
     *
     * @param points A list of points representing the polygon (must be closed).
     * @return The raw area value (twice the actual area), can be positive or negative.
     */
    private long calculateArea(List<Point2D> points) {
        long area = 0;
        int n = points.size();

        // Apply the Shoelace formula
        for (int i = 0; i < n - 1; i++) {
            area += (long) points.get(i).x * points.get(i + 1).y;
            area -= (long) points.get(i + 1).x * points.get(i).y;
        }

        // The area might be negative depending on the winding order; we can take the absolute value
        return area;
    }

    /**
     * Determines if two line segments (edges) intersect.
     *
     * This method uses the concept of orientations and special cases to check for intersection.
     *
     * @param p1 The first endpoint of the first segment.
     * @param p2 The second endpoint of the first segment.
     * @param q1 The first endpoint of the second segment.
     * @param q2 The second endpoint of the second segment.
     * @return {@code true} if the segments intersect; {@code false} otherwise.
     */
    private boolean edgesIntersect(Point2D p1, Point2D p2, Point2D q1, Point2D q2) {
        // Calculate orientations for the combinations of the points
        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        // General case: If orientations are different, the segments intersect
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special cases to handle colinear points
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        if (o2 == 0 && onSegment(p1, p2, q2)) return true;
        if (o3 == 0 && onSegment(q1, q2, p1)) return true;
        if (o4 == 0 && onSegment(q1, q2, p2)) return true;

        // Segments do not intersect
        return false;
    }

    /**
     * Computes the orientation of an ordered triplet (p, q, r).
     *
     * The orientation indicates the direction of rotation from p to r about q:
     * - 0: Colinear points
     * - 1: Clockwise rotation
     * - 2: Counterclockwise rotation
     *
     * @param p The first point.
     * @param q The second point.
     * @param r The third point.
     * @return The orientation code (0, 1, or 2).
     */
    private int orientation(Point2D p, Point2D q, Point2D r) {
        // Calculate the determinant of the matrix formed by the points
        long val = (long) (q.y - p.y) * (r.x - q.x) - (long) (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0; // Colinear
        return (val > 0) ? 1 : 2; // Clockwise if positive, counterclockwise if negative
    }

    /**
     * Checks if a point r lies on the line segment defined by points p and q.
     *
     * This method assumes that r, p, and q are colinear.
     *
     * @param p The first endpoint of the segment.
     * @param q The second endpoint of the segment.
     * @param r The point to check.
     * @return {@code true} if point r lies on the segment pq; {@code false} otherwise.
     */
    private boolean onSegment(Point2D p, Point2D q, Point2D r) {
        // Check if r's coordinates are within the bounding rectangle of p and q
        return r.x <= Math.max(p.x, q.x) && r.x >= Math.min(p.x, q.x) &&
                r.y <= Math.max(p.y, q.y) && r.y >= Math.min(p.y, q.y);
    }

    /**
     * Attempts to repair an invalid 2D shape to make it valid.
     *
     * The method handles various cases such as:
     * - Ensuring the shape is closed by adding the starting point at the end if necessary.
     * - Removing consecutive duplicate points.
     * - Splitting concatenated shapes into valid sub-shapes.
     * - Removing repeated points (excluding the closing point).
     * - Constructing a convex hull from the unique points if necessary.
     *
     * The method returns a new {@link Shape2D} object that is valid or an empty shape if it cannot be repaired.
     *
     * @param shape The invalid {@link Shape2D} object to repair.
     * @return A valid {@link Shape2D} object if repair is possible; otherwise, an empty {@link Shape2D}.
     */
    public Shape2D repair(Shape2D shape) {
        // Create a modifiable copy of the points
        List<Point2D> points = new ArrayList<>(shape.getPoints());

        // Ensure the shape is closed by adding the starting point at the end if it's not already closed
        if (points.size() >= 1 && !points.get(0).equals(points.get(points.size() - 1))) {
            points.add(points.get(0));
        }

        // Remove consecutive duplicate points to simplify the shape
        points = removeConsecutiveDuplicates(points);

        // If there are not enough points to form a polygon, return an empty shape
        if (points.size() < 4) {
            return new Shape2D();
        }

        // Attempt to split the shape at points where the starting point repeats (possible concatenated shapes)
        List<Integer> splitIndices = new ArrayList<>();
        splitIndices.add(0);
        for (int i = 1; i < points.size() - 1; i++) {
            if (points.get(i).equals(points.get(0))) {
                splitIndices.add(i);
            }
        }
        splitIndices.add(points.size());

        // Initialize the best valid shape found during splitting
        List<Point2D> bestShape = new ArrayList<>();

        // Iterate over each sub-shape formed by splitting at repeated starting points
        for (int k = 0; k < splitIndices.size() - 1; k++) {
            int start = splitIndices.get(k);
            int end = splitIndices.get(k + 1);
            List<Point2D> subPoints = new ArrayList<>(points.subList(start, end));

            // Skip sub-shapes that are too small to be valid
            if (subPoints.size() < 4) continue;

            // Ensure the sub-shape is closed
            if (!subPoints.get(0).equals(subPoints.get(subPoints.size() - 1))) {
                subPoints.add(subPoints.get(0));
            }

            // Remove consecutive duplicates within the sub-shape
            subPoints = removeConsecutiveDuplicates(subPoints);

            // Create a Shape2D object from the sub-points
            Shape2D subShape = new Shape2D(subPoints);

            // Check if the sub-shape is valid and larger than the current best shape
            if (isValid(subShape) && subPoints.size() > bestShape.size()) {
                bestShape = subPoints;
            }
        }

        // If a valid sub-shape was found, return it
        if (!bestShape.isEmpty()) {
            return new Shape2D(bestShape);
        }

        // Remove any repeated points (excluding the first and last point for closure)
        Set<Point2D> uniquePoints = new HashSet<>(points.subList(0, points.size() - 1));

        // If duplicates are found, attempt to remove them
        if (uniquePoints.size() != points.size() - 1) {
            List<Point2D> fixedPoints = new ArrayList<>();
            Set<Point2D> seen = new HashSet<>();

            // Collect unique points
            for (int i = 0; i < points.size() - 1; i++) {
                Point2D p = points.get(i);
                if (!seen.contains(p)) {
                    fixedPoints.add(p);
                    seen.add(p);
                }
            }

            // Ensure there are enough points to form a valid shape
            if (fixedPoints.size() >= 3) {
                // Close the shape
                fixedPoints.add(fixedPoints.get(0));
                points = fixedPoints;
            } else {
                // Not enough points to form a valid shape
                return new Shape2D();
            }
        }

        // Attempt to validate the repaired shape
        Shape2D repairedShape = new Shape2D(points);
        if (isValid(repairedShape)) {
            return repairedShape;
        }

        // As a last resort, construct the convex hull of the unique points
        List<Point2D> hull = constructConvexHull(new ArrayList<>(uniquePoints));

        // If the hull forms a valid shape, return it
        if (hull.size() >= 3) {
            // Ensure the hull is closed
            if (!hull.get(0).equals(hull.get(hull.size() - 1))) {
                hull.add(hull.get(0));
            }
            repairedShape = new Shape2D(hull);
            if (isValid(repairedShape)) {
                return repairedShape;
            }
        }

        // Cannot repair the shape, return an empty shape
        return new Shape2D();
    }

    /**
     * Removes consecutive duplicate points from a list of points.
     *
     * This helps in simplifying the shape by removing unnecessary points.
     *
     * @param points The list of points.
     * @return A new list of points without consecutive duplicates.
     */
    private List<Point2D> removeConsecutiveDuplicates(List<Point2D> points) {
        List<Point2D> result = new ArrayList<>();
        if (points.isEmpty()) return result;

        // Add the first point
        result.add(points.get(0));

        // Iterate over the points and add only if the current point is not equal to the previous one
        for (int i = 1; i < points.size(); i++) {
            if (!points.get(i).equals(points.get(i - 1))) {
                result.add(points.get(i));
            }
        }
        return result;
    }

    /**
     * Constructs the convex hull of a set of points using Graham's scan algorithm.
     *
     * The convex hull is the smallest convex polygon that contains all the points.
     * This can help in repairing self-intersecting shapes by creating a valid convex shape.
     *
     * @param points A list of unique points.
     * @return A list of points representing the convex hull in counterclockwise order.
     */
    private List<Point2D> constructConvexHull(List<Point2D> points) {
        if (points.size() <= 1) return new ArrayList<>(points);

        // Step 1: Find the point with the lowest y-coordinate (and leftmost in case of a tie)
        Point2D p0 = points.get(0);
        for (Point2D p : points) {
            if (p.y < p0.y || (p.y == p0.y && p.x < p0.x)) {
                p0 = p;
            }
        }

        final Point2D finalP0 = p0;

        // Step 2: Sort the points by the polar angle with respect to p0
        points.sort((p1, p2) -> {
            if (p1.equals(finalP0)) return -1;
            if (p2.equals(finalP0)) return 1;

            int orientation = orientation(finalP0, p1, p2);

            if (orientation == 0) {
                // Points are colinear; the closer one comes first
                long dist1 = distanceSquared(finalP0, p1);
                long dist2 = distanceSquared(finalP0, p2);
                return Long.compare(dist1, dist2);
            } else {
                // Counterclockwise orientation comes before clockwise
                return (orientation == 2) ? -1 : 1;
            }
        });

        // Step 3: Build the convex hull using a stack
        Stack<Point2D> stack = new Stack<>();
        stack.push(points.get(0)); // Push the first point
        stack.push(points.get(1)); // Push the second point

        // Process the remaining points
        for (int i = 2; i < points.size(); i++) {
            Point2D top = stack.pop();

            // Remove points from the stack that would cause a right turn (non-counterclockwise)
            while (!stack.isEmpty() && orientation(stack.peek(), top, points.get(i)) != 2) {
                top = stack.pop();
            }

            // Add the valid point to the stack
            stack.push(top);
            stack.push(points.get(i));
        }

        // Convert the stack to a list and return
        return new ArrayList<>(stack);
    }

    /**
     * Calculates the squared Euclidean distance between two points.
     *
     * Used to compare distances without taking square roots, which is more efficient.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The squared distance between {@code p1} and {@code p2}.
     */
    private long distanceSquared(Point2D p1, Point2D p2) {
        long dx = (long) p1.x - p2.x;
        long dy = (long) p1.y - p2.y;
        return dx * dx + dy * dy;
    }
}
