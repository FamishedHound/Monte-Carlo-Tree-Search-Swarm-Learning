package pack_technical;
import pack_1.Constants;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

//todo move maxTreeDepth to Constants

public class EnviromentalSimulation extends Simulation implements Runnable {

    Tree MCT;
    FlockManager flockManager;
    double startTime;
    AI_type ai_type;
    int maxTreeDepth = 20;
    int actionCounter = 0;
    final int maxSimulation = Constants.DEBUG_SIM_LIMIT;
    int simulations = 0;

    public EnviromentalSimulation(ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, ArrayList<BoidGeneric> attackBoids, CollisionHandler collisionHandler) {
        super(defenderBoids, waypointCoords, copyStateOfBoids(attackBoids), collisionHandler);
        defenderBoids = copyStateOfBoids(defenderBoids);
        this.flockManager = new FlockManager(true, true);

        for (BoidGeneric defenderBoid : defenderBoids) {
            defenderBoid.setAi(this.ai_type);
        }

        waypointSetup(defenderBoids);
        startTime = System.nanoTime();
        MCT = new Tree(maxTreeDepth, this.attackBoids);
        new Thread(this).start();
    }


    public void setAiToInnerSimulation(AI_type t) {
        ai_type = t;
    }


    public boolean isSimulating() {
        return true;
    }

    /**
     * Replaces the current MCTS tree structure with an empty root node and returns
     * the best acceleration vector according to the simulations the MCTS performed
     * To prevent memory issues it also runs garbage collection every 10 calls.
     *
     * @return
     */
    public PVector returnTargetVector(ArrayList<BoidGeneric> defenderBoids, ArrayList<BoidGeneric> attackBoids) {
        Node bestSim = MCT.bestAvgVal();
        PVector bestVector = bestSim.accelerationAction;
        try {
            updateBoids(defenderBoids, attackBoids);
            MCT.root = new Node(0, "root", 0, 0, attackBoids);
            simulations = 0;
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
            Node node = MCT.UCT(MCT.root, -1);
            InnerSimulation newSim;
            if(node.parent == null){
                newSim = new InnerSimulation(ai_type, defenderBoids, waypointCoords, attackBoids, collisionHandler, node.depth);
            }else {
                newSim = new InnerSimulation(ai_type, defenderBoids, waypointCoords, node.parent.attackBoids, collisionHandler, node.depth);
            }
            newSim.run();

            boolean dangerClose = newSim.rolloutReward < 0;

            double simVal = 0;
            if (newSim.getAttackBoid().hasFailed()) {
                simVal = -1 ;
            } else if (newSim.victory) {
                simVal = 1;
            } else {
                if(!dangerClose) {
                    simVal = 0.5 - (newSim.currentDistanceToTarget / 6000);
                }
            }

            String nodeName = node.name + "." + node.children.size();
            Node childNode = node.addChild(simVal, nodeName, newSim.rolloutReward, newSim.attackBoids, newSim.randomAccelerationAction);
            childNode.backPropagate(simVal);
            simulations++;
        }
    }
}
