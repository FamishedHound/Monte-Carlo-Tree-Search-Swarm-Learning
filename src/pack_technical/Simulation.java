package pack_technical;

import pack_1.Constants;
import pack_1.Utility;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;

public abstract class Simulation {

    ArrayList<BoidGeneric> defenderBoids;
    ArrayList<BoidGeneric> attackBoids;
    AI_type ai_type;
    PatrollingScheme patrollingScheme;
    CollisionHandler collisionHandler;
    ArrayList<int[]> waypointCoords;

    public Simulation(ArrayList<BoidGeneric> defenderBoids, ArrayList<int[]> waypointCoords, ArrayList<BoidGeneric> attackBoids, CollisionHandler collisionHandler) {
        ArrayList<int[]> correctCoords = new ArrayList<>();
        //only works on medium, chamnge hardcodes for others
        int[] wp1 = new int[]{(int) PVector.add(Constants.TARGET, new PVector(-100, 50)).x, (int) PVector.add(Constants.TARGET, new PVector(-100, 50)).y};
        int[] wp2 = new int[]{(int) PVector.add(Constants.TARGET, new PVector(100, 0)).x, (int) PVector.add(Constants.TARGET, new PVector(100, 0)).y};
        int[] wp3 = new int[]{(int) PVector.add(Constants.TARGET, new PVector(-100, -95)).x, (int) PVector.add(Constants.TARGET, new PVector(-100, 95)).y};
        correctCoords.add(wp1);
        correctCoords.add(wp2);
        correctCoords.add(wp3);
        this.collisionHandler = collisionHandler;
        this.waypointCoords = correctCoords;
        this.defenderBoids = defenderBoids;
        this.ai_type = new AI_type(30, 70, 70, 2.0, 1.2, 0.9f, 0.04f, "Simulator2000");
        this.attackBoids = attackBoids;
        this.patrollingScheme = new PatrollingScheme(ai_type.getWayPointForce());
    }

    public static ArrayList<BoidGeneric> copyStateOfBoids(ArrayList<BoidGeneric> boids) {
        ArrayList<BoidGeneric> boidListClone = new ArrayList<>();

        for (BoidGeneric boid : boids) {
            BoidGeneric bi = new BoidStandard(boid.getLocation().x, boid.getLocation().y, 6, 10);
            bi.setAcceleration(boid.getAcceleration());
            bi.setVelocity(boid.getVelocity());
            bi.setLocation(boid.getLocation());
            boidListClone.add(bi);
        }
        return boidListClone;
    }

    public AI_type getSimulator() {
        return ai_type;
    }

    public BoidGeneric getAttackBoid() {
        return attackBoids.get(0);
    }

    public BoidGeneric getAttackBoid(int index) {
        return attackBoids.get(index);
    }

    public void waypointSetup(ArrayList<BoidGeneric> defenders) {
        for (int[] cord : waypointCoords) {
            this.patrollingScheme.getWaypoints().add(new PVector(cord[0], cord[1]));
        }
        //FOLLOW THE SIMILLAR WAYPOINT AS DEFENDERS
        // TODO - Magic numbers!!
        float shortestDistanceSq = 3000 * 3000;
        int positionInTheList = 0;
        for (PVector checkpoint : this.patrollingScheme.getWaypoints()) {
            float distanceSq = Utility.distSq(defenders.get(0).getLocation(), checkpoint);
            if (distanceSq < shortestDistanceSq) {
                shortestDistanceSq = distanceSq;
            }
        }

        patrollingScheme.setup();

        for (int i = 0; i < positionInTheList + 1; i++) {
            if (!patrollingScheme.getIterator().hasNext()) {
                // if the end of the list of waypoints has been reached, reassigns the iterator
                // to patrollingScheme so it can begin from the beginning again
                patrollingScheme.setIterator(patrollingScheme.getWaypoints().iterator());
            }
            patrollingScheme.setCurrWaypoint(patrollingScheme.getIterator().next());
        }
    }



}