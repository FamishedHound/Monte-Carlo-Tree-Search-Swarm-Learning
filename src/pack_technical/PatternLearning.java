package pack_technical;

import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface used to interact with an object which learns waypoints.
 * Not complete in any way, right now just used to be able to pass waypoints
 * to ParameterSimulation and EnvironmentalSimulation in ZoneDefence
 * without modifcation of existing code
 *
 * to be renamed at a later date
 */
public interface PatternLearning {


    boolean isOnce();
    void setOnce(boolean once);
    List<PVector> getNewpoints();
    void newObservation(ArrayList<BoidGeneric> boids, int counter);
    int analyze();
}
