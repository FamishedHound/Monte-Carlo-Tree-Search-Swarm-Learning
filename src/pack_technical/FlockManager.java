package pack_technical;

import pack_1.Launcher;
import pack_1.Utility;

import java.util.ArrayList;
import pack_AI.AI_internal_model;
import pack_boids.Boid_imaginary;
import pack_boids.Boid_observer;
import pack_boids.Boid_standard;
import pack_boids.Boid_generic;
import processing.core.PVector;

/*
 * A flock holds a list of real_boids, and performs operations concerning removing and adding real_boids
 * to the simulation in a tidy manner. can also perform flock-wide operations such as returning the
 * nearest boid.
 */
public class FlockManager {

    public ArrayList<Boid_generic> getReal_boids() {
        return real_boids;
    }

    ArrayList<Boid_generic> real_boids; // An ArrayList for all the real_boids
    ArrayList<Boid_generic> imaginary_boids; // An ArrayList for all the real_boids
    Boid_observer camera_boid;
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
                camera_boid = new Boid_observer((float) Launcher.applet.width / 2, (float) Launcher.applet.height / 2,
                    GameManager.getTeam_number() + 1);
            // camera is the final team
            real_boids = new ArrayList<Boid_generic>(); // Initialize the ArrayList
        } else {
            imaginary_boids = new ArrayList<Boid_generic>(); // Initialize the ArrayList
        }

    }

    public void run(int steps) {
        ArrayList<Boid_generic> boids = get_all_boids();

        for (Boid_generic b : boids) {
            if (!b.isAlive()) {
                remove_boid(b);
                break;
            }
        }

        for (int step = 0; step < steps; step++) {
            // first run camera that does not interfere with the flock
            if (camera_boid != null)
                camera_boid.run(null, (step == 0), simulation);
            for (Boid_generic b : boids) {
                if (b instanceof Boid_standard) { // if real
                    b.run(boids, (step == 0), simulation);
                } else { // if imaginary
                    b.run(boids, (step == steps - 1), simulation);
                }
            }
        }
    }

    public ArrayList<Boid_generic> get_all_boids() {
        return (real ? real_boids : imaginary_boids);
    }

    public int get_boid_count() {
        return get_all_boids().size();
    }

    public Boid_generic remove_boid(Boid_generic b) {
        get_all_boids().remove(b);
        return b;
    }

    public Boid_generic add_boid(Boid_generic b) {
        // give the boid the ai derived from the team
        get_all_boids().add(b);
        return b;
    }

    public void reset() {
        get_all_boids().clear();
    }

    // used in creating an imaginary universe in which the real_boids are not real
    public void import_imaginary_boids(ArrayList<Boid_generic> boids_in, AI_internal_model internal_model) {
        for (Boid_generic b : boids_in) {
            Boid_imaginary b2 = new Boid_imaginary(b.getLocationHistory().x, b.getLocationHistory().y, b.getTeam(), b);
            b2.setVelocity(b.getVelocityHistory());
            b2.setAcceleration(b.getAccelerationHistory());
            b2.setAi(internal_model.get_ai_team(b.getTeam()));
            b2.setAngle(b.getAngleHistory());
            b2.setTeam(b.getTeam());
            add_boid(b2);
        }
    }

    public Boid_standard get_nearest_boid(float selectDist) {
        PVector mousePos = new PVector(Launcher.applet.mouseX, Launcher.applet.mouseY);
        Boid_standard nearestBoid = null;
        float selectDistSq = selectDist * selectDist;
        float distRecordSq = Float.MAX_VALUE;
        // attempt select camera first, this is not part of the flock
        float distSq = Utility.distSq(mousePos, camera_boid.getLocation());
        if ((distSq < distRecordSq) && (distSq < selectDistSq)) {
            nearestBoid = camera_boid;
            distRecordSq = distSq;
        }
        for (Boid_generic b : real_boids) {
            if(!(b instanceof Boid_standard)) continue; // Only consider if it is a real boid
            distSq = Utility.distSq(mousePos, b.getLocationHistory());
            if ((distSq < distRecordSq) && (distSq < selectDistSq)) {
                nearestBoid = (Boid_standard)b;
                distRecordSq = distSq;
            }
        }
        return nearestBoid;
    }

    public Boid_observer getCamera_boid() {
        return camera_boid;
    }

    public void setCamera_boid(Boid_observer camera_boid) {
        this.camera_boid = camera_boid;
    }

}
