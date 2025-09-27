package bouncing_balls;

public interface PhysicalObject extends Positionable, Movable {

    double getMass();
    double getRadius();
    double getVX();
    double getVY();
    double getX();
    double getY();
}
