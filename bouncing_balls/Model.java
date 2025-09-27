package bouncing_balls;


/**
 * The physics model.
 * 
 * This class is where you should implement your bouncing balls model.
 * 
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 * 
 * @author Simon Robillard
 *
 */

class Model {
    double areaWidth, areaHeight;
    Ball[] balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;
        
        balls = new Ball[3];
        balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 1);
        balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 2);
        balls[2] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 2);
    }

    void step(double deltaT) {
        for (Ball b : balls) {
            applyGravity(b, deltaT);
        }
        
        for (Ball b : balls) {
            updatePosition(b, deltaT);
        }
        
        for (Ball b : balls) {
            handleWallCollision(b);
        }

        for(int i = 0; i < balls.length; i++) {
            for (int j = i + 1; j < balls.length ; j++) {
                if(isCollision(balls[i], balls[j])) {
                    handleBallCollision(balls[i], balls[j]);
                }
            }
        }
        
        /* if (isCollision(balls[0], balls[1])) {

            handleBallCollision(balls[0], balls[1]);
            
        } */
    }

    /* Rectangular coordinates (x, y) to polar coordinates (r, theta) */
    double[] rectToPolar(double x, double y, double centerX, double centerY) {
        double dx = x - centerX;
        double dy = y - centerY;
        double r = Math.sqrt(dx * dx + dy * dy);
        double theta = Math.atan2(dy, dx);
        return new double[]{r, theta};
    }
    
    /* Polar coordinates (r, theta) to rectangular coordinates (x, y) */
    double[] polarToRect(double r, double theta, double centerX, double centerY) {
        double x = centerX + r * Math.cos(theta);
        double y = centerY + r * Math.sin(theta);
        return new double[]{x, y};
    }

    /* Calculates the collision angle between two balls */
    double calculateCollisionAngle (Ball b1, Ball b2) {
        return Math.atan2(b2.y - b1.y, b2.x - b1.x);
    }
    
  
    void handleBallCollision(Ball b1, Ball b2) {
       

        /* Calculate collision angle */
        double collisionAngle = calculateCollisionAngle(b1, b2);
        
       
        
        double[] polarB1 = rectToPolar(b1.vx, b1.vy, 0, 0);
        double v1n = polarB1[0] * Math.cos(polarB1[1] - collisionAngle); 
        double v1t = polarB1[0] * Math.sin(polarB1[1] - collisionAngle); 
        
        
        double[] polar2 = rectToPolar(b2.vx, b2.vy, 0, 0);
        double v2n = polar2[0] * Math.cos(polar2[1] - collisionAngle);
        double v2t = polar2[0] * Math.sin(polar2[1] - collisionAngle);
        
        

        double totalMass = b1.m + b2.m;
        double newV1n = ((b1.m - b2.m) * v1n + 2 * b2.m * v2n) / totalMass;
        double newV2n = ((b2.m - b1.m) * v2n + 2 * b1.m * v1n) / totalMass;

        
        
        /* Convert from (newSpeed1, newAngle2) back to rectangular */ 
        double newSpeed1 = Math.sqrt(newV1n * newV1n + v1t * v1t);
        double newAngle1 = Math.atan2(v1t, newV1n) + collisionAngle;
        double[] rect1 = polarToRect(newSpeed1, newAngle1, 0, 0);
        b1.vx = rect1[0];
        b1.vy = rect1[1];
        
        /* Convert from (newSpeed2, newAngle2) back to rectangular */ 
        double newSpeed2 = Math.sqrt(newV2n * newV2n + v2t * v2t);
        double newAngle2 = Math.atan2(v2t, newV2n) + collisionAngle;
        double[] rect2 = polarToRect(newSpeed2, newAngle2, 0, 0);
        b2.vx = rect2[0];
        b2.vy = rect2[1];
        
        separateBalls(b1, b2);
    }


    void applyGravity(Ball b, double deltaT) {
        double g = -9.8;
        b.vy += deltaT * g;
    }

    void updatePosition(Ball b, double deltaT) {
        b.x += deltaT * b.vx;
        b.y += deltaT * b.vy;
    }

    void handleWallCollision(Ball b) {
        if (b.x < b.radius) { 
            b.x = b.radius; 
            b.vx = -b.vx; 
        }
        else if (b.x > areaWidth - b.radius) { 
            b.x = areaWidth - b.radius; 
            b.vx = -b.vx; 
        }
        
        if (b.y < b.radius) { 
            b.y = b.radius; 
            b.vy = -b.vy; 
        }
        else if (b.y > areaHeight - b.radius) { 
            b.y = areaHeight - b.radius; 
            b.vy = -b.vy; 
        }
    }

    boolean isCollision(Ball b1, Ball b2) {
        double dx = b1.x - b2.x;
        double dy = b1.y - b2.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < b1.radius + b2.radius;
    }

    void separateBalls(Ball b1, Ball b2) {
        double dx = b2.x - b1.x;
        double dy = b2.y - b1.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double overlap = (b1.radius + b2.radius) - distance;
        
        if (overlap > 0) {
            double angle = Math.atan2(dy, dx);
            double separation = overlap / 2;
            b1.x -= separation * Math.cos(angle);
            b1.y -= separation * Math.sin(angle);
            b2.x += separation * Math.cos(angle);
            b2.y += separation * Math.sin(angle);
        }
    }

    class Ball {

        double x, y, vx, vy, radius, m;

        Ball(double x, double y, double vx, double vy, double r, double m) {
            this.x = x; 
            this.y = y; 
            this.vx = vx; 
            this.vy = vy; 
            this.radius = r; 
            this.m = m;
        }
    }
    
}