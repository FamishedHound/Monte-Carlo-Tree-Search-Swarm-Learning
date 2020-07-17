package pack_boids;

import pack_1.Launcher;
import pack_technical.GameManager;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.List;

public class BoidObserver extends BoidStandard {

    public BoidObserver(float x, float y, int t) {
        super(x, y, t,-1);
        velocity = new PVector(0, 0);
    }



    @Override
    public void run(List<BoidGeneric> boids, boolean simulation) {
        if (!simulation) {
            if(Launcher.getPredictState() != Launcher.PredictStates.NONE && (Launcher.getPredictState() == Launcher.PredictStates.ALL || GameManager.getSelected_boid() == this)) {
                attempt_future();
            }
        }
    }
}
