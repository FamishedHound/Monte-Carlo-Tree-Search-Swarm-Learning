package pack_boids;

import java.util.ArrayList;
import pack_1.Launcher;
import pack_1.Launcher.predictStates;
import pack_AI.AI_internal_model;
import pack_AI.AI_machine_learner;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_technical.FlockManager;
import pack_technical.GameManager;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

// there is only one type of boid at the moment, this one can shoot(?) and predict
public class Boid_standard extends Boid_generic {

	// machine learning apparatus
	FlockManager mind_flock = new FlockManager(parent, false); // for inhead simulation
	AI_internal_model internal_model;
	AI_machine_learner machine_learner;

	public Boid_standard(PApplet p, float x, float y, int t,int id) {
		super(p, x, y, t,id);
		ai = AI_manager.get_team_ai(t);
		angle = parent.random(360); // degrees
		velocity = PVector.random2D().mult(2);
		internal_model = new AI_internal_model(false, this); // perfect model;
		machine_learner = new AI_machine_learner(parent, this); // perfect model;
	}

	// Method to update location
	protected void attempt_future() {
		if (Launcher.getFlock().get_boid_count() > 0) {
			mind_flock.import_imaginary_boids(Launcher.getFlock().get_all_boids(), internal_model);
			mind_flock.run(Launcher.getHISTORYLENGTH());
			machine_learner.run(mind_flock);
			mind_flock.reset();
		}
	}

	public void run(ArrayList<Boid_generic> boids, boolean real_step,boolean simulation) {
		if (!Launcher.isSim_paused()) {
			isalone = true; // is boid uninteracted with?
			move(boids); // sets isalone
			if(!simulation) {
				record_history();
				record_acceleration();
			}
			update();
		}
		if (!simulation) {
		move_borders(false);
		if (Launcher.isSim_drawtrails() && real_step) {
			render_trails(2,simulation);
			render_perfect_future();

		}

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
			reload();
		}
	}

	public AI_internal_model getInternal_model() {
		return internal_model;
	}

	void render_perfect_future() {
		parent.fill(fillcol.getRGB());
		//parent.noFill();
		parent.stroke(linecol.getRGB(), 180);
		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(velocity.heading());
		parent.beginShape(PConstants.TRIANGLES);
		parent.vertex(size, 0);
		parent.vertex(-size, size / 2);
		parent.vertex(-size, -size / 2);
		parent.endShape();
		parent.popMatrix();
	}

	public void render() {
//		if ((location_history.size() > 0) && (heading_history.size() > 0)) {
//			parent.fill(fillcol.getRGB());
//			parent.stroke(linecol.getRGB());
//			parent.pushMatrix();
//			parent.translate(location_history.get(0).x, location_history.get(0).y);
//			parent.rotate(heading_history.get(0));
//			parent.beginShape(PConstants.TRIANGLES);
//			parent.vertex(size, 0);
//			parent.vertex(-size, size / 2);
//			parent.vertex(-size, -size / 2);
//			parent.endShape();
//			parent.popMatrix();
//		}
	}
	public void setAi(AI_type ai) {
		this.ai = ai;
	}
}