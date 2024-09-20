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
        // Outer Loop (Iterating Through Edges)
        for (int i = 0; i < n - 1; i++) {
            // a1 and a2 represent the starting and ending points of the current edge
            Point2D a1 = points.get(i);
            Point2D a2 = points.get(i + 1);

            // Inner Loop (Comparing Current Edge with Others)
            for (int j = i + 1; j < n - 1; j++) {
                // This avoids a situation where two edges that are adjacent might technically "intersect"
                // at a shared vertex, which is expected and not considered a self-intersection
                //
                // First condition - edge 1-2 doesn’t need to be checked against edge 2-3
                // Second condition - skips the check for the case where the first edge and
                // the last edge of a closed polygon share the same vertex (which they must).
                if (Math.abs(i - j) <= 1 || (i == 0 && j == n - 2)) {
                    continue;
                }

                // b1 and b2 represent the starting and ending points of another edge
                Point2D b1 = points.get(j);
                Point2D b2 = points.get(j + 1);

                // Check if the two edges intersect
                if (edgesIntersect(a1, a2, b1, b2)) {
                    return false; // Edges intersect, shape is invalid
                }
            }
        }

        // Calculate the area of the shape to ensure it's not zero
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

        // Apply the Shoelace formula (Gauss's area formula)
        // https://www.wikiwand.com/en/articles/Shoelace_formula
        //
        // The Shoelace formula works by calculating twice the signed area of the polygon.
        // If the points are in counterclockwise order, the area will be positive,
        // and if they are in clockwise order, the area will be negative.
        for (int i = 0; i < n - 1; i++) {
            area += (long) points.get(i).x * points.get(i + 1).y;
            area -= (long) points.get(i + 1).x * points.get(i).y;
        }

        // The area might be negative depending on the winding order; we can take the absolute value
        // The caller of this method may later take the absolute value or divide by 2
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
        //
        // By computing the orientation of points (p1, p2, q1), and (p1, p2, q2)
        // we determine if the points q1 and q2 lie on opposite sides of the line segment p1-p2.
        // If they do, this is an indicator that the two line segments might intersect.
        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);

        // Do the same for (q1, q2, p1) and (q1, q2, p2).
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        // General case: If orientations are different, the segments intersect
        // If the orientations o1 and o2 are different
        // (i.e., q1 and q2 are on opposite sides of the segment p1-p2)
        // If both conditions are true, the two line segments intersect at some point.
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special cases to handle collinear points
        // (meaning the points lie on the same straight line)
        //
        // onSegment(p1, p2, q1) checks if the point q1 lies on the line segment p1-p2
        // (i.e., q1 is collinear with p1 and p2 and also lies between them).
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
        // This expression is derived from the cross product of vectors pq and qr,
        // which helps determine the orientation of the triplet (p,q,r)
        // The formula essentially represents the area of the triangle formed by these three points.
        // Subtracting the two gives the signed area of the triangle.
        // https://personal.utdallas.edu/~daescu/convexhull.pdf
        long val = (long) (q.y - p.y) * (r.x - q.x) - (long) (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0; // Collinear (the points that lie on the same straight line)
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
        // This method assumes that the points p, q, and r are collinear,
        // meaning they lie on the same straight line
        //
        // Let's say we have three collinear points:
        // p(1, 2), q(4, 5), r(2, 3)
        //
        // We check if r lies on the segment p-q by verifying:
        // r.x = 2 lies between p.x = 1 and q.x = 4.
        // r.y = 3 lies between p.y = 2 and q.y = 5.
        //
        // Since both conditions are true, r is on the segment between p and q, so the method returns true.
        return r.x <= Math.max(p.x, q.x) && r.x >= Math.min(p.x, q.x) &&
                r.y <= Math.max(p.y, q.y) && r.y >= Math.min(p.y, q.y);
    }

    /**
     * Attempts to repair an invalid 2D shape to make it valid.
     *
     * The method handles various cases such as:
     * - Ensuring the shape is closed by adding the starting point at the end if necessary.
     * - Removing consecutive duplicate points.
     * - Removing repeated points (excluding the closing point).
     * - Constructing a convex hull from the unique points if necessary.
     *
     * The method returns a new {@link Shape2D} object that is valid or an empty shape if it cannot be repaired.
     *
     * @param shape The invalid {@link Shape2D} object to repair.
     * @return A valid {@link Shape2D} object if repair is possible; otherwise, an empty {@link Shape2D}.
     */

    public Shape2D repair(Shape2D shape) {
        // Step 1: Create a mutable list of points from the input shape
        List<Point2D> points = new ArrayList<>(shape.getPoints());

        // Step 2: Ensure the shape is closed
        // If the first and last points are not the same, add the first point to the end
        if (!points.isEmpty() && !points.get(0).equals(points.get(points.size() - 1))) {
            points.add(points.get(0));
        }

        // Step 3: Remove consecutive duplicate points
        // This simplifies the shape and avoids zero-length edges
        points = removeConsecutiveDuplicates(points);

        // Step 4: Check if the shape has enough points to form a polygon
        // A valid polygon must have at least 3 distinct points (plus the closing point)
        if (points.size() < 4) {
            return new Shape2D(); // Not enough points to form a valid shape
        }

        // Step 5: Remove non-consecutive duplicate points (excluding the closing point)
        // This ensures all points (except the closing point) are unique
        Set<Point2D> uniquePoints = new HashSet<>(points.subList(0, points.size() - 1));
        if (uniquePoints.size() != points.size() - 1) {
            // There are duplicate points; remove them
            List<Point2D> fixedPoints = new ArrayList<>();
            Set<Point2D> seenPoints = new HashSet<>();
            for (int i = 0; i < points.size() - 1; i++) {
                Point2D p = points.get(i);
                if (seenPoints.add(p)) {
                    fixedPoints.add(p); // Add unique points
                }
            }
            if (fixedPoints.size() >= 3) {
                // Close the shape by adding the first point at the end
                fixedPoints.add(fixedPoints.get(0));
                points = fixedPoints; // Update the points list
            } else {
                return new Shape2D(); // Not enough points to form a polygon
            }
        }

        // Step 6: Attempt to fix self-intersections
        boolean shapeChanged = true; // Flag to track if changes are made
        while (!isValid(new Shape2D(points)) && shapeChanged) {
            // Try to resolve intersections
            shapeChanged = resolveIntersections(points);
        }

        // Step 7: Final validation
        // Check if the shape is now valid after resolving intersections
        Shape2D repairedShape = new Shape2D(points);
        if (isValid(repairedShape)) {
            return repairedShape; // Return the repaired shape if valid
        }

        // Step 8: Construct the convex hull as a last resort
        // The convex hull is the smallest convex polygon that contains all the points
        List<Point2D> hullPoints = constructConvexHull(new ArrayList<>(uniquePoints));

        // Ensure the convex hull is closed
        if (hullPoints.size() >= 3) {
            if (!hullPoints.get(0).equals(hullPoints.get(hullPoints.size() - 1))) {
                hullPoints.add(hullPoints.get(0));
            }
            repairedShape = new Shape2D(hullPoints);
            if (isValid(repairedShape)) {
                return repairedShape; // Return the convex hull if valid
            }
        }

        // Step 9: Unable to repair
        // Return an empty shape indicating that the shape cannot be repaired
        return new Shape2D();
    }

    /**
     * Attempts to resolve any self-intersections in the shape by removing problematic points.
     *
     * @param points The list of points representing the shape.
     * @return {@code true} if the shape was changed; {@code false} otherwise.
     */
    private boolean resolveIntersections(List<Point2D> points) {
        int n = points.size();
        // Iterate over all pairs of edges to find intersections
        for (int i = 0; i < n - 1; i++) {
            Point2D a1 = points.get(i);       // Start point of edge A
            Point2D a2 = points.get(i + 1);   // End point of edge A

            for (int j = 0; j < n - 1; j++) {
                // Skip adjacent edges (edges that share a common vertex)
                // and edges that share endpoints
                if (Math.abs(i - j) <= 1 || (i == 0 && j == n - 2)) {
                    continue;
                }
                Point2D b1 = points.get(j);       // Start point of edge B
                Point2D b2 = points.get(j + 1);   // End point of edge B

                // Check if edges A and B intersect
                if (edgesIntersect(a1, a2, b1, b2)) {
                    // Intersection detected between edges (a1, a2) and (b1, b2)
                    // Attempt to resolve the intersection
                    if (attemptToResolveIntersection(points, i, j)) {
                        // Intersection resolved by removing point(s)
                        return true; // Indicate that the shape has changed
                    }
                }
            }
        }
        return false; // No intersections resolved in this iteration
    }

    /**
     * Attempts to resolve a specific intersection by removing points involved in the intersection,
     * first individually and then in pairs.
     *
     * @param points The list of points representing the shape.
     * @param i Index of the first edge's starting point.
     * @param j Index of the second edge's starting point.
     * @return {@code true} if the intersection was resolved; {@code false} otherwise.
     */
    private boolean attemptToResolveIntersection(List<Point2D> points, int i, int j) {
        int n = points.size();
        // Indices of points involved in the intersection
        List<Integer> indicesOfIntersection = Arrays.asList(i, i + 1, j, j + 1);
        List<Shape2D> validShapes = new ArrayList<>();

        // First, try removing one point at a time
        for (int idx : indicesOfIntersection) {
            if (idx >= n - 1) {
                continue; // Skip the closing point
            }
            // Create a new list of points without the point at intersection (idx)
            List<Point2D> testPoints = new ArrayList<>(points);
            testPoints.remove(idx);
            Shape2D testShape = new Shape2D(testPoints);

            // Check if the new shape is valid
            if (isValid(testShape)) {
                validShapes.add(testShape); // Add to valid shapes
            }
        }
        if (!validShapes.isEmpty()) {
            // Select the valid shape with the largest area
            Shape2D bestShape = selectShapeWithLargestArea(validShapes);
            // Update the original points with the best shape found
            points.clear();
            points.addAll(bestShape.getPoints());
            return true; // Intersection resolved
        }

        // If single point removal doesn't work, try removing pairs of points
        for (int idx1 = 0; idx1 < indicesOfIntersection.size(); idx1++) {
            for (int idx2 = idx1 + 1; idx2 < indicesOfIntersection.size(); idx2++) {
                int index1 = indicesOfIntersection.get(idx1);
                int index2 = indicesOfIntersection.get(idx2);
                if (index1 >= n - 1 || index2 >= n - 1) {
                    continue; // Skip the closing point
                }
                // Create a new list without the two points
                List<Point2D> testPoints = new ArrayList<>(points);
                // Remove points in descending order to maintain correct indices
                int removeIdx1 = Math.max(index1, index2);
                int removeIdx2 = Math.min(index1, index2);
                testPoints.remove(removeIdx1);
                testPoints.remove(removeIdx2);
                Shape2D testShape = new Shape2D(testPoints);

                // Check if the new shape is valid
                if (isValid(testShape)) {
                    validShapes.add(testShape); // Add to valid shapes
                }
            }
        }
        if (!validShapes.isEmpty()) {
            // Select the valid shape with the largest area
            Shape2D bestShape = selectShapeWithLargestArea(validShapes);
            // Update the original points with the best shape found
            points.clear();
            points.addAll(bestShape.getPoints());
            return true; // Intersection resolved
        }

        // Unable to resolve the intersection by removing involved points
        return false;
    }

    /**
     * Selects the shape with the largest area from a list of valid shapes,
     * ensuring we preserve as much of the original shape as possible.
     *
     * @param shapes The list of valid shapes to choose from.
     * @return The shape with the largest area.
     */
    private Shape2D selectShapeWithLargestArea(List<Shape2D> shapes) {
        Shape2D bestShape = shapes.get(0);
        long maxArea = Math.abs(calculateArea(bestShape.getPoints()));
        for (Shape2D shape : shapes) {
            long area = Math.abs(calculateArea(shape.getPoints()));
            if (area > maxArea) {
                bestShape = shape;
                maxArea = area;
            }
        }
        return bestShape;
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
        // The first point in the input list is always added to the result list.
        // This is done because there is no previous point to compare the first point against
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
        // Graham's scan algorithm
        //https://www.youtube.com/watch?v=SBdWdT_5isI
        //
        // Step 1: Find the point with the lowest y-coordinate
        //
        // The point p0 is the starting point of the convex hull.
        // The method searches for the point with the lowest y-coordinate (the bottom-most point).
        // If two points have the same y-coordinate, the leftmost point (smallest x-coordinate) is chosen.
        Point2D p0 = points.get(0);
        for (Point2D p : points) {
            if (p.y < p0.y || (p.y == p0.y && p.x < p0.x)) {
                p0 = p;
            }
        }
        // This point will serve as the reference point
        // for sorting other points by their polar angle relative to p0.
        final Point2D finalP0 = p0;

        // Step 2: Sort the points by the polar angle with respect to p0
        //
        // If two points are collinear (have the same polar angle),
        // the closer one to p0 is placed first.
        // This is done using the distanceSquared method to compare the distances between the points and p0.
        //
        // Otherwise, the points are sorted so that points with a counterclockwise orientation
        // relative to p0 come before points with a clockwise orientation.
        // This ensures that the convex hull is built in a counterclockwise order.
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
        // The first two sorted points are added to the stack,
        // which will hold the points that form the convex hull.
        Stack<Point2D> stack = new Stack<>();
        stack.push(points.get(0)); // Push the first point
        stack.push(points.get(1)); // Push the second point

        // Process the remaining points
        //
        // For each point, it checks the orientation of the last two points in the stack and the current point.
        // If the orientation is not counterclockwise (i.e., if it forms a right turn or is collinear),
        // the last point is removed from the stack.
        //
        // The process ensures that the points in the stack form a valid convex polygon in counterclockwise order.
        // Only points that contribute to the outer boundary of the convex hull are kept.
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
