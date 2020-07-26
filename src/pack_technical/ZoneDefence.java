package pack_technical;

import pack_1.Constants;
import pack_1.ParameterGatherAndSetter;
import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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


    //TODO fix this hardcoded path
    public PrintWriter writer14 = new PrintWriter("output/AttackingAndUpdatingTime.txt");

    public ZoneDefence(CollisionHandler collision, FlockManager flockManager, ParameterGatherAndSetter parameterGatherAndSetter) throws IOException {
        this.flockManager = flockManager;
        this.collisionHandler = collision;
        defenderBoids = GameManager.get_team(0);
        attackBoids = GameManager.get_team(1);
        if (Constants.PERFECT_WAYPOINTS) {
            patternHandler = new DummyPatternHandler();
        } else {
            patternHandler = new PatternHandler();
        }
        this.parameterGatherAndSetter = parameterGatherAndSetter;
        waypoints.addAll(parameterGatherAndSetter.returnDifficulty());
        patrollingScheme.getWaypointsA().add(Constants.TARGET.copy());
        patrollingScheme.setup();
    }


    public void run() {
        if (patternHandler.isOnce()) {
            //after sim constructor has completed is the point where the MCTS is running.
            enviromentalSimulation = new EnviromentalSimulation(defenderBoids, patternHandler.getNewpoints(), attackBoids, collisionHandler);
            if (Constants.PERFECT_AI) {
                parameterSimulation = new DummyParameterSimulation();
            } else {
                parameterSimulation = new ParameterSimulation(defenderBoids, patternHandler.getNewpoints(), enviromentalSimulation.getSimulator());
            }
            patternHandler.setOnce(false);
        }

        if (enviromentalSimulation != null) {
            if (parameterSimulation.observe(defenderBoids) == 1) {
                enviromentalSimulation.setAiToInnerSimulation(parameterSimulation.getAi());
                parameterGatherAndSetter.sendParameters(parameterSimulation.getAi());
                attack.set(true);
                writer14.write("I started to attack " + "," + Math.round((System.nanoTime() - startTime) / 1000000) + "," + counter + "\n");
                writer14.flush();
            }
        }

        for (BoidGeneric attackBoid : attackBoids) {
            //the placement of these counters assume only one attackboid
            //also the magic numbers arent great
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

            // ATACK MODE
            if (attack.get()) {
                attackBoid.setMovable(true);
                if(Constants.DEBUG_SIM_LIMIT != 0) {
                    while(enviromentalSimulation.simulations < enviromentalSimulation.maxSimulation) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                PVector attackVector = enviromentalSimulation.returnTargetVector(defenderBoids, attackBoids);
                attackBoid.updateAttack(attackVector);
            } else {
                attackBoid.setStationary();
            }
        }

        for (BoidGeneric defenderBoid : defenderBoids) {
            if (defend) {
                defenderBoid.update(patrollingScheme.patrol(defenderBoid.getLocation(), defenderBoid));
            } else {
                defenderBoid.setStationary();
            }
        }
        parameterGatherAndSetter.incrementIterations();
    }
}
