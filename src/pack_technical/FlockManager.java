package pack_technical;

import pack_1.Launcher;
import pack_1.Utility;

import java.util.ArrayList;
import pack_AI.AI_internal_model;
import pack_boids.*;
import processing.core.PVector;

/*
 * A flock holds a list of real_boids, and performs operations concerning removing and adding real_boids
 * to the simulation in a tidy manner. can also perform flock-wide operations such as returning the
 * nearest boid.
 */
public class FlockManager {

    public ArrayList<BoidGeneric> getReal_boids() {
        return real_boids;
    }

    ArrayList<BoidGeneric> real_boids; // An ArrayList for all the real_boids
    ArrayList<BoidGeneric> imaginary_boids; // An ArrayList for all the real_boids
    BoidObserver camera_boid;
    boolean real;
    boolean simulation=false;

    public FlockManager(boolean real) {
        this(real, false);
    }

    public FlockManager(boolean real,boolean simulation) {
        this.real = real;
        this.simulation=simulation;
        if (real) {
            if(!simulation)
                camera_boid = new BoidObserver((float) Launcher.applet.width / 2, (float) Launcher.applet.height / 2,
                    GameManager.getTeam_number() + 1);
            // camera is the final team
            real_boids = new ArrayList<BoidGeneric>(); // Initialize the ArrayList
        } else {
            imaginary_boids = new ArrayList<BoidGeneric>(); // Initialize the ArrayList
        }

    }

    public void run(int steps) {
        ArrayList<BoidGeneric> boids = get_all_boids();

        for (BoidGeneric b : boids) {
            if (!b.isAlive()) {
                remove_boid(b);
                break;
            }
        }

        for (int step = 0; step < steps; step++) {
            // first run camera that does not interfere with the flock
            if (camera_boid != null)
                camera_boid.run(null, (step == 0), simulation);
            for (BoidGeneric b : boids) {
                if (b instanceof BoidStandard) { // if real
                    b.run(boids, (step == 0), simulation);
                } else { // if imaginary
                    b.run(boids, (step == steps - 1), simulation);
                }
            }
        }
    }

    public ArrayList<BoidGeneric> get_all_boids() {
        return (real ? real_boids : imaginary_boids);
    }

    public int get_boid_count() {
        return get_all_boids().size();
    }

    public BoidGeneric remove_boid(BoidGeneric b) {
        get_all_boids().remove(b);
        return b;
    }

    public BoidGeneric add_boid(BoidGeneric b) {
        // give the boid the ai derived from the team
        get_all_boids().add(b);
        return b;
    }

    public void reset() {
        get_all_boids().clear();
    }

    // used in creating an imaginary universe in which the real_boids are not real
    public void import_imaginary_boids(ArrayList<BoidGeneric> boids_in, AI_internal_model internal_model) {
        for (BoidGeneric b : boids_in) {
            BoidImaginary b2 = new BoidImaginary(b.getLocationHistory().x, b.getLocationHistory().y, b.getTeam(), b);
            b2.setVelocity(b.getVelocityHistory());
            b2.setAcceleration(b.getAccelerationHistory());
            b2.setAi(internal_model.get_ai_team(b.getTeam()));
            b2.setAngle(b.getAngleHistory());
            b2.setTeam(b.getTeam());
            add_boid(b2);
        }
    }

    public BoidStandard get_nearest_boid(float selectDist) {
        PVector mousePos = new PVector(Launcher.applet.mouseX, Launcher.applet.mouseY);
        BoidStandard nearestBoid = null;
        float selectDistSq = selectDist * selectDist;
        float distRecordSq = Float.MAX_VALUE;
        // attempt select camera first, this is not part of the flock
        float distSq = Utility.distSq(mousePos, camera_boid.getLocation());
        if ((distSq < distRecordSq) && (distSq < selectDistSq)) {
            nearestBoid = camera_boid;
            distRecordSq = distSq;
        }
        for (BoidGeneric b : real_boids) {
            if(!(b instanceof BoidStandard)) continue; // Only consider if it is a real boid
            distSq = Utility.distSq(mousePos, b.getLocationHistory());
            if ((distSq < distRecordSq) && (distSq < selectDistSq)) {
                nearestBoid = (BoidStandard)b;
                distRecordSq = distSq;
            }
        }
        return nearestBoid;
    }

    public BoidObserver getCamera_boid() {
        return camera_boid;
    }

    public void setCamera_boid(BoidObserver camera_boid) {
        this.camera_boid = camera_boid;
    }

}
