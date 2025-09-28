package bouncing_balls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    Model model;
    Ball b1;
    Ball b2;

    @BeforeEach
    void setUp() {
        model = new Model(100, 100);
        b1 = model.balls[0];
        b2 = model.balls[1];

        // Initialize ball properties for collision
        b1.x = 10;
        b1.y = 10;
        b1.vx = 1;
        b1.vy = 0;
        b1.radius = 1;
        b1.m = 1;

        b2.x = 12;
        b2.y = 10;
        b2.vx = -1;
        b2.vy = 0;
        b2.radius = 1;
        b2.m = 1;
    }

    @Test
    void testMomentumAndEnergyConservation() {
        // Calculate initial momentum and energy
        double initialMomentum = b1.m * b1.vx + b2.m * b2.vx;
        double initialEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

        // Simulate one step to handle collision
        model.step(0.1);

        // Calculate final momentum and energy
        double finalMomentum = b1.m * b1.vx + b2.m * b2.vx;
        double finalEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

        // Assert conservation of momentum and energy
        assertEquals(initialMomentum, finalMomentum, 1e-6, "Momentum is not conserved");
        assertEquals(initialEnergy, finalEnergy, 1e-6, "Energy is not conserved");
    }

    @Test
    void testEnergyAndMomentumOverMultipleSteps() {
        // Number of steps to simulate
        int steps = 1000;
        double deltaT = 0.1;

        // Calculate initial momentum and energy
        double initialMomentumX = 0;
        double initialMomentumY = 0;
        double initialEnergy = 0;
        for (Ball b : model.balls) {
            initialMomentumX += b.m * b.vx;
            initialMomentumY += b.m * b.vy;
            initialEnergy += 0.5 * b.m * (b.vx * b.vx + b.vy * b.vy);
        }

        // Simulate multiple steps
        for (int i = 0; i < steps; i++) {
            model.step(deltaT);

            // Calculate total momentum and energy after each step
            double totalMomentumX = 0;
            double totalMomentumY = 0;
            double totalEnergy = 0;
            for (Ball b : model.balls) {
                totalMomentumX += b.m * b.vx;
                totalMomentumY += b.m * b.vy;
                totalEnergy += 0.5 * b.m * (b.vx * b.vx + b.vy * b.vy);
            }

            // Log the values for debugging
            System.out.printf("Step %d: Total Momentum X = %.6f, Total Momentum Y = %.6f, Total Energy = %.6f%n",
                    i, totalMomentumX, totalMomentumY, totalEnergy);

            // Assert that momentum and energy are approximately conserved
            assertEquals(initialMomentumX, totalMomentumX, 1e-3, "Momentum X is not conserved at step " + i);
            assertEquals(initialMomentumY, totalMomentumY, 1e-3, "Momentum Y is not conserved at step " + i);
            assertEquals(initialEnergy, totalEnergy, 1e-3, "Energy is not conserved at step " + i);
        }
    }

    @Test
    void testBallSeparationAfterCollision() {
        // Initialize two balls on a collision course
        Ball ball1 = new Ball(10, 10, 1, 0, 1, 1); // Moving right
        Ball ball2 = new Ball(12, 10, -1, 0, 1, 1); // Moving left
        PhysicsEngine physicsEngine = new PhysicsEngine(50, 50);
        physicsEngine.setCollisionStrategy(new BallCollisionStrategy());

        double deltaT = 0.1; // Time step
        int steps = 100; // Number of simulation steps

        for (int i = 0; i < steps; i++) {
            // Update the physics engine
            physicsEngine.update(new PhysicalObject[]{ball1, ball2}, deltaT);

            // Calculate the distance between the two balls
            double dx = ball2.getX() - ball1.getX();
            double dy = ball2.getY() - ball1.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Log positions and velocities for debugging
            System.out.printf("Step %d: Ball1 (x=%.3f, y=%.3f, vx=%.3f, vy=%.3f), Ball2 (x=%.3f, y=%.3f, vx=%.3f, vy=%.3f), Distance=%.3f%n",
                    i, ball1.getX(), ball1.getY(), ball1.getVX(), ball1.getVY(),
                    ball2.getX(), ball2.getY(), ball2.getVX(), ball2.getVY(), distance);

            // Assert that the balls are not overlapping
            assertTrue(distance >= ball1.getRadius() + ball2.getRadius(),
                    "Balls are overlapping at step " + i);
        }
    }



    @Test
    void testEnergyConservationWithGravity() {
        // Skapa en boll med initial position och hastighet
        Ball ball = new Ball(10, 10, 0, 0, 1, 1); // Boll med massa 1 kg, stillastående
        PhysicsEngine physicsEngine = new PhysicsEngine( 50, 50);
        physicsEngine.setCollisionStrategy(new BallCollisionStrategy());

        double deltaT = 0.1; // Tidssteg

        // Beräkna initial energi (potentiell + kinetisk)
        double initialKineticEnergy = 0.5 * ball.m * (ball.vx * ball.vx + ball.vy * ball.vy);
        double initialPotentialEnergy = ball.m * 9.82 * ball.y;
        double initialTotalEnergy = initialKineticEnergy + initialPotentialEnergy;

        // Applicera gravitation
        physicsEngine.applyGravity(ball, deltaT);

        // Beräkna slutlig energi (potentiell + kinetisk)
        double finalKineticEnergy = 0.5 * ball.m * (ball.vx * ball.vx + ball.vy * ball.vy);
        double finalPotentialEnergy = ball.m * 9.82 * ball.y;
        double finalTotalEnergy = finalKineticEnergy + finalPotentialEnergy;

        // Logga energier för felsökning
        System.out.printf("Initial Energy: %.6f, Final Energy: %.6f%n", initialTotalEnergy, finalTotalEnergy);

        // Kontrollera att den totala energin är bevarad
        assertEquals(initialTotalEnergy, finalTotalEnergy, 1e-6, "Energy is not conserved when applying gravity");
    }

}