package pack_technical;

import java.util.ArrayList;
import pack_AI.AI_internal_model;
import pack_boids.Boid_imaginary;
import pack_boids.Boid_observer;
import pack_boids.Boid_standard;
import pack_boids.Boid_generic;
import processing.core.PApplet;
import processing.core.PVector;

/*
 * A flock holds a list of real_boids, and performs operations concerning removing and adding real_boids
 * to the simulation in a tidy manner. can also perform flock-wide operations such as returning the
 * nearest boid.
 */
public class FlockManager {

	public ArrayList<Boid_generic> getBoids() {
		return boids;
	}

	ArrayList<Boid_generic> boids; // the list of boids that loads real or imaginary

	public ArrayList<Boid_generic> getReal_boids() {
		return real_boids;
	}

	ArrayList<Boid_generic> real_boids; // An ArrayList for all the real_boids
	ArrayList<Boid_generic> imaginary_boids; // An ArrayList for all the real_boids
	Boid_observer camera_boid;
	PApplet parent;
	boolean real;
	boolean simulation=false;

	public FlockManager(PApplet parent, boolean real) {
		this.real = real;
		this.parent = parent;
		if (real) {
			camera_boid = new Boid_observer(parent, (float) parent.width / 2, (float) parent.height / 2,
					GameManager.getTeam_number() + 1);
			// camera is the final team
			real_boids = new ArrayList<Boid_generic>(); // Initialize the ArrayList
			real_boids.clear();
		} else {
			imaginary_boids = new ArrayList<Boid_generic>(); // Initialize the ArrayList
			imaginary_boids.clear();
		}

	}

	public FlockManager(PApplet parent, boolean real,boolean simulation) {
		this.real = real;
		this.parent = parent;
		this.simulation=simulation;
		if (real) {
			if(!simulation)
			camera_boid = new Boid_observer(parent, (float) parent.width / 2, (float) parent.height / 2,
					GameManager.getTeam_number() + 1);
			// camera is the final team
			real_boids = new ArrayList<Boid_generic>(); // Initialize the ArrayList
			real_boids.clear();
		} else {
			imaginary_boids = new ArrayList<Boid_generic>(); // Initialize the ArrayList
			imaginary_boids.clear();
		}

	}

	public void reset() {
		if (real)
			real_boids.clear();
		else
			imaginary_boids.clear();
	}

	public void run(int steps) {
		if (real)
			boids = real_boids;
		else
			boids = imaginary_boids;
		for (Boid_generic b : boids) {
			if (!b.get_alive()) {
				remove_boid(b);
				break;
			}
		}
		for (int step = 0; step < steps; step++) {
			// first rune camera that does not interfere with the flock
			if (camera_boid != null)
				if (step == 0)
					camera_boid.run(true,simulation);
				else
					camera_boid.run(false,simulation);
			for (Boid_generic b : boids) {
				if (b instanceof Boid_standard) { // if real
					if (step == 0) // only render on step 0
						b.run(boids, true,simulation);
					else
						b.run(boids, false,simulation);
				} else { // if imaginary
					if (step == steps - 1) // only render on last step
						b.run(boids, true,simulation); // should be true
					else
						b.run(boids, false,simulation);
				}
			}
		}
	}

	public Boid_generic remove_boid(Boid_generic b) {
		b.on_death();
		if (real)
			real_boids.remove(b);
		else
			imaginary_boids.remove(b);
		return b;
	}

	public Boid_generic add_boid(Boid_generic b) {
		// give the boid the ai derived from the team
		if (real)
			real_boids.add(b);
		else
			imaginary_boids.add(b);
		return b;
	}

	public int get_boid_count() {
		if (real)
			return real_boids.size();
		else
			return imaginary_boids.size();
	}

	// used in creating an imaginary universe in which the real_boids are not real
	public void import_imaginary_boids(ArrayList<Boid_generic> boids_in, AI_internal_model internal_model) {
		PVector vect_in = new PVector(0, 0);
		for (Boid_generic b : boids_in) {

			vect_in = new PVector(b.getLocation_history().x, b.getLocation_history().y);
			Boid_imaginary b2 = new Boid_imaginary(parent, vect_in.x, vect_in.y, b.getTeam(), b);

			vect_in = new PVector(b.getVelocity_history().x, b.getVelocity_history().y);
			b2.setVelocity(new PVector(vect_in.x, vect_in.y));

			vect_in = new PVector(b.getAcceleration_history().x, b.getAcceleration_history().y);
			b2.setAcceleration(new PVector(vect_in.x, vect_in.y));
			b2.setAi(internal_model.get_ai_team(b.getTeam()));
			b2.setAngle(b.getAngle_history());
			b2.setTeam(b.getTeam());
			add_boid(b2);
		}
	}

	public ArrayList<Boid_generic> get_all_boids() {
		if (real)
			return real_boids;
		else
			return imaginary_boids;
	}

	public Boid_generic get_nearest_boid(int select_dist) {
		PVector mouse_pos = new PVector(parent.mouseX, parent.mouseY);
		Boid_generic final_b = null;
		float dist_record = Integer.MAX_VALUE;
		// attempt select camera first, this is not part of the flock
		float dist = mouse_pos.dist(camera_boid.getLocation());
		if ((dist < dist_record) && dist < select_dist) {
			final_b = camera_boid;
			dist_record = dist;
		}
		for (Boid_generic b : real_boids) {
			dist = mouse_pos.dist(b.getLocation_history());
			if (b instanceof Boid_standard) // if it is real
				if ((dist < dist_record) && dist < select_dist) {
					final_b = b;
					dist_record = dist;
				}
		}
		return final_b;
	}

	public Boid_observer getCamera_boid() {
		return camera_boid;
	}

	public void setCamera_boid(Boid_observer camera_boid) {
		this.camera_boid = camera_boid;
	}

}
