package bouncing_balls;

public class Ball {
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
}
