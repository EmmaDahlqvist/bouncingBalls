package bouncing_balls;

/**
 * Strategy interface for handling collisions between physical objects.
 */
public interface PhysicalObjectCollisionStrategy {
    void handleCollision(PhysicalObject obj1, PhysicalObject obj2);
}
