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
		balls = new Ball[2];

		balls[0] = new Ball(width / 3, height * 0.9, 1, -1, 0.2, 1);
		balls[1] = new Ball(width / 3.2, height * 0.7, -4, 1, 0.3,2);

		// Initialize the physics engine
		physicsEngine = new PhysicsEngine(width, height);
		physicsEngine.setCollisionStrategy(new BallCollisionStrategy());
	}

	void step(double deltaT) {
		// Update position of balls and handle wall collisions
		int subSteps = 2; // amount of sub-steps to improve accuracy
		double subDt = deltaT / subSteps;

		for (int i = 0; i < subSteps; i++) {
			// Update the physics engine
			physicsEngine.update(balls, subDt);
		}

	}
}
