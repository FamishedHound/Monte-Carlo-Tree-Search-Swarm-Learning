package pack_technical;

import pack_1.Constants;
import pack_1.Utility;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public abstract class Simulation implements BoidsCloneable {

    ArrayList<BoidGeneric> defenderBoids;
    BoidGeneric attackBoid;
    AI_type simulation_ai;
    PatrollingScheme patrollingScheme;
    CollisionHandler collisionHandler;
    List<PVector> waypointCoords;
    FlockManager flockManager;

    public Simulation(ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, BoidGeneric attackBoid, CollisionHandler collisionHandler,List<PVector> waypoints)  {
        this.collisionHandler = collisionHandler;
        this.waypointCoords = waypointCoords;
        this.defenderBoids = BoidsCloneable.copyStateOfBoids(defenderBoids);
        this.simulation_ai = Constants.PERFECT_AI ? Constants.CORRECT_AI_PARAMS : new AI_type(Utility.randFloat(AI_manager.neighbourhoodSeparation_lower_bound, AI_manager.neighbourhoodSeparation_upper_bound), 70, 70, 2.0, 1.2, 0.9f, 0.04f, "Simulator2000");
        this.attackBoid = new BoidStandard(attackBoid);
        this.patrollingScheme = new PatrollingScheme(simulation_ai.getWayPointForce());
        this.patrollingScheme.getWaypoints().addAll(waypoints);
        this.flockManager = new FlockManager(false, true, this.defenderBoids);
    }



    public AI_type getSimulator() {
        return simulation_ai;
    }

    public BoidGeneric getAttackBoid() {
        return attackBoid;
    }

//    public void waypointSetup(ArrayList<BoidGeneric> defenders) {
//        for (PVector cord : waypointCoords) {
//            this.patrollingScheme.getWaypoints().add(cord);
//        }
//        //FOLLOW THE SIMILLAR WAYPOINT AS DEFENDERS
//        // TODO - Magic numbers!!
//        float shortestDistanceSq = 3000 * 3000;
//        int counter = 0;
//        int positionInTheList = 0;
//        for (PVector checkpoint : this.patrollingScheme.getWaypoints()) {
//            float distanceSq = Utility.distSq(defenders.get(0).getLocation(), checkpoint);
//            counter++;
//            if (distanceSq < shortestDistanceSq) {
//                shortestDistanceSq = distanceSq;
//                positionInTheList = counter;
//            }
//        }
//
//        patrollingScheme.setup();
//
//        for (int i = 0; i < positionInTheList + 1; i++) {
//            if (!patrollingScheme.getIterator().hasNext()) {
//                // if the end of the list of waypoints has been reached, reassigns the iterator
//                // to patrollingScheme so it can begin from the beginning again
//                patrollingScheme.setIterator(patrollingScheme.getWaypoints().iterator());
//            }
//            patrollingScheme.setCurrWaypoint(patrollingScheme.getIterator().next());
//        }
//    }
}