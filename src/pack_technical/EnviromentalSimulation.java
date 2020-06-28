package pack_technical;

import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.Boid_generic;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.IOException;
import java.util.*;

//todo move maxTreeDepth to Constants

public class EnviromentalSimulation extends Simulation implements Runnable {
    Tree MCT;
    PApplet parent;
    ArrayList<int[]> cords;
    FlockManager flock;
    double startTime = 0;
    int maxTreeDepth = 20;
    int actionCounter = 0;
    boolean treeReady = false;
    boolean dangerClose = false;

    public AI_type getSimulator() {
        return ai_type;
    }


    public EnviromentalSimulation(int sns, int ans, int cns, double sw, double aw, double cw, String name, ArrayList<Boid_generic> defenders, PApplet parent, ArrayList<int[]> cords, ArrayList<Boid_generic> attackers, CollisionHandler collisionHandler) throws IOException {
        this.parent = parent;
        this.collisionHandler = collisionHandler;
        this.cords = cords;
        this.defenders = defenders;

        ai_type = new AI_type(randFloat(AI_manager.neighbourhoodSeparation_lower_bound, AI_manager.neighbourhoodSeparation_upper_bound), 70, 70, 2.0, 1.2, 0.9f, 0.04f, "Simulator2000");

        defenders = copyStateOfBoids(defenders);
        this.attackBoids = copyStateOfBoids(attackers);

        this.flock = new FlockManager(parent, true, true);
        this.patrollingScheme = new PatrollingScheme(ai_type.getWayPointForce());
        for (Boid_generic g : defenders) {
            g.setAi(ai_type);
        }

        for (int[] cord : cords) {
            patrollingScheme.getWaypoints().add(new PVector(cord[0], cord[1]));
        }
        //FOLLOW THE SIMILLAR WAYPOINT AS DEFENDERS
        float shortestDistance = 3000;
        int counter = 0;
        int positionInTheList = 0;
        for (PVector checkpoint : patrollingScheme.getWaypoints()) {
            float distance = PVector.dist(defenders.get(0).getLocation(), checkpoint);
            counter++;
            if (distance < shortestDistance) {
                shortestDistance = distance;
                positionInTheList = counter;
            }
        }

        patrollingScheme.setup();

        for (int i = 0; i < positionInTheList + 1; i++) {
            if (!patrollingScheme.getIterator().hasNext()) {
                // if the end of the list of waypoints has been reached, reassigns the iterator
                // to scheme so it can begin from the beginning again
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


    public static float randFloat(float min, float max) {
        Random rand = new Random();
        float result = rand.nextFloat() * (max - min) + min;
        return result;
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
        this.defenders = copyStateOfBoids(defenders);
        this.attackBoids = copyStateOfBoids(attacker);
    }


    public void run() {
        while (true) {
            try {
                Node n = MCT.UCT(MCT.root);
                InnerSimulation newSim;
                if(n.parent == null){
                    newSim = new InnerSimulation(ai_type, defenders, cords, attackBoids, collisionHandler, parent, n.depth);
                }else {
                    newSim = new InnerSimulation(ai_type, defenders, cords, n.parent.attacker, collisionHandler, parent, n.depth);
                }
                newSim.run1();

                if(newSim.avgReward < 0){
                    dangerClose = true;
                }else{
                    dangerClose = false;
                }

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
