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
    void testSeparateBallsEnergyConservation() {
        // Skapa två bollar som överlappar
        Ball b1 = new Ball(10, 10, 0, 0, 2, 1); // Boll 1
        Ball b2 = new Ball(11, 10, 0, 0, 2, 1); // Boll 2 (överlappande)

        PhysicsEngine physicsEngine = new PhysicsEngine();

        // Beräkna initial energi
        double initialEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

        System.out.println("Before separation: b1.x=" + b1.x + ", b1.y=" + b1.y + ", b2.x=" + b2.x + ", b2.y=" + b2.y);

        // Anropa separateBalls för att separera bollarna
        physicsEngine.handleCollisionBetweenObjects(b1, b2);
        System.out.println("After separation: b1.x=" + b1.x + ", b1.y=" + b1.y + ", b2.x=" + b2.x + ", b2.y=" + b2.y);

        // Beräkna slutlig energi
        double finalEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

        // Kontrollera att energin är bevarad
        assertEquals(initialEnergy, finalEnergy, 1e-6, "Energy is not conserved after separating balls");
    }

    @Test
    void testEnergyConservationWithNonHorizontalCollision() {
        // Skapa två bollar med en vinkel mellan dem
        Ball b1 = new Ball(10, 10, 1, 1, 1, 1); // Boll 1 rör sig diagonalt uppåt höger
        Ball b2 = new Ball(12, 12, -1, -1, 1, 1); // Boll 2 rör sig diagonalt nedåt vänster

        PhysicsEngine physicsEngine = new PhysicsEngine();

        // Beräkna initial energi
        double initialEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

        System.out.println("Before collision: b1.vx=" + b1.vx + ", b1.vy=" + b1.vy + ", b2.vx=" + b2.vx + ", b2.vy=" + b2.vy);

        // Anropa handleCollisionBetweenObjects för att simulera kollision
        physicsEngine.handleCollisionBetweenObjects(b1, b2);

        System.out.println("After collision: b1.vx=" + b1.vx + ", b1.vy=" + b1.vy + ", b2.vx=" + b2.vx + ", b2.vy=" + b2.vy);

        // Beräkna slutlig energi
        double finalEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

        // Kontrollera att energin är bevarad
        assertEquals(initialEnergy, finalEnergy, 1e-6, "Energy is not conserved in non-horizontal collision");
    }

    @Test
    void testEnergyConservationWithMultipleCollisions() {
        // Create two balls with high velocities for repeated collisions
        Ball b1 = new Ball(10, 10, 5, 0, 1, 1); // Ball 1 moving right
        Ball b2 = new Ball(15, 10, -5, 0, 1, 1); // Ball 2 moving left

        PhysicsEngine physicsEngine = new PhysicsEngine();
        double deltaT = 0.01; // Small time step for precision
        int steps = 100; // Number of simulation steps

        // Calculate initial energy
        double initialEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

        for (int i = 0; i < steps; i++) {
            // Update positions
            physicsEngine.updatePosition(b1, deltaT);
            physicsEngine.updatePosition(b2, deltaT);

            // Check for collisions and handle them
            physicsEngine.handleCollisionBetweenObjects(b1, b2);

            // Calculate total energy after each step
            double totalEnergy = 0.5 * b1.m * (b1.vx * b1.vx + b1.vy * b1.vy)
                    + 0.5 * b2.m * (b2.vx * b2.vx + b2.vy * b2.vy);

            // Log energy for debugging
            System.out.printf("Step %d: Total Energy = %.6f%n", i, totalEnergy);

            // Assert that energy is approximately conserved
            assertEquals(initialEnergy, totalEnergy, 1e-3, "Energy is not conserved at step " + i);
        }
    }
    @Test
    void testEnergyConservationWithGravity() {
        // Skapa en boll med initial position och hastighet
        Ball ball = new Ball(10, 10, 0, 0, 1, 1); // Boll med massa 1 kg, stillastående
        PhysicsEngine physicsEngine = new PhysicsEngine();
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