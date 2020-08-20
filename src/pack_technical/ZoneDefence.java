package pack_technical;

import pack_1.Constants;
import pack_1.ParameterGatherAndSetter;
import pack_1.Utility;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class ZoneDefence implements Cloneable {

    private final boolean defend = true;
    private final ArrayList<BoidGeneric> defenderBoids;
    private final ArrayList<BoidGeneric> attackBoids;
    static int counter = 0;
    boolean flag = true;
    int warmUpTimer = 0;
    CollisionHandler collisionHandler;
    PatternLearning patternHandler;
    //timing simulation/real world
    long startTime = 0;
    private final PatrollingScheme patrollingScheme = new PatrollingScheme(0.04f);
    private final ArrayList<PVector> waypoints = patrollingScheme.getWaypoints();
    EnviromentalSimulation enviromentalSimulation;
    private final AtomicBoolean attack = new AtomicBoolean();
    FlockManager flockManager;
    ParameterSimulator parameterSimulation;
    ParameterGatherAndSetter parameterGatherAndSetter;
    PApplet parent;

    AI_type simulation_ai;
    //TODO fix this hardcoded path
    //public PrintWriter writer14 = new PrintWriter("output/AttackingAndUpdatingTime.txt");

    public ZoneDefence(PApplet parent, CollisionHandler collision, FlockManager flockManager, ParameterGatherAndSetter parameterGatherAndSetter) throws IOException {
        this.flockManager = flockManager;
        this.collisionHandler = collision;
        this.parent = parent;
        defenderBoids = GameManager.get_team(0);
        attackBoids = GameManager.get_team(1);
        if (Constants.PERFECT_WAYPOINTS) {
            this.patternHandler = new DummyPatternHandler();
        } else {
            this.patternHandler = new PatternHandler();
        }
        this.simulation_ai = Constants.PERFECT_AI ? Constants.CORRECT_AI_PARAMS :
                new AI_type(Utility
                        .randFloat(AI_manager.neighbourhoodSeparation_lower_bound, AI_manager.neighbourhoodSeparation_upper_bound), 70, 70, 2.0, 1.2, 0.9f, 0.04f, "Simulator2000");
        this.parameterGatherAndSetter = parameterGatherAndSetter;
        waypoints.addAll(parameterGatherAndSetter.returnDifficulty());
        patrollingScheme.getWaypointsA().add(Constants.TARGET.copy());
        patrollingScheme.setup();
        enviromentalSimulation = new EnviromentalSimulation(patrollingScheme,defenderBoids, patternHandler.getNewpoints(), attackBoids.get(0), collisionHandler, parameterGatherAndSetter.returnDifficulty(),simulation_ai);
        this.simulation_ai = enviromentalSimulation.getAi_type();
        attack.set(true);

    }


    public void run() {

        enviromentalSimulation.startExecution();
        //parameterGatherAndSetter.sendParameters(this.simulation_ai);

        updateDefenders();
        updateAttacker();
        parameterGatherAndSetter.incrementIterations();
    }

    private void updateAttacker() {
        for (BoidGeneric attackBoid : attackBoids) {

            //handleWarmup(attackBoid);
            attackMode(attackBoid);
        }
    }

    private void attackMode(BoidGeneric attackBoid) {

        attackBoid.setMovable(true);
        //debugSimulationLimit();
        applyMCTSVector(attackBoid);
        debugDrawMCTSVectors(attackBoid);



    }

    private void handleWarmup(BoidGeneric attackBoid) {
        counter++;
        if (counter >= Constants.warmUpTime / 8 && counter <= Constants.warmUpTime * 2) {
            if (!attack.get()) attackBoid.setMovable(false);
            attackBoid.setStationary();
            warmUpTimer++;
        }

        if (warmUpTimer >= Constants.warmUpTime) {
            patternHandler.newObservation(defenderBoids, counter);
            if (attackBoids != null && flag && patternHandler.analyze() == 1) {
                flag = false;
                startTime = System.nanoTime();
            }
        }
    }

    private void  applyMCTSVector(BoidGeneric attackBoid) {



        while(!enviromentalSimulation.stopThread()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        PVector attackVector = enviromentalSimulation.makeDecision();

        attackBoid.updateAttack(attackVector);

    }

    private void debugSimulationLimit() {
        if (Constants.DEBUG_SIM_LIMIT != 0) {
            while (enviromentalSimulation.getActionCounter() < enviromentalSimulation.getMaxSimulation()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void debugDrawMCTSVectors(BoidGeneric attackBoid) {
        parent.fill(255, 255, 30);
        PVector randomAcceleration1 = new PVector(new Random().nextFloat() * 2 - 1, new Random().nextFloat() * 2 - 1);
        PVector randomAcceleration2 = new PVector(new Random().nextFloat() * 2 - 1, new Random().nextFloat() * 2 - 1);

        PVector randomAcceleration3 = new PVector(new Random().nextFloat() * 2 - 1, new Random().nextFloat() * 2 - 1);

        parent.line(attackBoid.getLocation().x, attackBoid.getLocation().y, attackBoid.getLocation().x + attackBoid.getVelocity().x * 100, attackBoid.getLocation().y + attackBoid.getVelocity().y * 100);
        parent.line(attackBoid.getLocation().x, attackBoid.getLocation().y, attackBoid.getLocation().x + randomAcceleration1.x * 100, attackBoid.getLocation().y + randomAcceleration1.y * 100);
//                for (Node n : enviromentalSimulation.getRoot().getChildren()){
//                    parent.line(attackBoid.getLocation().x, attackBoid.getLocation().y, attackBoid.getLocation().x + attackBoid.getVelocity().x*100, attackBoid.getLocation().y + attackBoid.getVelocity().y*100);
//                }
    }

    private void updateDefenders() {
        for (BoidGeneric defenderBoid : defenderBoids) {
            if (defend) {
                defenderBoid.update(patrollingScheme.patrol(defenderBoid.getLocation(), defenderBoid));
            } else {
                defenderBoid.setStationary();
            }
        }
    }
}
