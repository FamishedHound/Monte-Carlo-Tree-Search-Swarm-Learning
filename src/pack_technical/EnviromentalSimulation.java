package pack_technical;

import pack_1.Constants;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PApplet;
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
    private boolean finished = false;
    public Thread getThread() {
        return thread;
    }
    private final int howManySimulations = 100;

    private Thread thread = null;
    private boolean isThreadRunning = false;
    PApplet parent;
    public EnviromentalSimulation(PatrollingScheme patrollingScheme,ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, BoidGeneric attackBoid, CollisionHandler collisionHandler, List<PVector> waypoints , AI_type simulation_ai) {
        super(patrollingScheme,BoidsCloneable.copyStateOfBoids(defenderBoids), waypointCoords, attackBoid, collisionHandler,simulation_ai);
        defenderBoids = BoidsCloneable.copyStateOfBoids(defenderBoids);
        this.waypoints = waypoints;
        for (BoidGeneric defenderBoid : defenderBoids) {
            defenderBoid.setAi(simulation_ai);
        }
        this.ai_type = simulation_ai;
        this.parent= parent;
        startTime = System.nanoTime();


    }
    public boolean stopThread(){

        if ( finished) {
            System.out.println("stopping the Thread " + thread.getName());
            finished=false;
            return true;
        }else{
            return false;
        }
    }
    public void startExecution() {
        MCT = new Tree(patrollingScheme, this.attackBoid,this.defenderBoids,ai_type,waypoints,collisionHandler);
        this.thread = new Thread(this);
        this.thread.start();
    }
    public void updateCurrentBoidPosition(ArrayList<BoidGeneric> defenderBoids,BoidGeneric attackBoid){
        this.defenderBoids = BoidsCloneable.copyStateOfBoids(defenderBoids);
        this.attackBoid = new BoidStandard(attackBoid);

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
     * @return
     */
    public PVector makeDecision() {
        return MCT.selectOptimalAction().getAction();

    }


    public void updateBoids(ArrayList<BoidGeneric> defenders, BoidGeneric attacker) {
        this.defenderBoids = BoidsCloneable.copyStateOfBoids(defenders);
        this.attackBoid = new BoidStandard(attacker);

    }


    public void run() {
        System.out.println("starting the Thread" + Thread.currentThread().getName());
        while (simulations<howManySimulations) {
            MCT.iterateTree();
            simulations++;
        }
        finished=true;
    }


    public int getActionCounter() {
        return actionCounter;
    }

    public int getMaxSimulation() {
        return maxSimulation;
    }
}
