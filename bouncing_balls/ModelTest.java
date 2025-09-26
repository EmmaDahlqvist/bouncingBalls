package bouncing_balls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    Model model = new Model(100, 100);
    Ball b1 = model.balls[0];
    Ball b2 = model.balls[1];

    @BeforeEach
    void setUp() {
        b1.vx = 1;
        b2.vx = -1;
        b1.x = 10;
        b2.x = 10.2;
        b1.y = b1.radius + 1;
        b2.y = b2.radius;
        b1.vy = 0;
        b2.vy = 0;
        b1.radius = 1;
        b2.radius = 1;
        b1.m = 1;
        b2.m = 2;
    }


    @Test
    void ballCollision() {




        double u1 = Math.sqrt(b1.vx*b1.vx + b1.vy * b1.vy);
        double u2 = Math.sqrt(b2.vx*b2.vx + b2.vy * b2.vy);

        double momentumBefore = u1*b1.m + u2*b2.m;




        u1 = Math.sqrt(b1.vx*b1.vx + b1.vy * b1.vy);
        u2 = Math.sqrt(b2.vx*b2.vx + b2.vy * b2.vy);

        double momentumAfter = u1*b1.m + u2*b2.m;

        assertEquals(momentumBefore, momentumAfter);
    }


    @Test
    void testEnergyHorizontally() {


        double u1 = Math.sqrt(b1.vx*b1.vx + b1.vy * b1.vy);
        double u2 = Math.sqrt(b2.vx*b2.vx + b2.vy * b2.vy);

        double energyAfter = 0.5*u1*u1*b1.m + u2*u2*b2.m*0.5;



        double energyBefore = 0.5*u1*u1*b1.m + u2*u2*b2.m*0.5;

        System.out.println(energyBefore + " " + energyAfter);
        assertEquals(energyBefore, energyAfter);

    }

    @Test
    void handleHorizontalCollision() {
        double u1 = getVelocity(b1.vx, b1.vy);
        double u2 = getVelocity(b2.vx, b2.vy);

        double momentumBefore = b1.vx*b1.m + b2.vx*b2.m;
        double energyBefore = 0.5*u1*u1*b1.m + u2*u2*b2.m*0.5;



        u1 = getVelocity(b1.vx, b1.vy);
        u2 = getVelocity(b2.vx, b2.vy);

        double momentumAfter = b1.vx*b1.m + b2.vx*b2.m;
        double energyAfter = 0.5*u1*u1*b1.m + u2*u2*b2.m*0.5;

        assertEquals(momentumBefore, momentumAfter);
        assertEquals(energyBefore, energyAfter);
    }

    private double getVelocity(double vx, double vy) {
        return Math.atan2(vx, vy);
    }
}