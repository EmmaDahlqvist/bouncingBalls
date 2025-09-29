package bouncing_balls;
/**
 * Implements collision detection and response for collisions between balls.
 * 
 * This strategy handles elastic collision between two circular physical objects
 * using conservation of momentum and energy. 
 * 
 * @authors Emma Dahlqvist, Shifaa Mahmoud, Aisha Mohamed
 * @see PhysicalObjectCollisionStrategy
 */

public class BallCollisionStrategy implements PhysicalObjectCollisionStrategy {

    /**
     * Handle collision between two ball objects.
     * 
     * @param obj1 the first physical object involved in collision
     * @param obj2 the second physical object involved in collision
     */
    @Override
    public void handleCollision(PhysicalObject obj1, PhysicalObject obj2) {
        if(!objectCollisionDetected(obj1, obj2)) {
            return;
        }
        // Calculate collision angle
        double collisionAngle = calculateCollisionAngle(obj1, obj2);

        // Convert velocities to polar coordinates, rotate coordinate system
        double[] polarB1 = rectToPolar(obj1.getVX(), obj1.getVY(), 0, 0);
        double v1n = polarB1[0] * Math.cos(polarB1[1] - collisionAngle);
        double v1t = polarB1[0] * Math.sin(polarB1[1] - collisionAngle);

        double[] polarB2 = rectToPolar(obj2.getVX(), obj2.getVY(), 0, 0);
        double v2n = polarB2[0] * Math.cos(polarB2[1] - collisionAngle);
        double v2t = polarB2[0] * Math.sin(polarB2[1] - collisionAngle);

        // Handle collision in 1D
        double[] newV = handleHorizontalCollision(obj1, obj2, v1n, v2n);
        double newV1n = newV[0];
        double newV2n = newV[1];

        // Convert from (newSpeed1, newAngle2) back to rectangular
        double newSpeed1 = Math.sqrt(newV1n * newV1n + v1t * v1t);
        double newAngle1 = Math.atan2(v1t, newV1n) + collisionAngle;
        double[] rect1 = polarToRect(newSpeed1, newAngle1, 0, 0);
        obj1.setVX(rect1[0]);
        obj1.setVY(rect1[1]);

        // Convert from (newSpeed2, newAngle2) back to rectangular
        double newSpeed2 = Math.sqrt(newV2n * newV2n + v2t * v2t);
        double newAngle2 = Math.atan2(v2t, newV2n) + collisionAngle;
        double[] rect2 = polarToRect(newSpeed2, newAngle2, 0, 0);
        obj2.setVX(rect2[0]);
        obj2.setVY(rect2[1]);

        //  Ensure balls are not overlapping after collision
        separateBalls(obj1, obj2);
    }

    /**
     * Detect if two physical objects are colliding based on their position and radius
     * 
     * @param obj1 the first physical object
     * @param obj2 the second physical object
     * @return true if the objects are colliding, false otherwise
     */
    private boolean objectCollisionDetected(PhysicalObject obj1, PhysicalObject obj2) {
        double dx = obj2.getX() - obj1.getX();
        double dy = obj2.getY() - obj1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (obj1.getRadius() + obj2.getRadius());
    }


    /**
     * Converts rectangular coordinates (x, y) to polar coordinates (r, theta).
     *
     * @param x the x-coordinate in rectangular system
     * @param y the y-coordinate in rectangular system
     * @param centerX the x coordinate of the origin for polar conversion
     * @param centerY the y coordinate of the origin for polar conversion
     * @return an array where [0] is radius and [1] is angle 
     * @see #polarToRect(double, double, double, double)
     */

    double[] rectToPolar(double x, double y, double centerX, double centerY) {
        double dx = x - centerX;
        double dy = y - centerY;
        double r = Math.sqrt(dx * dx + dy * dy);
        double theta = Math.atan2(dy, dx);
        return new double[]{r, theta};
    }

   
    /**
     * Converts polar coordinates (r, theta) to rectangular coordinates (x, y).
     *
     * @param r the radius in polar coordinates
     * @param theta the angle in polar coordinates
     * @param centerX the x coordinate of the origin for rectangular conversion
     * @param centerY the y coordinate of the origin for rectangular conversion
     * @return an array where [0] is x coordinate and [1] is y coordinate
     */
    double[] polarToRect(double r, double theta, double centerX, double centerY) {
        double x = centerX + r * Math.cos(theta);
        double y = centerY + r * Math.sin(theta);
        return new double[]{x, y};
    }

    /**
     * Calculates the collision angle between two physical objects.
     * 
     * @param obj1 is the first physical object
     * @param obj2 is the second physical object
     * @return the collision angle 
     */
    
    double calculateCollisionAngle (PhysicalObject obj1, PhysicalObject obj2) {
        return Math.atan2(obj2.getY() - obj1.getY(), obj2.getX() - obj1.getX());
    }

    /**
     * Handles a 1D horizontal collision between two objects. 
     * Conserves both momentum and energy. 
     * 
     * @param obj1 the first physical object
     * @param obj2 the second physical object
     * @param v1n the velocity of obj1
     * @param v2n the velocity of obj2
     * @return an array where [0] is new velocity for obj1 and [1] is new velocity for obj2
     */
    double[] handleHorizontalCollision(PhysicalObject obj1, PhysicalObject obj2, double v1n, double v2n) {
        double totalMass =  obj1.getMass() + obj2.getMass();
        double newV1n = ((obj1.getMass() - obj2.getMass()) * v1n + 2 * obj2.getMass() * v2n) / totalMass;
        double newV2n = ((obj2.getMass() - obj1.getMass()) * v2n + 2 * obj1.getMass() * v1n) / totalMass;
        return new double[]{newV1n, newV2n};
    }

    /**
     * Separating two overlapping physical objects by moving them apart.
     * 
     * @param b1 the first physical object
     * @param b2 the second physical object
     */
    void separateBalls(PhysicalObject b1, PhysicalObject b2) {
        double dx = b2.getX() - b1.getX();
        double dy = b2.getY() - b1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double overlap = (b1.getRadius() + b2.getRadius()) - distance;

        if (overlap > 0) {
            double angle = Math.atan2(dy, dx);
            double separation = overlap / 2;
            b1.setX(b1.getX() - separation * Math.cos(angle));
            b1.setY(b1.getY() - separation * Math.sin(angle));
            b2.setX(b2.getX() + separation * Math.cos(angle));
            b2.setY(b2.getY() + separation * Math.sin(angle));
        }
    }

}
