package pack_boids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pack_1.Launcher;
import pack_AI.AI_internal_model;
import pack_AI.AI_machine_learner;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_technical.FlockManager;
import pack_technical.GameManager;
import processing.core.PConstants;
import processing.core.PVector;

// there is only one type of boid at the moment, this one can shoot(?) and predict
public class BoidStandard extends BoidGeneric {

	// machine learning apparatus
	FlockManager mind_flock = new FlockManager(false, false); // for inhead simulation
	AI_internal_model internal_model;
	AI_machine_learner machine_learner;

    public BoidStandard(float x, float y, int t, int id) {
        super(x, y, t,id);
        ai = AI_manager.get_team_ai(t);
        angle = new Random().nextInt(360); // degrees
        velocity = PVector.random2D().mult(2);
        internal_model = new AI_internal_model(false, this); // perfect model;
        machine_learner = new AI_machine_learner(this); // perfect model;
    }

	public BoidStandard(BoidGeneric boid_generic) {
		super(boid_generic);
		this.ai = AI_manager.get_team_ai(boid_generic.getTeam());
		this.angle = boid_generic.getAngle(); // degrees
		this.internal_model = new AI_internal_model(false, this); // perfect model;
		this.machine_learner = new AI_machine_learner(this); // perfect model;
	}

    @Override
    public void run(List<BoidGeneric> boids, boolean simulation) {
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
                if (Launcher.getPredictState() != Launcher.PredictStates.NONE && (Launcher.getPredictState() == Launcher.PredictStates.ALL || GameManager.getSelected_boid() == this)) {
                    attempt_future();
                }
            }
        }

	// Method to update location
	protected void attempt_future() {
		if (Launcher.getFlock().getBoidCount() > 0) {
			mind_flock.importImaginaryBoids(Launcher.getFlock().getAllBoids(), internal_model);
			mind_flock.run(Launcher.HISTORY_LENGTH);
			machine_learner.run(mind_flock);
			mind_flock.reset();
		}
	}

	public AI_internal_model getInternal_model() {
		return internal_model;
	}

}