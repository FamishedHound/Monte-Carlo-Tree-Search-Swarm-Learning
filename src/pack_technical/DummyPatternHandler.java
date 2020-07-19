package pack_technical;

import pack_1.Constants;
import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class DummyPatternHandler implements PatternLearning {

    boolean once = false;

    @Override
    public boolean isOnce() {
        return once;
    }

    @Override
    public void setOnce(boolean once) {
        this.once = once;
    }

    @Override
    public List<PVector> getNewpoints() {
        return Constants.DEFENDER_BOID_WAYPOINTS;
    }

    @Override
    public void newObservation(ArrayList<BoidGeneric> boids, int counter) {

    }

    @Override
    public int analyze() {
        once = true;
        return 1;
    }
}
