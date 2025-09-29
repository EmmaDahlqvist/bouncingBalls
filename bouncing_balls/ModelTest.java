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
    void testBallDirectionAfterObliqueCollisionWithGravity() {
        PhysicsEngine physicsEngine = new PhysicsEngine(100, 100);
        physicsEngine.setCollisionStrategy(new BallCollisionStrategy());

        // Initialize balls
        Ball ball1 = new Ball(10, 10, 1, 1, 1, 1); // Moving diagonally up-right
        Ball ball2 = new Ball(12, 12, -1, -1, 1, 1); // Moving diagonally down-left

        double deltaT = 0.1;
        double gravity = -9.82;

        // Apply gravity
        ball1.setVY(ball1.getVY() + gravity * deltaT);
        ball2.setVY(ball2.getVY() + gravity * deltaT);

        // Calculate expected velocities after collision
        double m1 = ball1.getMass();
        double m2 = ball2.getMass();
        double u1x = ball1.getVX(), u1y = ball1.getVY();
        double u2x = ball2.getVX(), u2y = ball2.getVY();

        // Relative velocity along the collision axis
        double dx = ball2.getX() - ball1.getX();
        double dy = ball2.getY() - ball1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double nx = dx / distance, ny = dy / distance;

        // Velocity components along the collision axis
        double v1n = u1x * nx + u1y * ny;
        double v2n = u2x * nx + u2y * ny;

        // Post-collision normal velocities (elastic collision)
        double v1nAfter = (v1n * (m1 - m2) + 2 * m2 * v2n) / (m1 + m2);
        double v2nAfter = (v2n * (m2 - m1) + 2 * m1 * v1n) / (m1 + m2);

        // Tangential velocities remain unchanged
        double v1t = u1x * -ny + u1y * nx;
        double v2t = u2x * -ny + u2y * nx;

        // Convert back to x, y components
        double expectedVX1 = v1nAfter * nx - v1t * ny;
        double expectedVY1 = v1nAfter * ny + v1t * nx;
        double expectedVX2 = v2nAfter * nx - v2t * ny;
        double expectedVY2 = v2nAfter * ny + v2t * nx;

        // Simulate collision
        physicsEngine.update(new PhysicalObject[]{ball1, ball2}, deltaT);

        // Assert velocities
        assertEquals(expectedVX1, ball1.getVX(), 1e-6, "Boll 1 har fel VX efter kollision");
        assertEquals(expectedVY1, ball1.getVY(), 1e-6, "Boll 1 har fel VY efter kollision");
        assertEquals(expectedVX2, ball2.getVX(), 1e-6, "Boll 2 har fel VX efter kollision");
        assertEquals(expectedVY2, ball2.getVY(), 1e-6, "Boll 2 har fel VY efter kollision");
    }

    @Test
    void testBallDirectionAfterCollisionWithGravity() {
        PhysicsEngine physicsEngine = new PhysicsEngine(100, 100);
        physicsEngine.setCollisionStrategy(new BallCollisionStrategy());

        // Testfall: Boll 1 rör sig rakt mot Boll 2
        Ball ball1 = new Ball(10, 10, 1, 0, 1, 1); // Ball 1 moving right
        Ball ball2 = new Ball(12, 10, -1, 0, 1, 1); // Ball 2 moving left

        double deltaT = 0.1;
        double gravity = -9.82;

        // Applicera gravitation på bollarna innan kollision
        ball1.setVY(ball1.getVY() + gravity * deltaT);
        ball2.setVY(ball2.getVY() + gravity * deltaT);

        // Simulera en kollision
        physicsEngine.update(new PhysicalObject[]{ball1, ball2}, deltaT);

        // Beräkna den förväntade vinkeln för Boll 1 och Boll 2 efter gravitation
        double expectedAngleBall1 = Math.atan2(ball1.getVY(), ball1.getVX());
        double expectedAngleBall2 = Math.atan2(ball2.getVY(), ball2.getVX());

        // Faktiska vinklar efter kollision
        double actualAngleBall1 = Math.atan2(ball1.getVY(), ball1.getVX());
        double actualAngleBall2 = Math.atan2(ball2.getVY(), ball2.getVX());

        // Kontrollera att vinklarna är korrekta
        assertEquals(expectedAngleBall1, actualAngleBall1, 1e-6, "Boll 1 har fel riktning efter kollision");
        assertEquals(expectedAngleBall2, actualAngleBall2, 1e-6, "Boll 2 har fel riktning efter kollision");
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