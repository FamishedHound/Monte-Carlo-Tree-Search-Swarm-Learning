package pack_technical;

import pack_1.Launcher;
import pack_1.Utility;

import java.util.ArrayList;
import java.util.List;

import pack_AI.AI_internal_model;
import pack_boids.*;
import processing.core.PConstants;
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
    boolean simulation;

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

        for (BoidGeneric boid : boids) {
            if (!boid.isAlive()) {
                removeBoid(boid);
                break;
            }
        }
        //block below currently never gets ran for the "mind_flock" stored in a BoidGeneric
        for (int step = 0; step < steps; step++) {
            for (BoidGeneric boid : boids) {
                if (boid instanceof BoidStandard) { // if real
                    if(boid.getTeam() != 1) {
                        boid.run(boids, simulation);
                    }
                    if (step == 0) {
                        if (Launcher.areTrailsDrawn()) {
                            renderTrails(boid, TrailType.CURVE);
                        }
                        renderBoid(boid);
                    }
                } else { // if imaginary
                    boid.run(boids, simulation);
                    if(!simulation && step == steps -1) {
                        renderTrails(boid, TrailType.DOTS);
                        renderBoid(boid);
                    }
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
    public void importImaginaryBoids(List<BoidGeneric> boidsToImport, AI_internal_model internal_model) {
        for (BoidGeneric boidToImport : boidsToImport) {
            BoidImaginary boidToExport = new BoidImaginary(boidToImport.getOldestLocationInHistory().x, boidToImport.getOldestLocationInHistory().y, boidToImport.getTeam(), boidToImport);
            boidToExport.setVelocity(boidToImport.getOldestVelocityInHistory());
            boidToExport.setAcceleration(boidToImport.getAccelerationHistory());
            boidToExport.setAi(internal_model.get_ai_team(boidToImport.getTeam()));
            boidToExport.setAngle(boidToImport.getOldestAngleInHistory());
            boidToExport.setTeam(boidToImport.getTeam());
            addBoid(boidToExport);
        }
    }

    public BoidStandard getNearestBoid(float selectDist) {
        PVector mousePos = new PVector(Launcher.applet.mouseX, Launcher.applet.mouseY);
        BoidStandard nearestBoid = null;
        float selectDistSq = selectDist * selectDist;
        float distRecordSq = Float.MAX_VALUE;
        for (BoidGeneric b : real_boids) {
            if(!(b instanceof BoidStandard)) {
                continue; // Only consider if it is a real boid
            }
            float distSq = Utility.distSq(mousePos, b.getOldestLocationInHistory());
            if ((distSq < distRecordSq) && (distSq < selectDistSq)) {
                nearestBoid = (BoidStandard)b;
                distRecordSq = distSq;
            }
        }
        return nearestBoid;
    }

    void renderBoid(BoidGeneric boid) {
        Launcher.applet.fill(boid.getFillColour().getRGB());
        Launcher.applet.stroke(boid.getLineColour().getRGB(), 180);
        Launcher.applet.pushMatrix();
        Launcher.applet.translate(boid.getLocation().x, boid.getLocation().y);
        Launcher.applet.rotate(boid.getVelocity().heading());
        Launcher.applet.beginShape(PConstants.TRIANGLES);
        Launcher.applet.vertex(boid.getSize(), 0);
        Launcher.applet.vertex(-boid.getSize(), boid.getSize() / 2);
        Launcher.applet.vertex(-boid.getSize(), -boid.getSize() / 2);
        Launcher.applet.endShape();
        Launcher.applet.popMatrix();
    }

    /** The type of boid trail to draw */
    protected enum TrailType {
        /** Periodic dots */DOTS,
        /** Smooth curve */CURVE,
        /** Straight line */STRAIGHT
    }
    /**
     * Draw the trail of past location of the boid
     * @param type Type of trail to draw
     */
    void renderTrails(BoidGeneric boid, TrailType type) {
        if (boid.getLocationHistory().size() > 0) {
            switch (type) {
                case DOTS:
                    int index = 0;
                    Launcher.applet.stroke(boid.getFillColour().getRGB());
                    for (PVector vect : boid.getLocationHistory()) {
                        index++;
                        if ((index + Launcher.applet.frameCount) % 5 == 0)
                            Launcher.applet.point(vect.x, vect.y);
                    }
                    break;
                case CURVE:
                    Launcher.applet.noFill();
                    Launcher.applet.beginShape();
                    for (PVector vect : boid.getLocationHistory()) {
                        Launcher.applet.stroke(boid.getFillColour().getRGB(), 75); // set colour and opacity;
                        Launcher.applet.vertex(vect.x, vect.y);
                    }
                    Launcher.applet.endShape();
                    break;
                case STRAIGHT:
                    if (boid.getLocationHistory().size() > 0) {
                        Launcher.applet.noFill();
                        Launcher.applet.stroke(boid.getFillColour().getRGB(), 75); // set colour and opacity;
                        if (Utility.distSq(boid.getLocationHistory().get(0), boid.getLocationHistory().get(boid.getLocationHistory().size() - 1)) < 200 * 200)
                            Launcher.applet.line(boid.getLocationHistory().get(0).x, boid.getLocationHistory().get(0).y,
                                    boid.getLocationHistory().get(boid.getLocationHistory().size() - 1).x,
                                    boid.getLocationHistory().get(boid.getLocationHistory().size() - 1).y);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
