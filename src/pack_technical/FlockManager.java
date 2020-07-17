package pack_technical;

import pack_1.Launcher;
import pack_1.Utility;

import java.util.ArrayList;
import java.util.List;

import pack_AI.AI_internal_model;
import pack_boids.*;
import processing.core.PVector;

/*
 * A flockManager holds a list of real_boids, and performs operations concerning removing and adding real_boids
 * to the simulation in a tidy manner. can also perform flockManager-wide operations such as returning the
 * nearest boid.
 */
public class FlockManager {

    public List<BoidGeneric> getReal_boids() {
        return real_boids;
    }

    List<BoidGeneric> real_boids; // An ArrayList for all the real_boids
    List<BoidGeneric> imaginary_boids; // An ArrayList for all the real_boids
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
            real_boids = new ArrayList<BoidGeneric>(); // Initialize the ArrayList
        } else {
            imaginary_boids = new ArrayList<BoidGeneric>(); // Initialize the ArrayList
        }

    }

    public void run(int steps) {
        List<BoidGeneric> boids = getAllBoids();

        for (BoidGeneric b : boids) {
            if (!b.isAlive()) {
                removeBoid(b);
                break;
            }
        }

        for (int step = 0; step < steps; step++) {
            for (BoidGeneric b : boids) {
                if (b instanceof BoidStandard) { // if real
                    b.run(boids, (step == 0), simulation);
                } else { // if imaginary
                    b.run(boids, (step == steps - 1), simulation);
                }
            }
        }
    }

    public List<BoidGeneric> getAllBoids() {
        return (real ? real_boids : imaginary_boids);
    }

    public int getBoidCount() {
        return getAllBoids().size();
    }

    public BoidGeneric removeBoid(BoidGeneric b) {
        getAllBoids().remove(b);
        return b;
    }

    public BoidGeneric addBoid(BoidGeneric b) {
        // give the boid the ai derived from the team
        getAllBoids().add(b);
        return b;
    }

    public void reset() {
        getAllBoids().clear();
    }

    // used in creating an imaginary universe in which the real_boids are not real
    public void importImaginaryBoids(List<BoidGeneric> boids_in, AI_internal_model internal_model) {
        for (BoidGeneric b : boids_in) {
            BoidImaginary b2 = new BoidImaginary(b.getLocationHistory().x, b.getLocationHistory().y, b.getTeam(), b);
            b2.setVelocity(b.getVelocityHistory());
            b2.setAcceleration(b.getAccelerationHistory());
            b2.setAi(internal_model.get_ai_team(b.getTeam()));
            b2.setAngle(b.getAngleHistory());
            b2.setTeam(b.getTeam());
            addBoid(b2);
        }
    }

    public BoidStandard getNearestBoid(float selectDist) {
        PVector mousePos = new PVector(Launcher.applet.mouseX, Launcher.applet.mouseY);
        BoidStandard nearestBoid = null;
        float selectDistSq = selectDist * selectDist;
        float distRecordSq = Float.MAX_VALUE;
        for (BoidGeneric b : real_boids) {
            if(!(b instanceof BoidStandard)) continue; // Only consider if it is a real boid
            float distSq = Utility.distSq(mousePos, b.getLocationHistory());
            if ((distSq < distRecordSq) && (distSq < selectDistSq)) {
                nearestBoid = (BoidStandard)b;
                distRecordSq = distSq;
            }
        }
        return nearestBoid;
    }

}
