package bouncing_balls;

public interface PhysicalObject {

    double getMass();
    double getRadius();
    double getVX();
    double getVY();
    double getX();
    double getY();
    void setVX(double vx);
    void setVY(double vy);
    void setX(double x);
    void setY(double y);
}
