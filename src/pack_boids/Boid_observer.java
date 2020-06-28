package pack_boids;

import pack_1.Launcher;
import pack_technical.GameManager;
import processing.core.PConstants;
import processing.core.PVector;

public class Boid_observer extends Boid_standard {

    public Boid_observer(float x, float y, int t) {
        super(x, y, t,-1);
        velocity = new PVector(0, 0);
    }

    public void render() {
        Launcher.applet.ellipse(location.x, location.y, size, size);
        Launcher.applet.textSize(10);
        Launcher.applet.textAlign(PConstants.CENTER);
        Launcher.applet.text("camera", location.x, location.y - 6);
    }

    public void run(boolean real_step,boolean simulation) {
        if (!simulation && real_step) {
            render();
            if(Launcher.getPredict_state() != Launcher.predictStates.NONE && (Launcher.getPredict_state() == Launcher.predictStates.ALL || GameManager.getSelected_boid() == this)) {
                attempt_future();
            }
        }
    }
}
