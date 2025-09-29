package bouncing_balls;
/**
 * Represents a ball as a physical object in a 2D space.
 * 
 * This class implements the {@link PhysicalObject} interface.
 * 
 * @authors Emma Dahlqvist, Shifaa Mahmoud, Aisha Mohamed 
 * @see PhysicalObject
 */
public class Ball implements PhysicalObject {
    /** The x-coordinate position of the ball's center in 2D space */
    double x;

    /** The y-coordinate position of the ball's center in 2D space */
    double y;

    /** The velocity component in the x-direction */
    double vx;

    /** The velocity component in the y direction */
    double vy;

    /** The radius of the ball */
    double radius;

    /** The mass of the ball */
    double m;


    /**
     * Constructs a new Ball with specified physical properties
     * 
     * @param x the initial x-coordinate position of the ball's center
     * @param y the initial y-coordinate position of the ball's center
     * @param vx the initial velocity component in the x-direction
     * @param vy the initial velocity component in the y-direction
     * @param r the radius of the ball 
     * @param m the mass of the ball
     */
    Ball(double x, double y, double vx, double vy, double r, double m) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = r;
        this.m = m;
    }
    
    
    /**
     * @return the mass of this ball
     */
    @Override
    public double getMass() {
        return this.m;
    }

    /**
     * @return the radius of this ball
     */
    @Override
    public double getRadius() {
        return this.radius;
    }

    /**
     * @return the the current x-component velocity
     */
    @Override
    public double getVX() {
        return this.vx;
    }

    /**
     * @return the current y-component velocity
     */
    @Override
    public double getVY() {
        return this.vy;
    }

    /**
     * @param vx the new x-component velocity to set
     */
    @Override
    public void setVX(double vx) {
        this.vx = vx;
    }
    
    /**
     * @param vy the new y-component velocity to set
     */
    @Override
    public void setVY(double vy) {
        this.vy = vy;
    }

    /**
     * @return the current x-coordinate position
     */
    @Override
    public double getX() {
        return this.x;
    }

    /**
     * @return the current y-coordinate position
     */
    @Override
    public double getY() {
        return this.y;
    }

    /**
     * @param x the new x-coordinate position to set
     */
    @Override
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @param y the new y-coordinate position to set
     */
    @Override
    public void setY(double y) {
        this.y = y;
    }
}
