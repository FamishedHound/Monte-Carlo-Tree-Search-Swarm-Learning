package pack_boids;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import pack_1.Constants;
import pack_1.Launcher;
import pack_1.Utility;
import pack_AI.AI_type;
import pack_technical.GameManager;

import processing.core.PVector;

// the generic boid class holds functions common to all boid types
public abstract class BoidGeneric {

    /** Unique ID of the boid */
    private final int id;

    /** Colour to fill the boid, and draw the trails */
    protected final Color fillColour;
    /** Colour to draw the outline of boid */
    protected final Color lineColour;
    /** Size of boid, determines the size to draw and the collision radius */
    protected final float size;

    /** Current location of the boid */
    protected PVector location;
    /** History of the locations of the boid */
    protected ArrayList<PVector> locationHistory = new ArrayList<PVector>();

    /** Current velocity of the boid */
    protected PVector velocity = new PVector(0, 0);
    /** History of the velocities of the boid */
    protected ArrayList<PVector> velocityHistory = new ArrayList<PVector>();

    /** Current acceleration of the boid */
    protected PVector acceleration = new PVector(0, 0);
    /** History of the accelerations of the boid */
    protected ArrayList<PVector> accelerationHistory = new ArrayList<PVector>();

    /** Current direction the boid is pointing in, in degrees */
    protected double angle;
    /** History of the directions the boid has been pointing in */
    protected ArrayList<Double> angleHistory = new ArrayList<Double>();

    /** The team that the boid is on */
    protected int team;
    /** Whether the boid is currently alive */
    protected boolean alive = true;
    /** Whether the boid is uninteracted with */
    protected boolean isAlone = true;
    /** Whether the boid can move */
    protected boolean moveable = true;
    /** Whether the boid has failed, i.e. if the attack boid has collided with a defender boid */
    protected boolean hasFailed = false;

    /** AI type for various parameters specifying the movement of the boid */
    protected AI_type ai;

    // if real_step is true all normal actions will happen, otherwise
    // rendering is disabled, this is for 'in-mind simulation'
    public abstract void run(List<BoidGeneric> boids, boolean real_step, boolean simulation);
    /** Render the boid */
    protected abstract void render();

    /**
     * Create a new boid
     * @param x The initial x position of the boid
     * @param y The initial y position of the boid
     * @param team The team the boid belongs to
     * @param id The unique id of the boid
     */
    protected BoidGeneric(float x, float y, int team, int id) {
        this.id = id;
        this.team = team;
        fillColour = GameManager.get_team_colour(team);
        lineColour = new Color(245, 245, 245);
        location = new PVector(x, y);
        size = Constants.Boids.SIZE;
	}

    /**
     * Make a copy of the given boid
     * @param boid The boid to copy
     */
	public BoidGeneric(BoidGeneric boid) {
		this.id = boid.getId();
		this.team = boid.getTeam();
		this.fillColour = boid.getFillColour();
		this.lineColour = boid.getLineColour();
		this.size = boid.getSize();
		this.location = boid.location.copy();
		this.velocity = boid.velocity.copy();
		this.acceleration = boid.acceleration.copy();
	}

    /**
     * Adds the current acceleration of the boid to the history
     */
	public void recordAcceleration() {
		accelerationHistory.add(this.acceleration.copy());
		if (accelerationHistory.size() > Launcher.HISTORY_LENGTH) {
			accelerationHistory.remove(0);
		}
	}

    /**
     * Adds the current velocity, location, and angle of the boid to the history.
     * This is done separately to recordAccleration
     */
	public void recordHistory() {
		velocityHistory.add(this.velocity.copy());
		locationHistory.add(this.location.copy());
		angleHistory.add(angle);
		if (locationHistory.size() > Launcher.HISTORY_LENGTH) {
			velocityHistory.remove(0);
			locationHistory.remove(0);
			angleHistory.remove(0);
		}
	}

    /**
     * Update the velocity and location based on the current acceleration
     */
	public void update() {
		// Update velocity
		velocity.add(acceleration);
		// Limit speed
		velocity.limit(Constants.Boids.MAX_SPEED);
		location.add(velocity);
        // Reset acceleration to 0 each cycle
		acceleration.mult(0);
    }

    /**
     * Update the velocity and location based on a new acceleration
     * @param acceleration The new acceleration to apply to the boid
     */
    public void update(PVector acceleration) {
        this.setAcceleration(acceleration);
        this.update();
        velocity.limit(Constants.Boids.MAX_SPEED_ATTACK);
    }

    /**
     * Update the acceleration of the boid based on three rules
     * <ul>
     *  <li>Separation: Moves away from any nearby boids</li>
     *  <li>Alignment: </li>
     *  <li>Cohesion: </li>
     * </ul>
     * The weighting of each of these forces is dependent upon the parameters in
     * the {@link pack_AI.AI_type}
     * @param boids The flock of boids to update acceleration based upon
     * @see BoidGeneric#separate
     * @see BoidGeneric#align
     * @see BoidGeneric#cohesion
     */
    public void move(Iterable<BoidGeneric> boids) {
        // Compute the three forces
        PVector separation = separate(boids);
        PVector alignment = align(boids);
        PVector cohesion = cohesion(boids);

        // Arbitrarily weight these forces
        separation.mult((float) ai.getSep_weight());
        alignment.mult((float) ai.getAli_weight());
        cohesion.mult((float) ai.getCoh_weight());

        // Add the force vectors to acceleration
        if(moveable) {
            acceleration.add(separation);
            acceleration.add(alignment);
            acceleration.add(cohesion);
		}
	}

    /**
     * Checks for nearby boids and steers away
     * @param boids The collection of boids to move away from
     * @return A force to move the boid away neighbouring boids
     */
    private PVector separate(Iterable<BoidGeneric> boids) {
        PVector steer = new PVector(0, 0, 0);
        int count = 0;
        // For every boid in the system, check if it's too close
        for (BoidGeneric other : boids) {
            float d = Utility.distSq(location, other.location);
            // If the distance is greater than 0 and less than an arbitrary amount (0 when
            // you are yourself)
            if ((d > 0) && (d < ai.getSep_neighbourhood_size() * ai.getSep_neighbourhood_size())) {
                isAlone = false;
                // Calculate vector pointing away from neighbor
                PVector diff = PVector.sub(location, other.location);
                diff.normalize();
                diff.div((float)Math.sqrt(d)); // Weight by distance
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
            steer.setMag(Constants.Boids.MAX_SPEED);
            steer.sub(velocity);
            steer.limit(Constants.Boids.MAX_STEER);
        }
        return steer;
    }

    /**
     * Calculates the average velocity of neighbouring boids,
     * and gives a force to move this boids velocity towards that average velocity
     * @param boids The collection of boids to match the velocity of
     * @return A force to match the average velocity of neighbourin boids
     */
    private PVector align(Iterable<BoidGeneric> boids) {
        PVector sum = new PVector(0, 0);
        int count = 0;
        for (BoidGeneric other : boids) {
            float d = Utility.distSq(location, other.location);
            if ((d > 0) && (d < ai.getAli_neighbourhood_size() * ai.getAli_neighbourhood_size())) {
                isAlone = false;
                sum.add(other.velocity);
                count++;
            }
        }
        if (count > 0) {
            sum.div((float) count);
            sum.setMag(Constants.Boids.MAX_SPEED);
            PVector steer = PVector.sub(sum, velocity);
            steer.limit(Constants.Boids.MAX_STEER);
            return steer;
        } else {
            return new PVector(0, 0);
        }
    }

    /**
     * Calculates the center of the neighbouring boids, and produces a force to move this boid towards it
     * @param boids The collection of boids to move to the center of
     * @return A force to move towards the center of the neighbouring boids
     */
    private PVector cohesion(Iterable<BoidGeneric> boids) {
        PVector sum = new PVector(0, 0); // Start with empty vector to accumulate all locations
        int count = 0;
        for (BoidGeneric other : boids) {
            float d = Utility.distSq(location, other.location);
            if ((d > 0) && (d < ai.getCoh_neighbourhood_size() * ai.getCoh_neighbourhood_size())) {
                isAlone = false;
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

    /**
     * Calculates a steering force towards a target location
     * @param target The location to move towards
     * @return The steering force towards the target locaiton
     */
	private PVector seek(PVector target) {
		PVector desired = PVector.sub(target, location); // A vector pointing from the location to the target
		// Scale to maximum speed
		desired.setMag(Constants.Boids.MAX_SPEED);
		// Steering = Desired minus Velocity
		PVector steer = PVector.sub(desired, velocity);
		steer.limit(Constants.Boids.MAX_STEER); // Limit to maximum steering force
		return steer;
	}

    /** The type of boid trail to draw */
    protected static enum TrailType {
        /** Periodic dots */DOTS,
        /** Smooth curve */CURVE,
        /** Straight line */STRAIGHT
    }
    /**
     * Draw the trail of past location of the boid
     * @param type Type of trail to draw
     */
    void renderTrails(TrailType type) {
        if (locationHistory.size() > 0) {
            switch (type) {
                case DOTS:
                    int index = 0;
                    Launcher.applet.stroke(fillColour.getRGB());
                    for (PVector vect : locationHistory) {
                        index++;
                        if ((index + Launcher.applet.frameCount) % 5 == 0)
                            Launcher.applet.point(vect.x, vect.y);
                    }
                    break;
                case CURVE:
                    Launcher.applet.noFill();
                    Launcher.applet.beginShape();
                    for (PVector vect : locationHistory) {
                        Launcher.applet.stroke(fillColour.getRGB(), 75); // set colour and opacity;
                        Launcher.applet.vertex(vect.x, vect.y);
                    }
                    Launcher.applet.endShape();
                    break;
                case STRAIGHT:
                    if (locationHistory.size() > 0) {
                        Launcher.applet.noFill();
                        Launcher.applet.stroke(fillColour.getRGB(), 75); // set colour and opacity;
                        if (Utility.distSq(locationHistory.get(0), locationHistory.get(locationHistory.size() - 1)) < 200 * 200)
                            Launcher.applet.line(locationHistory.get(0).x, locationHistory.get(0).y,
                                locationHistory.get(locationHistory.size() - 1).x,
                                locationHistory.get(locationHistory.size() - 1).y);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // Getters and setters

    public int getId() {
		return id;
	}

    public float getSize() {
		return size;
	}

	public Color getFillColour() {
		return fillColour;
    }
    public Color getLineColour() {
		return lineColour;
	}

    public AI_type getAi() {
        return ai;
    }
	public void setAi(AI_type ai) {
		this.ai = ai;
	}

    public PVector getAccelerationHistory() {
        if (accelerationHistory.size() > 0)
            return accelerationHistory.get(0).copy();
		else
			return acceleration;
	}
	public PVector getAcceleration() {
		return acceleration;
	}
	public void setAcceleration(PVector acceleration) {
		this.acceleration = acceleration.copy();
	}

    public PVector getLocationHistory() {
        if (locationHistory.size() > 0)
            return locationHistory.get(0).copy();
		else
			return location;
	}
	public PVector getLocation() {
		return location;
    }
    public void setLocation(PVector location) {
		this.location = location.copy();
	}

    public PVector getVelocityHistory() {
		if (velocityHistory.size() > 0)
			return new PVector(velocityHistory.get(0).x, velocityHistory.get(0).y);
		else
			return velocity;
	}
	public PVector getVelocity() {
		return velocity;
    }
    public void setVelocity(PVector velocity) {
		this.velocity = velocity.copy();
	}

    public double getAngleHistory() {
		if (angleHistory.size() > 0)
			return angleHistory.get(0);
		else
			return angle;
	}
    public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}

	public void setStationary() {
		this.setLocation(this.getLocation());
		this.velocity.mult(0);
	}

	public boolean isAlone() {
		return isAlone;
	}

	public int getTeam() {
		return team;
    }
    public void setTeam(int team) {
		this.team = team;
	}

    public boolean hasFailed() {
		return hasFailed;
	}
	public void setHasFailed(boolean hasFailed) {
		this.hasFailed = hasFailed;
	}

    public boolean isMoveable() {
        return moveable;
    }
    public void setMovable(boolean moveable){
		this.moveable = moveable;
    }

	public boolean isAlive() {
		return alive;
    }
    public void setAlive(boolean alive) {
		this.alive = alive;
    }
}