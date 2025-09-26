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
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 1);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3,2);
		//balls[0] = new Ball(width / 3, height * 0.9, 0, -1, 0.2, 1);
		//balls[1] = new Ball(width / 3, height * 0.6, 0, 1, 0.2,1);
	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT

		ballCollision(balls[0], balls[1]);

		for (Ball b : balls) {
			borderCollision(b);
			applyGravity(b, deltaT);

			updateBallPosition(b, deltaT);
		}
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
	private void borderCollision(Ball b) {

		// detect collision with the border
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
	private void applyGravity(Ball b, double deltaT) {
		double g = 9.82 * b.m;

		b.vy = b.vy - deltaT * g;
	}
	
	/**
	 * Detect and handle collision of two balls.
	 * If a collision is detected, the directions of the balls are changed.
	 */
	private void ballCollision(Ball b1, Ball b2) {

		if(ballCollisionDetected(b1, b2)) {
			double theta = Math.atan2(b2.y - b1.y, b2.x - b1.x);

			//separateBalls(b1,b2);

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
	private void handleHorizontalCollision(Ball b1, Ball b2) {
		double ballOneNewVX;
		double ballTwoNewVX;

		ballOneNewVX = (b1.m * b1.vx + 2 * b2.m * b2.vx - b2.m * b2.vx)/(b1.m + b2.m);
		ballTwoNewVX = b1.vx - b2.vx + ballOneNewVX;

		b1.vx = ballOneNewVX;
		b2.vx = ballTwoNewVX;
	}

	/**
	 * Detect if two balls are colliding.
	 */
	private boolean ballCollisionDetected(Ball b1, Ball b2) {
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

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		
		Ball(double x, double y, double vx, double vy, double r, double m) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.m = m;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius, m;
	}
}
