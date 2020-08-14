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

public class EnviromentalSimulation extends Simulation implements Runnable, BoidsCloneable {

    private Tree MCT;
    private Random random = new Random();
    private double startTime;
    private AI_type ai_type;
    private int maxTreeDepth = 2147483647;
    private int actionCounter = 0;


    private final int maxSimulation = Constants.DEBUG_SIM_LIMIT;
    private int simulations = 0;
    private AI_type simulation_ai;
    private List<PVector> waypoints;

    private Thread thread;
    private boolean isThreadRunning = false;

    public EnviromentalSimulation(ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, BoidGeneric attackBoid, CollisionHandler collisionHandler, List<PVector> waypoints , AI_type simulation_ai) {
        super(BoidsCloneable.copyStateOfBoids(defenderBoids), waypointCoords, attackBoid, collisionHandler, waypoints,simulation_ai);
        defenderBoids = BoidsCloneable.copyStateOfBoids(defenderBoids);
        this.waypoints = waypoints;
        for (BoidGeneric defenderBoid : defenderBoids) {
            defenderBoid.setAi(simulation_ai);
        }
        this.simulation_ai = simulation_ai;

        startTime = System.nanoTime();
        MCT = new Tree(maxTreeDepth, this.attackBoid);

    }

    public void startExecution() {
        isThreadRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public boolean isThreadFinished() {
        return this.thread.isAlive();
    }

    public AI_type getAi_type() {
        return simulation_ai;
    }




    /**
     * Replaces the current MCTS tree structure with an empty root node and returns
     * the best acceleration vector according to the simulations the MCTS performed
     * To prevent memory issues it also runs garbage collection every 10 calls.
     *
     * @param defenderBoids
     * @param attackBoid
     * @return
     */
    public PVector makeDecision(ArrayList<BoidGeneric> defenderBoids, BoidGeneric attackBoid) {
        isThreadRunning=false;
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


    public void run() {
        Node node = MCT.UCT(MCT.getRoot(), -1);
        InnerSimulation innerSimulation = new InnerSimulation(ai_type, defenderBoids, waypointCoords, collisionHandler, node, waypoints,simulation_ai);
        while (isThreadRunning) {

            innerSimulation.run();
            if (!node.isExpanded()) {
                node.expandAndStoreState(innerSimulation);
                continue;
            }
            double simVal = innerSimulation.calcSimulationValue();
            Node childNode = MCT.addChild(node, innerSimulation);
            childNode.backPropagate(simVal);
            simulations++;
        }
    }


    public int getActionCounter() {
        return actionCounter;
    }

    public int getMaxSimulation() {
        return maxSimulation;
    }
}
