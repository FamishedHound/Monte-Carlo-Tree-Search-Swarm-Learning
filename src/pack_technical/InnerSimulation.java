package pack_technical;

import pack_AI.AI_type;
import pack_boids.Boid_generic;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import pack_1.Constants;
import pack_1.Utility;

//TODO: Simulation objects to be stored as fields on the AttackBoid objects themselves?
//TODO: Rename location to currentAttackerLocation
//TODO: Figure out what targetVector is. It seems to only have the local variable theClosest from run() assigned to it
//and theClosest itself only ever has randomVector assigned to it.
//TODO: seems to be a lot of redundant shared code between InnerSim and EnvSim. Tidy.
//TODO: change defenderBoid to be defenderBoids
//TODO: change currentDistance to be currentDistanceToTarget
//TODO: change theClosetDistance to be closetDistanceToTarget
//TODO: I think 'cords' are the waypoint co-ordinates. If so change cords to be waypointCoordinates
//TODO: rename r0acceleration & r0velocity local variables

public class InnerSimulation extends Simulation {

    private int tick =0;
    ArrayList<int[]> historyOfMovement = new ArrayList<>();
    boolean victory = false;
    Integer nextWaypoint;
    Random randG = new Random();
    //what does targetVector actually represent
    PVector targetVector = new PVector(0,0);
    PVector randomVector;
    float theClosetDistance;
    float currentDistance;
    double avgReward;
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
        randomVector = new PVector(-1+2*rand, -1+2*rand2);
        randomVector.setMag(0.1f);
    }

    public InnerSimulation(AI_type ai, ArrayList<Boid_generic> defenders, ArrayList<int[]> cords, ArrayList<Boid_generic> attackers, CollisionHandler collisionHandler, int nodeDepth) throws IOException {
        this.ai_type = ai;
        this.cords= new ArrayList<>(cords);
        this.attackBoids=copyStateOfBoids(attackers);
        this.defenderBoids =copyStateOfBoids(defenders);
        this.collisionHandler = collisionHandler;
        this.nodeDepth = nodeDepth;
        patrollingScheme = new PatrollingScheme(ai.getWayPointForce());

        for(int[] cord : cords){
            patrollingScheme.getWaypoints().add(new PVector(cord[0],cord[1]));
        }

        //FOLLOW THE SIMILLAR WAYPOINT AS DEFENDERS
        // TODO - Magic numbers!!
        float shortestDistanceSq = 3000 * 3000;
        float shortestVectorAngle=0;
        float nextToShortestVectorAngle=0;
        int positionInTheList = 0;
        for(int i=0;i<patrollingScheme.getWaypoints().size();i++) {
            PVector checkpoint = patrollingScheme.getWaypoints().get(i);
            PVector nextCheckPoint = patrollingScheme.getWaypoints().get((i+1)%patrollingScheme.getWaypoints().size());
            float distanceSq = Utility.distSq(defenderBoids.get(0).getLocation(), checkpoint);

            if (distanceSq < shortestDistanceSq) {
                shortestDistanceSq = distanceSq;
                shortestVectorAngle = PVector.angleBetween(defenderBoids.get(0).getLocation(), checkpoint);
                nextToShortestVectorAngle = PVector.angleBetween(defenderBoids.get(0).getLocation(), nextCheckPoint);
            }
        }

        if (shortestVectorAngle < nextToShortestVectorAngle) {
            nextWaypoint = positionInTheList;
        }else{
            nextWaypoint = (positionInTheList + 1) % patrollingScheme.getWaypoints().size();
        }

        patrollingScheme.currentPosition = nextWaypoint;
        createSimulationsAndRandomVectors();
    }



    public void run1(){
        if (simulating) {
            PVector sumOfMassCentres = new PVector(0, 0);
            PVector theClosest = new PVector(0,0);
            int counter = 0;
            float distance = 150; //distance to target from start?
            theClosetDistance = 2000;
            tick++;

            PVector acceleration = attackBoids.get(0).getAcceleration();
            PVector velocity = attackBoids.get(0).getVelocity();
            PVector location = attackBoids.get(0).getLocation();

            for (Boid_generic defenderBoid : defenderBoids) {
                //For each layer in the MCTS, moves every defender boid one iteration
                for(int i=0; i < nodeDepth; i++) {
                    defenderBoid.move(defenderBoids);
                    defenderBoid.update();
                }
                if (Utility.distSq(defenderBoid.getLocation(), location) < Constants.HIT_DISTANCE_SQ) {  // was 3
                    attackBoids.get(0).setHasFailed(true);                                                              //Has collided with a swarm agent
                }
            }

            if((Utility.distSq(location, Constants.TARGET) <= Constants.HIT_DISTANCE_SQ || Utility.distSq(attackBoids.get(0).getLocation(), location) >= distance * distance)
                && !attackBoids.get(0).isHasFailed() ){
                simulating = false;
            }

            //this bit to go in a function
            velocity.limit(1);
            location.add(velocity.add(acceleration.add(randomVector)));
            acceleration.mult(0); //doesnt seen to affect, maybe because velocity.limit is 1?

            // TODO - Could replace this dist with distSq, but that will change all of the currentDistance etc. vars to be currentDistanceSq
            currentDistance = PVector.dist(location, Constants.TARGET);


            if (!attackBoids.get(0).isHasFailed()) {
                if (currentDistance < theClosetDistance) {
                    theClosest = randomVector;
                    theClosetDistance = currentDistance;
                }
                if (!simulating) {
                    targetVector = theClosest;
                }
                if(currentDistance < 15){
                    victory = true;
                }
            } else {
                simulating = false;
            }

            //I think this is the random rollout from newly expanded node
            if(simulating && !victory) {
                PVector locationRollOut = new PVector(location.x, location.y);
                PVector rOacceleration = attackBoids.get(0).getAcceleration();
                PVector rOvelocity = attackBoids.get(0).getVelocity();
                //avgReward is more like instantaneous reward rather than an average reward?

                avgReward = 0;
                for(int j=0; j<1000; j++){
                    locationRollOut.add(rOvelocity.add(rOacceleration.add(randomVector)));
                    //float rand = randG.nextFloat() * 1;
                    //float rand2 = randG.nextFloat() * 1;
                    //locationRollOut.add(rOvelocity.add(rOacceleration.add(new PVector(-1+2*rand, -1+2*rand2))));

                    if(Utility.distSq(locationRollOut, Constants.TARGET) < 20 * 20) {
                        avgReward = 1;
                        break;
                    } else {
                        for (Boid_generic defenderBoid : defenderBoids) {
                            if (Utility.distSq(defenderBoid.getLocation(), locationRollOut) < 16 * 16) {  // was 3
                                avgReward = -1;
                                break;
                            }
                        }
                        if(avgReward < 0){
                            break;
                        }
                    }
                }
            }


            if (simulating) {
                for (Boid_generic defenderBoid : defenderBoids) {
                    PVector accelerationB = defenderBoid.getAcceleration();
                    PVector velocityB = defenderBoid.getVelocity();
                    PVector locationB = defenderBoid.getLocation();

                    defenderBoid.run(defenderBoids, true, true);

                    velocityB.limit(1);
                    locationB.add(velocityB.add(accelerationB.add(patrollingScheme.patrol(defenderBoid.getLocation(), defenderBoid)/*patrolling.patrol(be.getLocation(),be)*/)));
                    accelerationB.mult(0);

                    sumOfMassCentres = PVector.add(sumOfMassCentres, defenderBoid.getLocation());
                    counter++;
                }

                PVector mean = PVector.div(sumOfMassCentres, counter);
                if (tick % 10 == 0) {
                    historyOfMovement.add(new int[]{(int) mean.x + 50, (int) mean.y});
                }
            }
        }
    }
}