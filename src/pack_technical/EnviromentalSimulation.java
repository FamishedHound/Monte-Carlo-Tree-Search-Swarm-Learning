package pack_technical;
import pack_1.Constants;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//todo move maxTreeDepth to Constants

public class EnviromentalSimulation extends Simulation implements Runnable {

    Tree MCT;
    Random random = new Random();
    double startTime;
    AI_type ai_type;
    int maxTreeDepth = 2147483647;
    int actionCounter = 0;
    final int maxSimulation = Constants.DEBUG_SIM_LIMIT;
    int simulations = 0;

    public EnviromentalSimulation(ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, ArrayList<BoidGeneric> attackBoids, CollisionHandler collisionHandler) {
        super(copyStateOfBoids(defenderBoids), waypointCoords, copyStateOfBoids(attackBoids), collisionHandler);
        defenderBoids = copyStateOfBoids(defenderBoids);

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
        Node bestNode = MCT.bestAvgVal();
        PVector bestVector = bestNode.getAccelerationAction();
        try {
            updateBoids(defenderBoids, attackBoids);
            MCT.setRoot(new Node(0, "root", 0, 0, attackBoids));
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


    //do we need to store a CollisionHandler as a field?
    public void run() {
        while (true) {
            Node node = MCT.UCT(MCT.getRoot(), -1);
            InnerSimulation newSim = new InnerSimulation(ai_type, defenderBoids, waypointCoords, collisionHandler, node);
            newSim.run();
            double simVal = newSim.getSimulationValue();
            String nodeName = node.getName() + "." + node.getChildren().size();
            Node childNode = MCT.addChild(node, simVal, nodeName, newSim.rolloutReward, newSim.attackBoids, newSim.getAccelerationAction());
            childNode.backPropagate(simVal);
            simulations++;
        }
    }
}
