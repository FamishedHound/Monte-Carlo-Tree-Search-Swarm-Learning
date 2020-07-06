package pack_technical;

import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

import pack_1.Constants;
import pack_1.Utility;


//TODO: change currentDistance to be currentDistanceToTarget
//TODO: I think 'waypointCoords' are the waypoint co-ordinates. If so change waypointCoords to be waypointCoordinates
//TODO: rename r0acceleration & r0velocity local variables

public class InnerSimulation extends Simulation {

    boolean victory = false;
    Random randG = new Random();
    PVector chosenAccelerationAction = new PVector(0,0);
    PVector randomAccelerationAction;
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

    public void createSimulationsAndRandomVectors(){
        float rand = randG.nextFloat() * 1;
        float rand2 = randG.nextFloat() * 1;
        randomAccelerationAction = new PVector(-1+2*rand, -1+2*rand2);
        randomAccelerationAction.setMag(0.1f);
    }

    public InnerSimulation(AI_type ai, ArrayList<BoidGeneric> defenderBoids, ArrayList<int[]> waypointCoords, ArrayList<BoidGeneric> attackBoids, CollisionHandler collisionHandler, int nodeDepth) {
        super(copyStateOfBoids(defenderBoids), waypointCoords, copyStateOfBoids(attackBoids), collisionHandler);
        this.ai_type = ai;
        this.nodeDepth = nodeDepth;
        waypointSetup(defenderBoids);
        createSimulationsAndRandomVectors();
    }



    public void run(){
        if (simulating) {
            PVector theClosest = new PVector(0,0);
            float distance = 150; //distance to target from start?
            closestDistanceToTarget = 2000;
            PVector currentAttackerLocation = getAttackBoid().getLocation();

            for (BoidGeneric defenderBoid : defenderBoids) {
                //For each layer in the MCTS, moves every defender boid one iteration
                for(int i=0; i < nodeDepth; i++) {
                    defenderBoid.move(defenderBoids);
                    defenderBoid.update();
                }
                if (Utility.distSq(defenderBoid.getLocation(), getAttackBoid().getLocation()) < Constants.COLLISION_DISTANCE_SQ) {
                    getAttackBoid().setHasFailed(true);
                }
            }

            //there was a check to see if attackBoid had moved certain distance in tree lifetime,
            //but it was wrong in its current form so it was removed
            if((Utility.distSq(currentAttackerLocation, Constants.TARGET) <= Constants.HIT_DISTANCE_SQ) && !getAttackBoid().hasFailed() ){
                simulating = false;
            }

            getAttackBoid().update(randomAccelerationAction);
            // TODO - Could replace this dist with distSq, but that will change all of the currentDistance etc. vars to be currentDistanceSq
            currentDistanceToTarget = PVector.dist(currentAttackerLocation, Constants.TARGET);


            if (!getAttackBoid().hasFailed()) {
                if (currentDistanceToTarget < closestDistanceToTarget) {
                    theClosest = randomAccelerationAction;
                    closestDistanceToTarget = currentDistanceToTarget;
                }
                if (!simulating) {
                    chosenAccelerationAction = theClosest;
                }
                if(currentDistanceToTarget < 15){
                    victory = true;
                }
            } else {
                simulating = false;
            }

            if(simulating && !victory) {
                BoidGeneric rolloutAttackBoid = new BoidStandard(getAttackBoid());
                rolloutReward = 0;

                for(int j=0; j<1000; j++){
                    rolloutAttackBoid.update(randomAccelerationAction);
                    if(Utility.distSq(rolloutAttackBoid.getLocation(), Constants.TARGET) < 20 * 20) {
                        rolloutReward = 1;
                        break;
                    } else {
                        for (BoidGeneric defenderBoid : defenderBoids) {
                            if (Utility.distSq(defenderBoid.getLocation(), rolloutAttackBoid.getLocation()) < 16 * 16) {  // was 3
                                rolloutReward = -1;
                                break;
                            }
                        }
                        if(rolloutReward < 0){
                            break;
                        }
                    }
                }
            }
        }
    }
}