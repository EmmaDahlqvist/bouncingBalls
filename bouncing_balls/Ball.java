package bouncing_balls;

public class Ball implements PhysicalObject {

    Ball(double x, double y, double vx, double vy, double r, double m) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = r;
        this.m = m;
    }

    /**
     * Position, speed, and radius of the ball. You may wish to add other attributes.
     */
    double x, y, vx, vy, radius, m;

    @Override
    public double getMass() {
        return this.m;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public double getVX() {
        return this.vx;
    }

    @Override
    public double getVY() {
        return this.vy;
    }

    @Override
    public void setVX(double vx) {
        this.vx = vx;
    }

    @Override
    public void setVY(double vy) {
        this.vy = vy;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }
}
