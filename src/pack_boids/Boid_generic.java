package pack_boids;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import pack_1.Launcher;
import pack_AI.AI_internal_model;
import pack_AI.AI_type;
import pack_technical.GameManager;
import pack_technical.ParameterHandler;
import pack_technical.PatternHandler;
import processing.core.PApplet;

import processing.core.PVector;

// the generic boid class holds functions common to all boid types
public class Boid_generic {

	Color fillcol, linecol; // not in generic boid because not all boid suse the same colours
	ArrayList<PVector> velocity_history = new ArrayList<PVector>();
	ArrayList<PVector> location_history = new ArrayList<PVector>();
	ArrayList<PVector> acceleration_history = new ArrayList<PVector>();
	ArrayList<Float> heading_history = new ArrayList<Float>();
	ArrayList<Double> angle_history = new ArrayList<Double>();
	PApplet parent; // the processing app (allows access to it's functions)
	public PVector location, velocity, acceleration = new PVector(0, 0);
	PVector prev_vertex; // used for not drawing trails when a boid wraps around
	float size; // determines drawing size
	float maxsteer = 0.02f; // maximum steering force
	float maxspeed = 3.0f; // maximum speed
	double angle; // current pointing angle in DEGREES
	int team; // determines team
	float fireangle;
	float firerange;
	int reload_time; // steps taken to reload, counts down
	boolean alive = true; // is alive?
	boolean canfire = false; // is able to fire laser?
	boolean isalone = true;
	int current_reload; // current reload progress

	public boolean isMoveable() {
		return moveable;
	}

	boolean moveable = true;
	public int getId() {
		return id;
	}
	public void setToMove(boolean b){
		moveable=b;
	}
	private int id;
	AI_type ai;
	ParameterHandler pHandler = new ParameterHandler();
	public boolean isHasFailed() {
		return hasFailed;
	}

	public void setHasFailed(boolean hasFailed) {
		this.hasFailed = hasFailed;
	}

	boolean hasFailed=false;
	// create a boid (application,angle,x_position,y_position)
	public Boid_generic(PApplet p, float x, float y, int t,int id) {

		this.id = id;
		parent = p;
		team = t;
		// graphics //
		fillcol = GameManager.get_team_colour(t);
		linecol = new Color(245, 245, 245);
		// for in head simulations
		location = new PVector(x, y);
		/// set new properties ///
		firerange = 140;
		fireangle = 25;
		reload_time = 100;
		size = 6.0f;
		// maxspeed = 3.0f; // float
		// maxsteer = 0.02f; // float
		current_reload = reload_time;
		velocity_history.clear();
		angle_history.clear();
		acceleration_history.clear();
		location_history.clear();
		heading_history.clear();
	}

	public void on_death() {
		// generic boid has no features

	}

	public void record_acceleration() {
		acceleration_history.add(new PVector(this.acceleration.x, this.acceleration.y));
		if (acceleration_history.size() > Launcher.getHISTORYLENGTH()) {
			acceleration_history.remove(0);
		}
	}

	public void record_history() { // done seperatley because of end of step reset
		velocity_history.add(new PVector(this.velocity.x, this.velocity.y));
		location_history.add(new PVector(this.location.x, this.location.y));
		heading_history.add(velocity.heading());
		angle_history.add(angle);
		if (location_history.size() > Launcher.getHISTORYLENGTH()) {
			velocity_history.remove(0);
			location_history.remove(0);
			heading_history.remove(0);
			angle_history.remove(0);

		}
	}

	// if real_step is true all normal actions will happen, otherwise
	// rendering is disabled, this is for 'in-mind simulation'
	public void run(ArrayList<Boid_generic> boids, boolean real_step,boolean simulation) {
		System.out.println("handled by specific boids");
	}

	void move_borders(boolean wrap) {
		if (wrap) {
			if (location_history.size() > 0) {
				if (location_history.get(0).x < -size)
					location.x = parent.width + size;
				if (location_history.get(0).y < -size)
					location.y = parent.height + size;
				if (location_history.get(0).x > parent.width + size)
					location.x = -size;
				if (location_history.get(0).y > parent.height + size)
					location.y = -size;
			}
		} else {
			if (location.x < size)
				velocity.add(5, 0);
			if (location.y < size)
				velocity.add(0, 5);
			if (location.x > parent.width - size)
				velocity.add(-5, 0);
			if (location.y > parent.height - size)
				velocity.add(0, -5);
		}
	}

	// We accumulate a new acceleration each time based on three rules
	public void move(ArrayList<Boid_generic> boids) {
		PVector sep = separate(boids); // Separation
		PVector ali = align(boids); // Alignment
		PVector coh = cohesion(boids); // Cohesion
		// Arbitrarily weight these forces

		sep.mult((float) ai.getSep_weight());
		ali.mult((float) ai.getAli_weight());
		coh.mult((float) ai.getCoh_weight());
		//System.out.println(" here" + sep + " " + ali + " " + coh);
		// Add the force vectors to acceleration
		if(moveable) {
			acceleration.add(sep);
			acceleration.add(ali);
			acceleration.add(coh);
		}

	}

	public void update() {
		// Update velocity
		velocity.add(acceleration);
		// Limit speed
		velocity.limit(maxspeed);
		location.add(velocity);
		// Reset accelertion to 0 each cycle
		acceleration.mult(0);
	}

	// A method that calculates and applies a steering force towards a target
	// STEER = DESIRED MINUS VELOCITY
	PVector seek(PVector target) {
		PVector desired = PVector.sub(target, location); // A vector pointing from the location to the target
		// Scale to maximum speed
		desired.setMag(maxspeed);
		// Steering = Desired minus Velocity
		PVector steer = PVector.sub(desired, velocity);
		steer.limit(maxsteer); // Limit to maximum steering force
		return steer;
	}


	void render_perfect_future() {
		// abstract
	}

	public void render() {
		// abstract, handles by individuals
	}

	 synchronized void render_trails(int type,boolean simulation) { // all boids draw trails
		if(!simulation) {
			prev_vertex = new PVector(location.x, location.y);
			if (location_history.size() > 0) {
				switch (type) {
					default:
						break;
					case 1: // periodic dots
						int index = 0;
						parent.stroke(fillcol.getRGB());
						for (PVector vect : location_history) {
							prev_vertex = vect;
							index++;
							if ((index + parent.frameCount) % 5 == 0)
								parent.point(vect.x, vect.y);
						}
						break;// smooth curves
					case 2:
						parent.noFill();
						parent.beginShape();
					{
						for (PVector vect : location_history) {
							if (vect.dist(prev_vertex) > 200) {
								prev_vertex = new PVector(vect.x, vect.y);
								parent.endShape();
								return;
							} else {
								parent.stroke(fillcol.getRGB(), 75); // set colour and opacity;
								parent.vertex(vect.x, vect.y);
								prev_vertex = new PVector(vect.x, vect.y);
							}
						}
						parent.endShape();
					}
					break;
					case 3: // direct line
						if (location_history.size() > 0) {
							parent.noFill();
							parent.stroke(fillcol.getRGB(), 75); // set colour and opacity;
							if (location_history.get(0).dist(location_history.get(location_history.size() - 1)) < 200)
								parent.line(location_history.get(0).x, location_history.get(0).y,
										location_history.get(location_history.size() - 1).x,
										location_history.get(location_history.size() - 1).y);
							break;
						}
				}
			}
		}
	}

	// Separation - Method checks for nearby boids and steers away
	// Method checks for nearby boids and steers away
	PVector separate(ArrayList<Boid_generic> boids) {
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (Boid_generic other : boids) {
			float d = PVector.dist(location, other.location);
			// If the distance is greater than 0 and less than an arbitrary amount (0 when
			// you are yourself)
			if ((d > 0) && (d < ai.getSep_neighbourhood_size())) {
				isalone = false;
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(location, other.location);
				diff.normalize();
				diff.div(d); // Weight by distance
				steer.add(diff);
				count++; // Keep track of how many
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.div((float) count);
		}

		// As long as the vector is greater than 0
		if (steer.magSq() > 0) {
			steer.setMag(maxspeed);
			steer.sub(velocity);
			steer.limit(maxsteer);
		}
		return steer;
	}

	// Alignment - For every nearby boid in the system, calculate the average
	// velocity
	PVector align(ArrayList<Boid_generic> boids) {
		PVector sum = new PVector(0, 0);
		int count = 0;
		for (Boid_generic other : boids) {
			float d = PVector.dist(location, other.location);
			if ((d > 0) && (d < ai.getAli_neighbourhood_size())) {
				isalone = false;
				sum.add(other.velocity);
				count++;
			}
		}
		if (count > 0) {
			sum.div((float) count);
			sum.setMag(maxspeed);
			PVector steer = PVector.sub(sum, velocity);
			steer.limit(maxsteer);
			return steer;
		} else {
			return new PVector(0, 0);
		}
	}

	// Cohesion - For the average location (i.e. center) of all nearby boids,
	// calculate
	// steering vector towards that location
	PVector cohesion(ArrayList<Boid_generic> boids) {
		PVector sum = new PVector(0, 0); // Start with empty vector to accumulate all locations
		int count = 0;
		for (Boid_generic other : boids) {
			float d = PVector.dist(location, other.location);
			if ((d > 0) && (d < ai.getCoh_neighbourhood_size())) {
				isalone = false;
				sum.add(other.location); // Add location
				count++;
			}
		}
		if (count > 0) {
			sum.div(count);
			return seek(sum); // Steer towards the location
		} else {
			return new PVector(0, 0);
		}
	}

	void reload() {
		current_reload--;
		if (current_reload <= 0 && !canfire) {
			canfire = true;
			current_reload = reload_time;
		}
	}

	public void kill() {
		alive = false;
	}

	public double get_size() {
		return size;
	}

	public boolean get_alive() {
		return alive;
	}

	public Color get_colour() {
		return fillcol;
	}

	public PVector get_future_location() {
		return location;
	}

	public int observed_team() {
		return team;
	}

	public AI_type getAi() {
		return ai;
	}

	public void setAi(AI_type ai) {
		this.ai = ai;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public PVector getAcceleration() {
		return acceleration; // only accesses 'future'
	}

	public void setAcceleration(PVector acceleration) {
		this.acceleration = new PVector(acceleration.x, acceleration.y); // only accesses 'future'
	}

	public Color getFillcol() {
		return fillcol;
	}

	public Color getLinecol() {
		return linecol;
	}

	public PVector getVelocity_history() {
		if (velocity_history.size() > 0)
			return new PVector(velocity_history.get(0).x, velocity_history.get(0).y);
		else
			return velocity;
	}

	public Double getAngle_history() {
		if (angle_history.size() > 0)
			return angle_history.get(0);
		else
			return angle;
	}

	public PVector getLocation_history() {
		if (location_history.size() > 0)
			return new PVector(location_history.get(0).x, location_history.get(0).y);
		else
			return location;
	}

	public PVector getAcceleration_history() {
		if (acceleration_history.size() > 0)
			return new PVector(acceleration_history.get(0).x, acceleration_history.get(0).y);
		else
			return acceleration;
	}

	public ArrayList<PVector> getLocation_history_full() {
		return location_history;

	}

	public PApplet getParent() {
		return parent;
	}

	public PVector getLocation() {
		return location;
	}

	public PVector getVelocity() {
		return velocity;
	}

	public float getSize() {
		return size;
	}

	public float getmaxsteer() {
		return maxsteer;
	}

	public float getMaxspeed() {
		return maxspeed;
	}

	public double getAngle() {
		return angle;
	}

	public float getFireangle() {
		return fireangle;
	}

	public float getFirerange() {
		return firerange;
	}

	public int getReload_time() {
		return reload_time;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isCanfire() {
		return canfire;
	}

	public int getCurrent_reload() {
		return current_reload;
	}

	public void setVelocity(PVector velocity) {
		this.velocity = new PVector(velocity.x, velocity.y);
	}

	public void setLocation_history(ArrayList<PVector> location_history) {
		location_history.clear();
		this.location_history = location_history;
	}

	public void setHeading_history(ArrayList<Float> heading_history) {
		heading_history.clear();
		this.heading_history = heading_history;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public void setVelocity_history(ArrayList<PVector> velocity_history) {
		this.velocity_history = velocity_history;
	}

	public void setLocation(PVector location) {
		this.location = new PVector(location.x, location.y);
	}

	public ArrayList<Float> getHeading_history() {
		return heading_history;
	}

	public AI_internal_model getInternal_model() {
		// only used by standard boid, abstracted away
		return null;
	}

	public boolean Isalone() {
		return isalone;
	}

	public int getTeam() {
		return team;
	}
}