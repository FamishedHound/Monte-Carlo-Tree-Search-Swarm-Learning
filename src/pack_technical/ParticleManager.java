package pack_technical;

import java.util.ArrayList;

import pack_boids.Boid_generic;
import processing.core.PApplet;

// currently unused
public class ParticleManager {

	PApplet parent; // the processing app (allows access to its functions)
	ArrayList<Boid_generic> projectiles; // An ArrayList for all the projectiles

	public ParticleManager(PApplet p) {
		parent = p;
	}

	public void draw() {

	}
}
