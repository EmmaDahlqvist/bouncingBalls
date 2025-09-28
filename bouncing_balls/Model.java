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
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 2);
		balls[2] = new Ball(width / 3, height * 0.9, 1, 1, 0.2, 1);
		//balls[1] = new Ball(width / 2, height * 0.9, -1, 1, 0.2,1);
		Ball b1 = balls[0];
		Ball b2 = balls[1];
	}

	void step(double deltaT) {
		final int sub = 8;
		final double h = deltaT / sub;

		System.out.println(new HashMap<Ball, Set<Ball>>());

		for (int s = 0; s < sub; s++) {
			Map<Ball, Set<Ball>> visitedBalls = new HashMap<>();

			for (Ball b : balls) {
				applyGravity(b, h);
				updateBallPosition(b, h);
				borderCollision(b);

				for (Ball b2 : balls) {
					if (b.equals(b2)) continue;

					if (visitedBalls.get(b) == null) {
						ballCollision(b2, b);
					}
					else if (!visitedBalls.get(b).contains(b2)) {
						ballCollision(b2, b);
					}
					visitedBalls.putIfAbsent(b, new HashSet<>());
					visitedBalls.get(b).add(b2);

					visitedBalls.putIfAbsent(b2, new HashSet<>());
					visitedBalls.get(b2).add(b);
				}
			}
		}

		Ball b1 = balls[0];
		Ball b2 = balls[1];
		double v1 = Math.sqrt(b1.vx*b1.vx + b1.vy*b1.vy);
		double v2 = Math.sqrt(b2.vx*b2.vx + b2.vy*b2.vy);
		double energy = b1.m*v1*v1 + b2.m*v2*v2;

		final double g = 9.82;
		double Ek = 0, Ep = 0;
		for (Ball b : balls) {
			v2 = b.vx*b.vx + b.vy*b.vy;
			Ek += 0.5 * b.m * v2;
			Ep += b.m * g * b.y;
		}
		double E = Ek + Ep;
		System.out.println(String.format("Ek=%.5f  Ep=%.5f  E=%.5f", Ek, Ep, E));
	}

	private void updateBallPosition(Ball b, double deltaT) {
		b.x += deltaT * b.vx;
		b.y += deltaT * b.vy;
	}

	public void borderCollision(Ball b) {
		if (b.x < b.radius) {
			handleXOverlap(b);
			if (b.vx < 0) b.vx *= -1;
		}
		if (b.x > areaWidth - b.radius) {
			handleXOverlap(b);
			if (b.vx > 0) b.vx *= -1;
		}
		if (b.y < b.radius) {
			handleYOverlap(b);
			if (b.vy < 0) b.vy *= -1;
		}
		if (b.y > areaHeight - b.radius) {
			handleYOverlap(b);
			if (b.vy > 0) b.vy *= -1;
		}
	}

	private void handleXOverlap(Ball b) {
		b.x = b.x + getOverlap(b.x, b.radius, areaWidth);
	}

	private void handleYOverlap(Ball b) {
		b.y = b.y + getOverlap(b.y, b.radius, areaHeight);
	}

	private double getOverlap(double center, double radius, double borderLength) {
		if (center < radius) {
			return radius - center;
		} else if (center > borderLength - radius) {
			return -(center + radius - borderLength);
		}
		return 0;
	}

	public void applyGravity(Ball b, double deltaT) {
		double g = 9.82;
		b.vy = b.vy - deltaT * g;
	}

	public void ballCollision(Ball b1, Ball b2) {
		if (!ballCollisionDetected(b1, b2)) return;

		double theta = Math.atan2(b2.y - b1.y, b2.x - b1.x);
		rotateCollisionLine(b1, b2, theta);

		double rel = b2.vx - b1.vx;
		if (rel < 0) {
			handleHorizontalCollision(b1, b2);
		}

		rotateCollisionLine(b1, b2, -theta);
		separateBalls(b1, b2);
	}

	private void separateBalls(Ball b1, Ball b2) {
		double dx = b2.x - b1.x;
		double dy = b2.y - b1.y;

		double distance = Math.sqrt(dx*dx + dy*dy);
		if (distance == 0) {
			distance = b1.radius + b2.radius;
			dx = distance; dy = 0;
		}

		double overlap = (b1.radius + b2.radius) - distance;
		double slop = 1e-6;
		if (overlap <= slop) return;

		double percent = 0.2;

		double totalMass = b1.m + b2.m;
		double nx = dx / distance;
		double ny = dy / distance;

		double corr = (overlap - slop) * percent;
		double b1Adjustment = corr * (b2.m / totalMass);
		double b2Adjustment = corr * (b1.m / totalMass);

		b1.x -= b1Adjustment * nx;
		b1.y -= b1Adjustment * ny;
		b2.x += b2Adjustment * nx;
		b2.y += b2Adjustment * ny;
	}

	/**
	 * Ny variant: elastisk 1D-kollision i roterad ram, energi bevaras bättre.
	 */
	public void handleHorizontalCollision(Ball b1, Ball b2) {
		double m1 = b1.m;
		double m2 = b2.m;
		double u1 = b1.vx;
		double u2 = b2.vx;

		// klassisk formel för elastisk kollision mellan två massor
		double v1 = (u1*(m1 - m2) + 2*m2*u2) / (m1 + m2);
		double v2 = (u2*(m2 - m1) + 2*m1*u1) / (m1 + m2);

		b1.vx = v1;
		b2.vx = v2;
	}

	public boolean ballCollisionDetected(Ball b1, Ball b2) {
		double dx = b2.x - b1.x;
		double dy = b2.y - b1.y;
		double r  = b1.radius + b2.radius;
		double dist2 = dx*dx + dy*dy;

		double eps = 1e-9;
		return dist2 < r*r - eps;
	}

	private void rotateCollisionLine(Ball b1, Ball b2, double theta) {
		rotateVectors(b1, theta);
		rotateVectors(b2, theta);
	}

	private void rotateVectors(Ball ball, double theta) {
		double newVX2 = ball.vx * Math.cos(theta) - ball.vy * Math.sin(theta);
		double newVY2 = ball.vx * Math.sin(theta) + ball.vy * Math.cos(theta);
		ball.vx = newVX2;
		ball.vy = newVY2;
	}
}
