package bouncing_balls;

public class PhysicsEngine {



    public void applyGravity(PhysicalObject physicalObject, double deltaT) {
        double g = -9.82; // Gravitational acceleration
        double newVY = physicalObject.getVY() + g * deltaT; // Update velocity using Euler's method
        physicalObject.setVY(newVY);
    }

    public void updatePosition(PhysicalObject physicalObject, double deltaT) {
        double newX = physicalObject.getX() + physicalObject.getVX() * deltaT;
        double newY = physicalObject.getY() + physicalObject.getVY() * deltaT;
        physicalObject.setX(newX);
        physicalObject.setY(newY);
    }


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

    private void handleXOverlap(PhysicalObject c, double areaWidth) {
        c.setX(c.getX() + getOverlap(c.getX(), c.getRadius(), areaWidth));
    }

    private void handleYOverlap(PhysicalObject c, double areaHeight) {
        c.setY(c.getY() + getOverlap(c.getY(), c.getRadius(), areaHeight));
    }

    private double getOverlap(double center, double radius, double borderLength)
    {
        if(center < radius) {
            return radius - center;
        } else if(center > borderLength - radius) {
            return -(center + radius - borderLength);
        }
        return 0; // no overlap
    }

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

    private void separateBalls(PhysicalObject c1, PhysicalObject c2) {
        double dx = c2.getX() - c1.getX();
        double dy = c2.getY() - c1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double overlap = (c1.getRadius() + c2.getRadius()) - distance;

        // Normalize the distance vector
        double nx = dx / distance;
        double ny = dy / distance;

        // Adjust positions to separate the balls
        c1.setX(c1.getX() - overlap*nx);
        c1.setY(c1.getY() - overlap*ny);
        c2.setX(c2.getX() + overlap*nx);
        c2.setY(c2.getY() + overlap*ny);
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
        return distance <= (o1.getRadius() + o2.getRadius());
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
