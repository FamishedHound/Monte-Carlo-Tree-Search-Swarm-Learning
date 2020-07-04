package pack_boids;

import java.util.ArrayList;
import java.util.Random;
import pack_1.Launcher;
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
	FlockManager mind_flock = new FlockManager(false); // for inhead simulation
	AI_internal_model internal_model;
	AI_machine_learner machine_learner;

    public Boid_standard(float x, float y, int t,int id) {
        super(x, y, t,id);
        ai = AI_manager.get_team_ai(t);
        angle = new Random().nextInt(360); // degrees
        velocity = PVector.random2D().mult(2);
        internal_model = new AI_internal_model(false, this); // perfect model;
        machine_learner = new AI_machine_learner(this); // perfect model;
    }

	public Boid_standard(PApplet p, Boid_generic boid_generic) {
		super(boid_generic);
		this.ai = AI_manager.get_team_ai(boid_generic.getTeam());
		this.angle = boid_generic.getAngle(); // degrees
		this.internal_model = new AI_internal_model(false, this); // perfect model;
		this.machine_learner = new AI_machine_learner(this); // perfect model;
	}

    @Override
    public void run(ArrayList<Boid_generic> boids, boolean real_step, boolean simulation) {
        if (!Launcher.isPaused()) {
            isAlone = true; // is boid uninteracted with?
            move(boids); // sets isalone
            if(!simulation) {
                recordHistory();
                recordAcceleration();
            }
            update();
        }
        if (!simulation) {
            if(real_step) {
                render();
                if (Launcher.getPredictState() != Launcher.PredictStates.NONE && (Launcher.getPredictState() == Launcher.PredictStates.ALL || GameManager.getSelected_boid() == this)) {
                    attempt_future();
                }
            }
        }
    }

    @Override
    protected void render() {
        if (Launcher.areTrailsDrawn()) {
            renderTrails(TrailType.CURVE);
        }
        render_perfect_future();
	}

	// Method to update location
	protected void attempt_future() {
		if (Launcher.getFlock().get_boid_count() > 0) {
			mind_flock.import_imaginary_boids(Launcher.getFlock().get_all_boids(), internal_model);
			mind_flock.run(Launcher.HISTORY_LENGTH);
			machine_learner.run(mind_flock);
			mind_flock.reset();
		}
	}

	public AI_internal_model getInternal_model() {
		return internal_model;
	}

	void render_perfect_future() {
		Launcher.applet.fill(fillColour.getRGB());
		Launcher.applet.stroke(lineColour.getRGB(), 180);
		Launcher.applet.pushMatrix();
		Launcher.applet.translate(location.x, location.y);
		Launcher.applet.rotate(velocity.heading());
		Launcher.applet.beginShape(PConstants.TRIANGLES);
		Launcher.applet.vertex(size, 0);
		Launcher.applet.vertex(-size, size / 2);
		Launcher.applet.vertex(-size, -size / 2);
		Launcher.applet.endShape();
		Launcher.applet.popMatrix();
	}

	public void setAi(AI_type ai) {
		this.ai = ai;
	}
}