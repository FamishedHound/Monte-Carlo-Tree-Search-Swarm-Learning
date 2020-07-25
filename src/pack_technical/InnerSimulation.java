package pack_technical;

import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pack_1.Constants;

//todo: would be nice to have relevant getters be fail or be otherwise hidden if InnerSimulation.run has not been ran

public class InnerSimulation extends Simulation {

    boolean victory = false;
    boolean nodeExpanded;
    Random random = new Random();
//PVector chosenAccelerationAction;
    PVector accelerationAction;
    float closestDistanceToTarget;
    float currentDistanceToTarget;
    double rolloutReward;
    int nodeDepth;
    boolean simulating=true;

    public boolean isSimulating() {
        return simulating;
    }

    public void setSimulating(boolean simulating) {
        this.simulating = simulating;
    }

    public PVector createRandomVector() {
        PVector randomAcceleration = new PVector(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1);
        return randomAcceleration.setMag(0.1f);
    }

    public PVector getAccelerationAction() {
        return accelerationAction.copy();
    }

    public double getRolloutReward() {
        return this.rolloutReward;
    }

    public InnerSimulation(AI_type ai, ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, CollisionHandler collisionHandler, Node node) {
        super(defenderBoids, waypointCoords, node.getAttackBoidsForSimulation(), collisionHandler);
        this.ai_type = ai;
        this.nodeExpanded = node.isExpanded();
        this.nodeDepth = node.getDepth();
        waypointSetup(defenderBoids);
        accelerationAction = node.isExpanded() ? createRandomVector() : node.getAccelerationAction();
    }

    public double calcSimulationValue() {
        if (getAttackBoid().hasFailed()) {
            return -1;
        } else if (victory) {
            return 1;
        } else if (rolloutReward > 0) {
            return 0.5 - (currentDistanceToTarget / 6000);
        }
        return 0;
    }

    public double rollout() {
        BoidGeneric rolloutAttackBoid = new BoidStandard(getAttackBoid());
        for(int j=0; j<1000; j++){
            rolloutAttackBoid.update(getAccelerationAction());
            if(CollisionHandler.doesReachTarget(rolloutAttackBoid, 5)) {
                return 1;
            }
            for (BoidGeneric defenderBoid : defenderBoids) {
                if (CollisionHandler.doesCollide(rolloutAttackBoid, defenderBoid, 0)) {
                    return -1;
                    }
                }
            }
        return 0;
    }

    public void run(){
        //PVector theClosest = new PVector(0,0);
        closestDistanceToTarget = 2000;
        PVector currentAttackerLocation = getAttackBoid().getLocation();

        for (BoidGeneric defenderBoid : defenderBoids) {
            //For each layer in the MCTS, moves every defender boid one iteration
            //probs should be done via flockManager
            for(int i=0; i < nodeDepth; i++) {
                defenderBoid.move(defenderBoids);
                defenderBoid.update();
            }
        }


        getAttackBoid().update(getAccelerationAction());

        if (CollisionHandler.checkCollisions(attackBoid, defenderBoids, 2)) {
            getAttackBoid().setHasFailed(true);
        }

        if(CollisionHandler.doesReachTarget(getAttackBoid(), 5) && !getAttackBoid().hasFailed()) {
            simulating = false;
        }

        currentDistanceToTarget = PVector.dist(currentAttackerLocation, Constants.TARGET);

        if (!getAttackBoid().hasFailed()) {
                victory = CollisionHandler.doesReachTarget(getAttackBoid(), 0);
        } else {
            simulating = false;
            rolloutReward = -1;
        }

        //maybe return the below?
        rolloutReward = simulating && !victory && nodeExpanded ? rollout() : rolloutReward;
    }
}