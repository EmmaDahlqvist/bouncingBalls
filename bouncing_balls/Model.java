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
		//balls[2] = new Ball(width / 3, height * 0.9, 1, 1, 0.2, 1);
		//balls[3] = new Ball(width / 2, height * 0.9, -1, 1, 0.2,1);


	}

	void step(double deltaT) {
		// Update position of balls and handle wall collisions
		for (Ball b : balls) {
			//b.applyGravity(deltaT);
			b.updatePosition(deltaT);
			b.handleWallCollision(areaWidth, areaHeight);
		}

		// Collision between balls
		for (int i = 0; i < balls.length; i++) {
			for (int j = i + 1; j < balls.length; j++) {
				Ball b1 = balls[i];
				Ball b2 = balls[j];
				b1.handleCollisionWithPhysicalObject(b2);
			}
		}

		// Beräkna total energi och rörelsemängd
		calculateTotalEnergyAndMomentum();
	}

	private void calculateTotalEnergyAndMomentum() {
		double totalEnergy = 0;
		double totalMomentum = 0;

		for (Ball b : balls) {
			double v = Math.sqrt(b.vx * b.vx + b.vy * b.vy);
			totalEnergy += 0.5 * b.m * v * v;
			totalMomentum += b.m * v;
		}

		System.out.println("Total energy: " + totalEnergy + ", total momentum: " + totalMomentum);
	}
}
