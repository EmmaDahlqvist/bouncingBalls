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
		// ---- Substepping: kör fysiken i 8 mindre delsteg för mindre penetrering/energiläckage
		final int sub = 8;
		final double h = deltaT / sub;

		// (behåll en utskrift per step för att matcha din tidigare struktur)
		System.out.println(new HashMap<Ball, Set<Ball>>());

		for (int s = 0; s < sub; s++) {
			Map<Ball, Set<Ball>> visitedBalls = new HashMap<>();

			for (Ball b : balls) {

				// Semi-implicit (symplectic) Euler:
				applyGravity(b, h);       // v += a*h
				updateBallPosition(b, h); // x += v*h

				borderCollision(b);

				for (Ball b2 : balls) {
					if (b.equals(b2)) {
						continue;
					}

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

		// Left wall
		if (b.x < b.radius) {
			handleXOverlap(b);
			if (b.vx < 0) b.vx *= -1; // flip endast om på väg in
		}
		// Right wall
		if (b.x > areaWidth - b.radius) {
			handleXOverlap(b);
			if (b.vx > 0) b.vx *= -1;
		}
		// Bottom wall
		if (b.y < b.radius) {
			handleYOverlap(b);
			if (b.vy < 0) b.vy *= -1;
		}
		// Top wall
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

	private double getOverlap(double center, double radius, double borderLength)
	{
		if (center < radius) {
			return radius - center;
		} else if (center > borderLength - radius) {
			return -(center + radius - borderLength);
		}
		return 0; // no overlap
	}

	/**
	 * Apply gravity to the ball by changing its vertical speed.
	 */
	public void applyGravity(Ball b, double deltaT) {
		double g = 9.82;
		// symplectic Euler: uppdatera v först
		b.vy = b.vy - deltaT * g;
	}

	/**
	 * Detect and handle collision of two balls.
	 * If a collision is detected, the directions of the balls are changed.
	 */
	public void ballCollision(Ball b1, Ball b2) {
		if (!ballCollisionDetected(b1, b2)) return;

		// Vinkel för kollisionsnormal
		double theta = Math.atan2(b2.y - b1.y, b2.x - b1.x);

		// Rotera till kollisionsram
		rotateCollisionLine(b1, b2, theta);

		// Relativ hastighet längs normalen (x-komponent i roterad ram)
		double rel = b2.vx - b1.vx;

		// Endast om de rör sig MOT varandra
		if (rel < 0) {
			handleHorizontalCollision(b1, b2);
		}

		// Rotera tillbaka till världens ram
		rotateCollisionLine(b1, b2, -theta);

		// Separera positioner efter hastighetsuppdateringen
		separateBalls(b1, b2);
	}

	private void separateBalls(Ball b1, Ball b2) {
		double dx = b2.x - b1.x;
		double dy = b2.y - b1.y;

		double distance = Math.sqrt(dx*dx + dy*dy);
		if (distance == 0) { // undvik /0
			distance = b1.radius + b2.radius;
			dx = distance; dy = 0;
		}

		double overlap = (b1.radius + b2.radius) - distance;

		// liten slop: tillåt mikropenetration för att undvika överkorrigering
		double slop = 1e-6;
		if (overlap <= slop) return; // inget att separera

		// Catto-style: ta bara en procentandel för stabilitet (minskar energidrift)
		double percent = 0.2; // 20% av kvarvarande penetration

		// Fördela överlapp baserat på massa
		double totalMass = b1.m + b2.m;
		double nx = dx / distance;
		double ny = dy / distance;

		double corr = (overlap - slop) * percent;
		double b1Adjustment = corr * (b2.m / totalMass);
		double b2Adjustment = corr * (b1.m / totalMass);

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
		double u2 = b2.vx;

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
		double r  = b1.radius + b2.radius;
		double dist2 = dx*dx + dy*dy;

		// epsilon + strikt "<" för att undvika falska tangentkollisioner
		double eps = 1e-9;
		return dist2 < r*r - eps;
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
