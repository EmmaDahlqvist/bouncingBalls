package bouncing_balls;

import java.util.*;

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
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[3];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 1);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3,2);
		balls[2] = new Ball(width / 3, height * 0.9, 1, 1, 0.2, 1);
		//balls[1] = new Ball(width / 2, height * 0.9, -1, 1, 0.2,1);
		Ball b1 = balls[0];
		Ball b2 = balls[1];


	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		Map<Ball, Set<Ball>> visitedBalls = new HashMap<>();

		System.out.println(visitedBalls);


		for (Ball b : balls) {

			applyGravity(b, deltaT);

			updateBallPosition(b, deltaT);

			borderCollision(b);
			for(Ball b2 : balls) {
				if(b.equals(b2)) {
					continue;
				}

				if(visitedBalls.get(b) == null) {
					ballCollision(b2, b);
				}
				else if(!visitedBalls.get(b).contains(b2)) {
					ballCollision(b2, b);
				}
				visitedBalls.putIfAbsent(b, new HashSet<>());
				visitedBalls.get(b).add(b2);

				visitedBalls.putIfAbsent(b2, new HashSet<>());
				visitedBalls.get(b2).add(b);
			}
		}

		Ball b1 = balls[0];
		Ball b2 = balls[1];
		double v1 = Math.sqrt(b1.vx*b1.vx + b1.vy*b1.vy);
		double v2 = Math.sqrt(b2.vx*b2.vx + b2.vy*b2.vy);
		double energy = b1.m*v1*v1 + b2.m*v2*v2;
		//System.out.println("b eneger: " + energy);

		final double g = 9.82;
		double Ek = 0, Ep = 0;
		for (Ball b : balls) {
			v2 = b.vx*b.vx + b.vy*b.vy;
			Ek += 0.5 * b.m * v2;
			Ep += b.m * g * b.y;   // y=0 vid golvet i din modell
		}
		double E = Ek + Ep;
		System.out.println(String.format("Ek=%.5f  Ep=%.5f  E=%.5f", Ek, Ep, E));
	}

	/**
	 * Update the position of the ball according to its speed.
	 */
	private void updateBallPosition(Ball b, double deltaT) {
		b.x += deltaT * b.vx;
		b.y += deltaT * b.vy;
	}

	/**
	 * Detect and handle collision of a ball with the border of the area.
	 * If a collision is detected, the direction of the ball is changed.
	 */
	public void borderCollision(Ball b) {

		if (b.x < b.radius || b.x > areaWidth - b.radius) {
			handleXOverlap(b);
			b.vx *= -1; // change direction of ball
		}
		if (b.y < b.radius || b.y > areaHeight - b.radius) {
			handleYOverlap(b);
			b.vy *= -1;
		}
	}

	private void handleXOverlap(Ball b) {
		b.x = b.x + getOverlap(b.x, b.radius, areaWidth);
	}

	private void handleYOverlap(Ball b) {
		b.y = b.y + getOverlap(b.y, b.radius, areaHeight);
	}


	private double getOverlap(double center, double radius, double borderLength)
	{
		if(center < radius) {
			return radius - center;
		} else if(center > borderLength - radius) {
			return -(center + radius - borderLength);
		}
		return 0; // no overlap
	}

	/**
	 * Apply gravity to the ball by changing its vertical speed.
	 */
	public void applyGravity(Ball b, double deltaT) {
		double g = 9.82;

		b.vy = b.vy - deltaT * g;
	}


	/**
	 * Detect and handle collision of two balls.
	 * If a collision is detected, the directions of the balls are changed.
	 */
	public void ballCollision(Ball b1, Ball b2) {
		if(ballCollisionDetected(b1, b2)) {

			separateBalls(b1,b2);

			double theta = Math.atan2(b2.y - b1.y, b2.x - b1.x);

			System.out.println("theta " + theta);

			// rotate velocities to align with collision axis
			rotateCollisionLine(b1, b2, theta);

			handleHorizontalCollision(b1, b2);

			// rotate velocities back
			rotateCollisionLine(b1, b2, -theta);
		}
	}

	private void separateBalls(Ball b1, Ball b2) {
		double dx = b2.x - b1.x;
		double dy = b2.y - b1.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		double overlap = (b1.radius + b2.radius) - distance;

		// Fördela överlappningen baserat på massa
		double totalMass = b1.m + b2.m;
		double b1Adjustment = overlap * (b2.m / totalMass);
		double b2Adjustment = overlap * (b1.m / totalMass);

		// Normalisera riktningen
		double nx = dx / distance;
		double ny = dy / distance;

		// Justera positionerna
		b1.x -= b1Adjustment * nx;
		b1.y -= b1Adjustment * ny;
		b2.x += b2Adjustment * nx;
		b2.y += b2Adjustment * ny;
	}

	/**
	 * Handle collision of two balls moving in horizontal direction
	 */
	public void handleHorizontalCollision(Ball b1, Ball b2) {
		double v1;
		double v2;

		double m1 = b1.m;
		double m2 = b2.m;
		double u1 = b1.vx;
		double u2= b2.vx;

		v1 = (m1*u1 + 2*m2*u2 - m2*u1)/(m1 + m2);
		v2 = u1 - u2 + v1;

		b1.vx = v1;
		System.out.println("b1: " + b1.vx);
		b2.vx = v2;
		System.out.println("b2: " + b2.vx);

	}

	/**
	 * Detect if two balls are colliding.
	 */
	public boolean ballCollisionDetected(Ball b1, Ball b2) {
		double dx = b2.x - b1.x;
		double dy = b2.y - b1.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		return distance <= (b1.radius + b2.radius);
	}

	/**
	 * Rotate the velocity vectors of two balls by an angle theta.
	 */
	private void rotateCollisionLine(Ball b1, Ball b2, double theta) {
		rotateVectors(b1, theta);
		rotateVectors(b2, theta);
	}

	/**
	 * Rotate the velocity vector of a ball by an angle theta.
	 */
	private void rotateVectors(Ball ball, double theta) {
		double newVX2 = ball.vx * Math.cos(theta) - ball.vy * Math.sin(theta);
		double newVY2 = ball.vx * Math.sin(theta) + ball.vy * Math.cos(theta);
		ball.vx = newVX2;
		ball.vy = newVY2;
	}

}
