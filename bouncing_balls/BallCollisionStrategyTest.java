package bouncing_balls;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BallCollisionStrategyTest {

    @Test
    void handleCollision() {
        BallCollisionStrategy strategy = new BallCollisionStrategy();
        Ball ball1 = new Ball(0, 0, 1, 0, 1, 1);
        Ball ball2 = new Ball(1.5, 0, -1, 0, 1, 1);

        strategy.handleCollision(ball1, ball2);

        // Assert that velocities are updated after collision
        assertNotEquals(1, ball1.getVX());
        assertNotEquals(-1, ball2.getVX());
    }

    @Test
    void rectToPolar() {
        BallCollisionStrategy strategy = new BallCollisionStrategy();
        double[] polar = strategy.rectToPolar(3, 4, 0, 0);

        assertEquals(5, polar[0], 0.001); // r = sqrt(3^2 + 4^2)
        assertEquals(Math.atan2(4, 3), polar[1], 0.001); // theta = atan2(4, 3)
    }

    @Test
    void polarToRect() {
        BallCollisionStrategy strategy = new BallCollisionStrategy();
        double[] rect = strategy.polarToRect(5, Math.atan2(4, 3), 0, 0);

        assertEquals(3, rect[0], 0.001); // x = r * cos(theta)
        assertEquals(4, rect[1], 0.001); // y = r * sin(theta)
    }

    @Test
    void calculateCollisionAngle() {
        BallCollisionStrategy strategy = new BallCollisionStrategy();
        Ball ball1 = new Ball(0, 0, 0, 0, 1, 1);
        Ball ball2 = new Ball(1, 1, 0, 0, 1, 1);

        double angle = strategy.calculateCollisionAngle(ball1, ball2);

        assertEquals(Math.PI / 4, angle, 0.001); // atan2(1, 1) = π/4
    }

    @Test
    void handleHorizontalCollision() {
        BallCollisionStrategy strategy = new BallCollisionStrategy();
        Ball ball1 = new Ball(0, 0, 1, 0, 1, 2); // Mass = 2
        Ball ball2 = new Ball(0, 0, -1, 0, 1, 1); // Mass = 1

        double[] newVelocities = strategy.handleHorizontalCollision(ball1, ball2, 1, -1);

        assertEquals(-1.0 / 3, newVelocities[0], 0.001); // New velocity of ball1
        assertEquals(5.0 / 3, newVelocities[1], 0.001); // New velocity of ball2
    }


    @Test
    void separateBalls() {
        BallCollisionStrategy strategy = new BallCollisionStrategy();
        Ball ball1 = new Ball(0, 0, 0, 0, 1, 1);
        Ball ball2 = new Ball(1.5, 0, 0, 0, 1, 1);

        strategy.separateBalls(ball1, ball2);

        // Assert that balls are no longer overlapping
        double distance = Math.sqrt(Math.pow(ball2.getX() - ball1.getX(), 2) + Math.pow(ball2.getY() - ball1.getY(), 2));
        assertTrue(distance >= ball1.getRadius() + ball2.getRadius());
    }
}