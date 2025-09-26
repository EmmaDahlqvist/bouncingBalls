package bouncing_balls;

public interface PhysicalObject extends Positionable, Movable {

    double getMass();
    double getRadius();
    void handleCollisionWithPhysicalObject(PhysicalObject physicalObject);
    void handleWallCollision(double areaWidth, double areaHeight);

}
