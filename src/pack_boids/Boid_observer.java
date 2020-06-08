package pack_boids;

import pack_1.Launcher;
import pack_1.Launcher.predictStates;
import pack_technical.GameManager;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Boid_observer extends Boid_standard {

	public Boid_observer(PApplet p, float x, float y, int t) {
		super(p, x, y, t,-1);
		velocity = new PVector(0, 0);
	}

	public void render() {
		parent.ellipse(location.x, location.y, size, size);
		parent.textSize(10);
		parent.textAlign(PConstants.CENTER);
		parent.text("camera", location.x, location.y - 6);
	}

	public void run(boolean real_step,boolean simulation) {
		move_borders(false);
		if(!simulation){
		if (real_step)
			render();
		if (Launcher.getPredict_state() != predictStates.NONE) {
			if (Launcher.getPredict_state() == predictStates.ALL) {
				if (real_step)
					attempt_future();
			} else if (GameManager.getSelected_boid() == this)
				if (real_step)
					attempt_future();
		} // note AI learning happens in real time, and is not accelerated with simspeed
	}
}
	}
