package pack_technical;
import pack_1.Constants;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//todo move maxTreeDepth to Constants

public class EnviromentalSimulation extends Simulation implements Runnable,BoidsCloneable {

    Tree MCT;
    Random random = new Random();
    double startTime;
    AI_type ai_type;
    int maxTreeDepth = 2147483647;
    int actionCounter = 0;
    final int maxSimulation = Constants.DEBUG_SIM_LIMIT;
    int simulations = 0;
    AI_type ai;
    List<PVector> waypoints;
    public EnviromentalSimulation(ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, BoidGeneric attackBoid, CollisionHandler collisionHandler,List<PVector> waypoints) {
        super(BoidsCloneable.copyStateOfBoids(defenderBoids), waypointCoords, attackBoid, collisionHandler,waypoints);
        defenderBoids = BoidsCloneable.copyStateOfBoids(defenderBoids);
        this.waypoints = waypoints;
        for (BoidGeneric defenderBoid : defenderBoids) {
            defenderBoid.setAi(simulation_ai);
        }
        this.ai = simulation_ai;

        startTime = System.nanoTime();
        MCT = new Tree(maxTreeDepth, this.attackBoid);
        new Thread(this).start();
    }


    public AI_type getAi_type() {
        return simulation_ai;
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
     * @param defenderBoids
     * @param attackBoid
     */
    public PVector returnTargetVector(ArrayList<BoidGeneric> defenderBoids, BoidGeneric attackBoid) {
        Node bestNode = MCT.bestAvgVal();
        PVector bestVector = bestNode.getAccelerationAction();

        updateBoids(defenderBoids, attackBoid);
        MCT.resetRoot(attackBoid);
        simulations = 0;
        return bestVector;

    }


    public void updateBoids(ArrayList<BoidGeneric> defenders, BoidGeneric attacker) {
        this.defenderBoids = BoidsCloneable.copyStateOfBoids(defenders);
        this.attackBoid = new BoidStandard(attacker);

    }


    //do we need to store a CollisionHandler as a field?
    public void run() {
        while (true) {
            Node node = MCT.UCT(MCT.getRoot(), -1);
            InnerSimulation innerSimulation = new InnerSimulation(ai_type, defenderBoids, waypointCoords, collisionHandler, node,waypoints);
            innerSimulation.run();
            if (!node.isExpanded()) {
                node.expandAndStoreState(innerSimulation);
                continue;
            }
            double simVal =  innerSimulation.calcSimulationValue();
            Node childNode = MCT.addChild(node, innerSimulation);
            childNode.backPropagate(simVal);
            simulations++;
        }
    }
}
