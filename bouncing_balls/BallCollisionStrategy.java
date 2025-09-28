package bouncing_balls;

public class BallCollisionStrategy implements PhysicalObjectCollisionStrategy {

    /**
     * Handle collision between two balls.
     */
    @Override
    public void handleCollision(PhysicalObject obj1, PhysicalObject obj2) {
        if(objectCollisionDetected(obj1, obj2)) {
            double theta = Math.atan2(obj2.getY() - obj1.getY(), obj2.getX() - obj1.getX());

            separateBallsBeforeCollision(obj1, obj2);

            // rotate velocities to align with collision axis
            rotateCollisionLine(obj1, obj2, theta);

            handleHorizontalCollision(obj1, obj2);

            // rotate velocities back
            rotateCollisionLine(obj1, obj2, -theta);

        }
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
    private void rotateCollisionLine(PhysicalObject ob1, PhysicalObject ob2, double theta) {
        rotateVectors(ob1, theta);
        rotateVectors(ob2, theta);
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

    /**
     * Handle collision of two balls moving in horizontal direction
     */
    private void handleHorizontalCollision(PhysicalObject ob1, PhysicalObject ob2) {
        double m1 = ob1.getMass();
        double m2 = ob2.getMass();
        double u1 = ob1.getVX();
        double u2= ob2.getVX();

        double v1 = (m1*u1 + 2*m2*u2 - m2*u1)/(m1 + m2);
        double v2 = u1 - u2 + v1;

        ob1.setVX(v1);
        ob2.setVX(v2);
    }

    /**
     * Adjust positions of two overlapping balls to eliminate overlap before handling collision.
     */
    private void separateBallsBeforeCollision(PhysicalObject obj1, PhysicalObject obj2) {
        double dx = obj2.getX() - obj1.getX();
        double dy = obj2.getY() - obj1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDist = obj1.getRadius() + obj2.getRadius();

        if (distance < minDist) {
            // Normalize the collision axis
            double nx = dx / distance;
            double ny = dy / distance;

            // Calculate the overlap
            double overlap = minDist - distance;

            // Adjust positions to eliminate overlap
            obj1.setX(obj1.getX() - overlap * nx / 2);
            obj1.setY(obj1.getY() - overlap * ny / 2);
            obj2.setX(obj2.getX() + overlap * nx / 2);
            obj2.setY(obj2.getY() + overlap * ny / 2);
        }
    }


}
