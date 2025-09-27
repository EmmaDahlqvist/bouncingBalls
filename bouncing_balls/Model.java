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

	PhysicsEngine physicsEngine;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[4];
		balls[0] = new Ball(width / 3, height * 0.9, 1, -1, 0.2, 1);
		balls[1] = new Ball(width / 3.2, height * 0.7, -4, 1, 0.3,2);
		balls[2] = new Ball(width / 3, height * 0.9, 1, 1, 0.2, 1);
		balls[3] = new Ball(width / 2, height * 0.9, -1, 1, 0.2,1);

		physicsEngine = new PhysicsEngine(width, height);


	}

	void step(double deltaT) {
		// Update position of balls and handle wall collisions
		int subSteps = 2; // amount of sub-steps to improve accuracy
		double subDt = deltaT / subSteps;

		for (int i = 0; i < subSteps; i++) {
			physicsEngine.update(balls, subDt);
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

		//System.out.println("Total energy: " + totalEnergy + ", total momentum: " + totalMomentum);

		totalEnergy = 0;
		System.out.println("\n--- Energy and Momentum ---");
		for (Ball b : balls) {
			double speed2 = b.vx*b.vx + b.vy*b.vy;
			double kinetic = 0.5 * b.m * speed2;
			double potential = b.m * 9.82 * b.y;
			double energy = kinetic + potential;
			totalEnergy += energy;

			//System.out.printf(
			//		"Ball %s: x=%.2f y=%.2f vx=%.2f vy=%.2f KE=%.3f PE=%.3f E=%.3f%n\n",
			//		b, b.x, b.y, b.vx, b.vy, kinetic, potential, energy
			//);
		}

		System.out.printf("Total E=%.3f",totalEnergy
				);
		System.out.println("\n--- END ---\n");
	}
}
