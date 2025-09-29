package bouncing_balls;

/**
 * The physics engine that updates positions and velocities of physical objects.
 * It applies gravity, handles wall collisions, and handles collisions between objects using a specified strategy.
 */
public class PhysicsEngine {

    private final double areaWidth;
    private final double areaHeight;
    private PhysicalObjectCollisionStrategy strategy;

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
                PhysicalObject obj1 = physicalObjects[i];
                PhysicalObject obj2 = physicalObjects[j];
                strategy.handleCollision(obj1, obj2); // Use strategy to handle collision
            }
        }
    }

    /**
     * Apply gravitational force to a ball, updating its vertical velocity.
     */
    public void applyGravity(PhysicalObject obj, double deltaT) {
        double g = -9.82; // Gravitational acceleration
        double newVY = obj.getVY() + g * deltaT; // Update velocity using Euler's method
        obj.setVY(newVY);
    }

    /**
     * Update position of a ball based on its velocity and the time step deltaT.
     */
    public void updatePosition(PhysicalObject obj, double deltaT) {
        double newX = obj.getX() + obj.getVX() * deltaT;
        double newY = obj.getY() + obj.getVY() * deltaT;
        obj.setX(newX);
        obj.setY(newY);
    }

    /**
     * Handle collision of ball with walls.
     */
    public void handleWallCollision(PhysicalObject obj, double areaWidth, double areaHeight) {
        if (obj.getX() < obj.getRadius() || obj.getX() > areaWidth - obj.getRadius()) {
            handleXOverlap(obj, areaWidth);
            obj.setVX(obj.getVX()*-1); // change direction of ball
        }
        if (obj.getY() < obj.getRadius() || obj.getY() > areaHeight - obj.getRadius()) {
            handleYOverlap(obj, areaHeight);
            obj.setVY(obj.getVY()*-1);
        }
    }

    /**
     * Adjust x position of ball if it overlaps with left or right wall.
     */
    private void handleXOverlap(PhysicalObject obj, double areaWidth) {
        obj.setX(obj.getX() + getOverlap(obj.getX(), obj.getRadius(), areaWidth));
    }

    /**
     * Adjust y position of ball if it overlaps with top or bottom wall.
     */
    private void handleYOverlap(PhysicalObject obj, double areaHeight) {
        obj.setY(obj.getY() + getOverlap(obj.getY(), obj.getRadius(), areaHeight));
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

    public void setCollisionStrategy(PhysicalObjectCollisionStrategy strategy) {
        this.strategy = strategy;
    }
}
