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
    AI_type simulation_ai;

    public void setSimulating(boolean simulating) {
        this.simulating = simulating;
    }

    public PVector createRandomVector() {
        PVector randomAcceleration = new PVector(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1);
        return randomAcceleration.setMag(Constants.Boids.MAX_ACC_ATTACK);
    }

    public PVector getAccelerationAction() {
        return accelerationAction.copy();
    }

    public double getRolloutReward() {
        return this.rolloutReward;
    }



    public InnerSimulation(AI_type ai, ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, CollisionHandler collisionHandler, Node node,List<PVector> waypoints,AI_type simulation_ai) {
        super(defenderBoids, waypointCoords, node.getAttackBoidsForSimulation(), collisionHandler,waypoints,simulation_ai);
        this.simulation_ai = simulation_ai;
        this.nodeExpanded = node.isExpanded();
        this.nodeDepth = node.getDepth();
        accelerationAction = node.isExpanded() ? createRandomVector() : node.getAccelerationAction();
    }

    public double calcSimulationValue() {
        if (getAttackBoid().hasFailed() || rolloutReward == -1) {
            return -1;
        } else if (victory || rolloutReward == 1) {
            return 1;
        } else if (rolloutReward == 0) {
            return 0.5 - (currentDistanceToTarget / 6000);
        }
        return 0;
    }

    public double rollout() {
        BoidGeneric rolloutAttackBoid = new BoidStandard(getAttackBoid());
        for(int j=0; j<1000; j++) {
            rolloutAttackBoid.updateAttack(getAccelerationAction());
            if(CollisionHandler.doesReachTarget(rolloutAttackBoid, 0)) {
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

        getAttackBoid().updateAttack(getAccelerationAction());

        if (CollisionHandler.checkCollisions(attackBoid, defenderBoids, 0)) {
            getAttackBoid().setHasFailed(true);
        }

        if(CollisionHandler.doesReachTarget(getAttackBoid(), 0) && !getAttackBoid().hasFailed()) {
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