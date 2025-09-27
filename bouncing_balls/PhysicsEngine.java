package bouncing_balls;

import java.util.List;

public class PhysicsEngine {

    private double areaWidth, areaHeight;

    public PhysicsEngine(double areaWidth, double areaHeight) {
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
    }

    /**
     * Update positions and velocities of all physical objects over a time step deltaT.
     * This includes applying gravity, updating positions, handling wall collisions,
     * and handling collisions between objects.
     */
    public void update(PhysicalObject[] physicalObjects, double deltaT) {
        // Apply gravity
        for (PhysicalObject p : physicalObjects) {
            applyGravity(p, deltaT);
        }

        // Update positions and handle wall collisions
        for (PhysicalObject p : physicalObjects) {
            updatePosition(p, deltaT);
            handleWallCollision(p, areaWidth, areaHeight);
        }
        // Collision between balls
        for (int i = 0; i < physicalObjects.length; i++) {
            for (int j = i + 1; j < physicalObjects.length; j++) {
                PhysicalObject p1 = physicalObjects[i];
                PhysicalObject p2 = physicalObjects[j];
                handleCollisionBetweenObjects(p1, p2);
            }
        }
    }

    /**
     * Apply gravitational force to a ball, updating its vertical velocity.
     */
    public void applyGravity(PhysicalObject physicalObject, double deltaT) {
        double g = -9.82; // Gravitational acceleration
        double newVY = physicalObject.getVY() + g * deltaT; // Update velocity using Euler's method
        physicalObject.setVY(newVY);
    }

    /**
     * Update position of a ball based on its velocity and the time step deltaT.
     */
    public void updatePosition(PhysicalObject physicalObject, double deltaT) {
        double newX = physicalObject.getX() + physicalObject.getVX() * deltaT;
        double newY = physicalObject.getY() + physicalObject.getVY() * deltaT;
        physicalObject.setX(newX);
        physicalObject.setY(newY);
    }


    /**
     * Handle collision of ball with walls.
     */
    public void handleWallCollision(PhysicalObject c, double areaWidth, double areaHeight) {

        if (c.getX() < c.getRadius() || c.getX() > areaWidth - c.getRadius()) {
            handleXOverlap(c, areaWidth);
            c.setVX(c.getVX()*-1); // change direction of ball
        }
        if (c.getY() < c.getRadius() || c.getY() > areaHeight - c.getRadius()) {
            handleYOverlap(c, areaHeight);
            c.setVY(c.getVY()*-1);
        }
    }

    /**
     * Adjust x position of ball if it overlaps with left or right wall.
     */
    private void handleXOverlap(PhysicalObject c, double areaWidth) {
        c.setX(c.getX() + getOverlap(c.getX(), c.getRadius(), areaWidth));
    }

    /**
     * Adjust y position of ball if it overlaps with top or bottom wall.
     */
    private void handleYOverlap(PhysicalObject c, double areaHeight) {
        c.setY(c.getY() + getOverlap(c.getY(), c.getRadius(), areaHeight));
    }

    /**
     * Calculate overlap of ball with wall.
     * Returns positive value if ball overlaps left or bottom wall,
     * negative value if ball overlaps right or top wall,
     * and zero if no overlap.
     */
    private double getOverlap(double center, double radius, double borderLength)
    {
        if(center < radius) {
            return radius - center;
        } else if(center > borderLength - radius) {
            return -(center + radius - borderLength);
        }
        return 0; // no overlap
    }

    /**
     * Handle collision between two balls.
     */
    public void handleCollisionBetweenObjects(PhysicalObject c1, PhysicalObject c2) {
        if(objectCollisionDetected(c1, c2)) {

            separateBalls(c1,c2);

            double theta = Math.atan2(c2.getY() - c1.getY(), c2.getX() - c1.getX());

            // rotate velocities to align with collision axis
            rotateCollisionLine(c1, c2, theta);

            handleHorizontalCollision(c1, c2);

            // rotate velocities back
            rotateCollisionLine(c1, c2, -theta);

        }
    }

    /**
     * Separate two overlapping balls based on their masses.
     */
    private void separateBalls(PhysicalObject c1, PhysicalObject c2) {
        double dx = c2.getX() - c1.getX();
        double dy = c2.getY() - c1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDist = c1.getRadius() + c2.getRadius();

        double overlap = minDist - distance;

        // normalized direction vector between balls
        double nx = dx / distance;
        double ny = dy / distance;

        positionalCorrection(c1, c2, overlap, nx, ny);
    }

    private static final double PERCENT = 0.8; // hur mycket av överlappen som korrigeras
    private static final double SLOP = 0.01;   // tolerans (ignorera små penetreringar)

    private void positionalCorrection(PhysicalObject c1, PhysicalObject c2, double overlap, double nx, double ny) {
        double correction = Math.max(overlap - SLOP, 0.0) * (PERCENT / (c1.getMass() + c2.getMass()));
        double correctionX = correction * nx;
        double correctionY = correction * ny;

        c1.setX(c1.getX() - correctionX * c2.getMass());
        c1.setY(c1.getY() - correctionY * c2.getMass());
        c2.setX(c2.getX() + correctionX * c1.getMass());
        c2.setY(c2.getY() + correctionY * c1.getMass());
    }

    /**
     * Handle collision of two balls moving in horizontal direction
     */
    private void handleHorizontalCollision(PhysicalObject p1, PhysicalObject p2) {

        double m1 = p1.getMass();
        double m2 = p2.getMass();
        double u1 = p1.getVX();
        double u2= p2.getVX();

        double v1 = (m1*u1 + 2*m2*u2 - m2*u1)/(m1 + m2);
        double v2 = u1 - u2 + v1;

        p1.setVX(v1);
        p2.setVX(v2);
    }

    /**
     * Detect if two balls are colliding.
     */
    private boolean objectCollisionDetected(PhysicalObject o1, PhysicalObject o2) {
        double dx = o2.getX() - o1.getX();
        double dy = o2.getY() - o1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double EPSILON = 0.001; // small tolerance for stability
        return distance < (o1.getRadius() + o2.getRadius()) - EPSILON;
    }

    /**
     * Rotate the velocity vectors of two balls by an angle theta.
     */
    private void rotateCollisionLine(PhysicalObject o1, PhysicalObject o2, double theta) {
        rotateVectors(o1, theta);
        rotateVectors(o2, theta);
    }

    /**
     * Rotate the velocity vector of a ball by an angle theta.
     */
    private void rotateVectors(PhysicalObject obj, double theta) {
        double vx = obj.getVX() * Math.cos(theta) - obj.getVY() * Math.sin(theta);
        double vy = obj.getVX() * Math.sin(theta) + obj.getVY() * Math.cos(theta);
        obj.setVX(vx);
        obj.setVY(vy);
    }

}
