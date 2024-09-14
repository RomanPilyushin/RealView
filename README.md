# TheShapeFixer Class Explanation

The `TheShapeFixer` class designed to validate and repair 2D shapes represented as sequences of connected points. It ensures that shapes meet specific criteria to be considered valid polygons and provides methods to correct invalid shapes when possible.

## Overview

The main functionalities of the `TheShapeFixer` class are:

- **Validation**: Checking if a given shape is a valid polygon based on defined criteria.
- **Repair**: Attempting to fix invalid shapes to make them conform to the required standards.

## Validation Criteria

A shape is considered **valid** if it satisfies the following conditions:

1. **Closure**: The shape must be closed; the first and last points must be the same.
2. **Minimum Points**: The shape must have at least three distinct points (forming a polygon), plus the closing point (minimum of four points in total).
3. **No Repeated Points**: There should be no repeated points in the sequence, excluding the closing point.
4. **Non-Self-Intersecting**: The edges of the shape must not cross each other.
5. **Positive Area**: The shape must enclose a positive area; it cannot be degenerate (e.g., all points lying on a straight line).

## Class Methods

### `isValid(Shape2D shape)`

Checks if the provided 2D shape is valid based on the criteria above.

**Implementation Details:**

- **Closure Check**: Verifies that the first and last points are identical.
- **Minimum Points Check**: Ensures there are at least four points (three unique points plus the closing point).
- **Duplicate Points Check**: Utilizes a `HashSet` to detect any repeated points, excluding the closing point.
- **Edge Intersection Check**: Iterates over pairs of non-adjacent edges to detect intersections using the `edgesIntersect` method.
- **Area Calculation**: Uses the Shoelace formula via the `calculateArea` method to compute the area and ensures it's non-zero.

### `repair(Shape2D shape)`

Attempts to repair an invalid shape to make it valid. If it cannot be repaired, it returns an empty shape.

**Repair Strategies:**

1. **Closure Enforcement**: Adds the starting point at the end if the shape isn't already closed.
2. **Consecutive Duplicate Removal**: Removes any consecutive duplicate points to simplify the shape.
3. **Duplicate Point Removal**: Eliminates any repeated points to try forming a valid shape.
4. **Convex Hull Construction**: As a last resort, constructs the convex hull of the unique points to form a valid shape using the `constructConvexHull` method.

### `edgesIntersect(Point2D p1, Point2D p2, Point2D q1, Point2D q2)`

Determines if two line segments intersect.

**Explanation:**

- **Orientation Calculation**: Computes the orientations of various combinations of the points to determine their relative positions.
- **General Case**: If the orientations of the endpoints of the segments differ, the segments intersect.
- **Special Cases**: Handles colinear cases where points may lie on the other segment using the `onSegment` method.

**Algorithm Details:**

1. **Calculate Orientations:**

   For segments `p1p2` and `q1q2`, compute:

    - `o1 = orientation(p1, p2, q1)`
    - `o2 = orientation(p1, p2, q2)`
    - `o3 = orientation(q1, q2, p1)`
    - `o4 = orientation(q1, q2, p2)`
2. **General Case:**

    - If `o1 != o2` and `o3 != o4`, the segments intersect.
3. **Special Cases (Colinear Points):**

    - If any orientation is `0`, check if the corresponding point lies on the other segment using `onSegment`.
4. **Conclusion:**

    - Return `true` if any of the above conditions are met; otherwise, return `false`.

### `orientation(Point2D p, Point2D q, Point2D r)`

Computes the orientation of an ordered triplet `(p, q, r)`.

**Returns:**

- `0`: Colinear points.
- `1`: Clockwise orientation.
- `2`: Counterclockwise orientation.


### `onSegment(Point2D p, Point2D q, Point2D r)`

Checks if point `r` lies on the line segment defined by points `p` and `q`.

**Usage:**

- Assumes that `p`, `q`, and `r` are colinear.
- Returns `true` if `r` lies on segment `pq`; `false` otherwise.

### `calculateArea(List<Point2D> points)`

Calculates the area of a polygon using the Shoelace formula.

### `constructConvexHull(List<Point2D> points)`

Constructs the convex hull of a set of points using Graham's scan algorithm.

**Steps:**

1. **Pivot Selection:**

    - Find the point with the lowest `y` coordinate (and the leftmost in case of a tie) to serve as the pivot `p0`.
2. **Sorting Points by Polar Angle:**

    - Sort the points based on the angle they make with the pivot.
3. **Hull Construction:**

    - Use a stack to build the convex hull by ensuring that the sequence of points makes left turns (counterclockwise orientation).

### `removeConsecutiveDuplicates(List<Point2D> points)`

Removes consecutive duplicate points from the list.

## Edge Intersection Detection Explained

Detecting whether two edges intersect is essential for validating that a shape is non-self-intersecting. The `edgesIntersect` method achieves this by using orientations and handling special cases.

### Orientations and Their Significance

- **Orientation** of an ordered triplet of points indicates the direction of rotation from the first point to the third point about the second point.
- Calculated using the cross product, which helps determine if points turn left, right, or are colinear.

### General Case Intersection

- If the orientations of the combinations of points for both segments differ, the segments intersect.

- Specifically, if:
  `o1 != o2 && o3 != o4`

  
  where `o1`, `o2`, `o3`, and `o4` are orientations calculated for the points of the segments.


### Special Cases (Colinear Points)

- If any orientation is `0`, it indicates colinearity.
- Use the `onSegment` method to check if a point lies on another segment when orientations are colinear.
- The segments intersect if a colinear point lies on the other segment.

### Mathematical Background

The **orientation calculation** used in `TheShapeFixer` is based on the **determinant of vectors** formed by three points in 2D space. Here's a detailed explanation:

#### Vectors and Determinants

- Given three points `P(p_x, p_y)`, `Q(q_x, q_y)`, and `R(r_x, r_y)`, the orientation can be computed using the **determinant** of the vectors **PQ** and **QR**.
- The determinant essentially measures the area of the parallelogram formed by these two vectors in 2D space.

Mathematically, the determinant is computed as:

$$ \text{det} = (q_x - p_x) \cdot (r_y - q_y) - (q_y - p_y) \cdot (r_x - q_x) $$


This value gives insight into the direction of the turn from `P` to `R` about `Q`.

#### Turn Direction Based on the Determinant

1. **Clockwise (Right Turn)**: If the determinant is **positive**, it means the turn is **clockwise**.
2. **Counterclockwise (Left Turn)**: If the determinant is **negative**, it means the turn is **counterclockwise**.
3. **Colinear (No Turn)**: If the determinant is **zero**, the points are **colinear**, meaning they lie on a straight line.

#### Parallelogram and Area

- The determinant gives **twice the area** of the parallelogram formed by the vectors **PQ** and **QR**.
- The **sign of the determinant** tells us about the relative orientation (clockwise or counterclockwise) of the three points:
    - **Positive determinant**: Left turn (counterclockwise).
    - **Negative determinant**: Right turn (clockwise).
    - **Zero determinant**: Colinear points (no turn).

This approach helps determine the orientation of edges or whether two edges intersect when testing shapes for validity.

### Example

Suppose we have two segments:

- Segment `p1p2`: `(1, 1)` to `(4, 4)`
- Segment `q1q2`: `(1, 4)` to `(4, 1)`

Calculating orientations:

- `o1 = orientation(p1, p2, q1) = 2` (Counterclockwise)
- `o2 = orientation(p1, p2, q2) = 1` (Clockwise)
- `o3 = orientation(q1, q2, p1) = 1` (Clockwise)
- `o4 = orientation(q1, q2, p2) = 2` (Counterclockwise)

Since `o1 != o2` and `o3 != o4`, the segments intersect.

## Conclusion

The `TheShapeFixer` class is a comprehensive utility for ensuring 2D shapes are valid polygons according to specific criteria. It uses fundamental concepts from computational geometry, such as orientations and convex hulls, to validate and repair shapes.





