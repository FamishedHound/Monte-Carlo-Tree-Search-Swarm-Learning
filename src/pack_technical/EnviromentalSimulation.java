package pack_technical;

import pack_1.Utility;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.Boid_generic;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;

//todo move maxTreeDepth to Constants

public class EnviromentalSimulation extends Simulation implements Runnable {
    Tree MCT;
    ArrayList<int[]> cords;
    FlockManager flock;
    double startTime = 0;
    int maxTreeDepth = 20;
    int actionCounter = 0;
    boolean treeReady = false;
    boolean dangerClose = false;
    CollisionHandler handler;

    public EnviromentalSimulation(ArrayList<Boid_generic> defenders, ArrayList<int[]> cords, ArrayList<Boid_generic> attackers, CollisionHandler handler) {
        this.handler = handler;
        this.cords = cords;
        this.defenderBoids = defenders;
        this.ai_type = new AI_type(Utility.randFloat(AI_manager.neighbourhoodSeparation_lower_bound, AI_manager.neighbourhoodSeparation_upper_bound), 70, 70, 2.0, 1.2, 0.9f, 0.04f, "Simulator2000");
        defenders = copyStateOfBoids(defenders);
        this.attackBoids = copyStateOfBoids(attackers);
        this.flock = new FlockManager(true, true);
        this.patrollingScheme = new PatrollingScheme(ai_type.getWayPointForce());
        for (Boid_generic g : defenders) {
            g.setAi(this.ai_type);
        }

        for (int[] cord : cords) {
            this.patrollingScheme.getWaypoints().add(new PVector(cord[0], cord[1]));
        }
        //FOLLOW THE SIMILLAR WAYPOINT AS DEFENDERS
        // TODO - Magic numbers!!
        float shortestDistanceSq = 3000 * 3000;
        float shortestVectorAngle=0;
        float nextToShortestVectorAngle=0;
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
        startTime = System.nanoTime();

        MCT = new Tree(maxTreeDepth);
        //the PVector would be a random vector, but for the root it is just 0.
        //TODO: abstract below line to Node constructor if it proves easy
        MCT.root.storeDetails(new PVector(0,0,0), attackBoids);
        new Thread(this).start();
    }


    public void setAiToInnerSimulation(AI_type t) {
        ai_type = t;
    }


    public boolean isSimulating() {
        return true;
    }

    public PVector reutrnTargetVecotr() {
        Node bestSim = MCT.bestAvgVal();
        PVector bestVector = bestSim.MrLeandroVector;
        try {
            MCT.root = new Node(0, "root", 0, 0);
            MCT.root.storeDetails(new PVector(0,0,0), attackBoids);
            dangerClose = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(actionCounter > 10){
            System.gc();
            System.runFinalization();
            actionCounter = 0;
        }else{
            actionCounter++;
        }


        return bestVector;
    }


    public void updateBoids(ArrayList<Boid_generic> defenders, ArrayList<Boid_generic> attacker) {
        this.defenderBoids = copyStateOfBoids(defenders);
        this.attackBoids = copyStateOfBoids(attacker);
    }


    public void run() {
        while (true) {
            try {
                Node n = MCT.UCT(MCT.root);
                InnerSimulation newSim;
                if(n.parent == null){
                    newSim = new InnerSimulation(ai_type, defenderBoids, cords, attackBoids, handler, n.depth);
                }else {
                    newSim = new InnerSimulation(ai_type, defenderBoids, cords, n.parent.attacker, handler, n.depth);
                }
                newSim.run1();

                dangerClose = newSim.avgReward < 0;

                double simVal = 0;
                if (newSim.attackBoids.get(0).isHasFailed()) {
                    simVal = -1 ;
                } else if (newSim.victory) {
                    simVal = 1;
                } else {
                    if(!dangerClose) {
                        simVal = 0.5 - (newSim.currentDistance / 6000);
                    }
                }

                String nodeName = n.name + "." + n.children.size(); 
                n.addChild(simVal, nodeName, newSim.avgReward);
                n.children.get(n.children.size()-1).storeDetails(newSim.MrLeandroVector, newSim.attackBoids);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
