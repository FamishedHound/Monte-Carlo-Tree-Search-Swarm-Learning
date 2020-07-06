package pack_technical;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;

//todo move maxTreeDepth to Constants

public class EnviromentalSimulation extends Simulation implements Runnable {

    Tree MCT;
    FlockManager flockManager;
    double startTime;
    AI_type ai_type;
    int maxTreeDepth = 20;
    int actionCounter = 0;

    public EnviromentalSimulation(ArrayList<BoidGeneric> defenderBoids, ArrayList<int[]> waypointCoords, ArrayList<BoidGeneric> attackBoids, CollisionHandler collisionHandler) {
        super(defenderBoids, waypointCoords, copyStateOfBoids(attackBoids), collisionHandler);
        defenderBoids = copyStateOfBoids(defenderBoids);
        this.flockManager = new FlockManager(true, true);

        for (BoidGeneric defenderBoid : defenderBoids) {
            defenderBoid.setAi(this.ai_type);
        }

        waypointSetup(defenderBoids);
        startTime = System.nanoTime();
        MCT = new Tree(maxTreeDepth);
        //the PVector would be a random vector, but for the root it is just 0.
        //TODO: abstract below line to Node constructor if it proves easy
        MCT.root.storeDetails(new PVector(0,0,0), this.attackBoids);
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
        PVector bestVector = bestSim.actionAcceleration;
        try {
            MCT.root = new Node(0, "root", 0, 0);
            MCT.root.storeDetails(new PVector(0,0,0), attackBoids);
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


    public void updateBoids(ArrayList<BoidGeneric> defenders, ArrayList<BoidGeneric> attacker) {
        this.defenderBoids = copyStateOfBoids(defenders);
        this.attackBoids = copyStateOfBoids(attacker);

    }


    public void run() {
        while (true) {
            Node node = MCT.UCT(MCT.root);
            InnerSimulation newSim;
            if(node.parent == null){
                newSim = new InnerSimulation(ai_type, defenderBoids, waypointCoords, attackBoids, collisionHandler, node.depth);
            }else {
                newSim = new InnerSimulation(ai_type, defenderBoids, waypointCoords, node.parent.attacker, collisionHandler, node.depth);
            }
            newSim.run();

            //fix, definitely wrong bc these defender boids wwont have been moved like the ones in newSim
            boolean dangerClose = newSim.avgReward < 0;

            double simVal = 0;
            if (newSim.getAttackBoid().hasFailed()) {
                simVal = -1 ;
            } else if (newSim.victory) {
                simVal = 1;
            } else {
                if(!dangerClose) {
                    simVal = 0.5 - (newSim.currentDistance / 6000);
                }
            }

            String nodeName = node.name + "." + node.children.size();
            node.addChild(simVal, nodeName, newSim.avgReward);
            node.children.get(node.children.size()-1).storeDetails(newSim.randomVector, newSim.attackBoids);
        }
    }
}
