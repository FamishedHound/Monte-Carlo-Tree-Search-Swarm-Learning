package pack_boids;

import pack_1.Launcher;
import pack_technical.GameManager;
import processing.core.PConstants;
import processing.core.PVector;
import java.util.ArrayList;

public class BoidObserver extends BoidStandard {

    public BoidObserver(float x, float y, int t) {
        super(x, y, t,-1);
        velocity = new PVector(0, 0);
    }

    @Override
    protected void render() {
        Launcher.applet.fill(fillColour.getRGB());
        Launcher.applet.ellipse(location.x, location.y, size, size);
        Launcher.applet.textSize(10);
        Launcher.applet.textAlign(PConstants.CENTER);
        Launcher.applet.text("camera", location.x, location.y - 6);
    }

    @Override
    public void run(ArrayList<BoidGeneric> boids, boolean real_step, boolean simulation) {
        if (!simulation && real_step) {
            render();
            if(Launcher.getPredictState() != Launcher.PredictStates.NONE && (Launcher.getPredictState() == Launcher.PredictStates.ALL || GameManager.getSelected_boid() == this)) {
                attempt_future();
            }
        }
    }
}
