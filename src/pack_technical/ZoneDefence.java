package pack_technical;

import pack_1.Constants;
import pack_1.ParameterGatherAndSetter;
import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


//TODO: rename delay2

public class ZoneDefence implements Cloneable {

    public ArrayList<BoidGeneric> getDefenderBoids() {
        return defenderBoids;
    }

    private final boolean defend = true;
    private final ArrayList<BoidGeneric> defenderBoids;
    private final ArrayList<BoidGeneric> attackBoids;
    static int counter = 0;
    boolean flag = true;
    int DELAY = 200;
    int delay2 = 0;
    CollisionHandler collisionHandler;
    PatternHandler patternHandler;
    //timing simulation/real world
    long startTime = 0;
    private final PatrollingScheme patrollingScheme = new PatrollingScheme(0.04f);
    private final ArrayList<PVector> waypoints = patrollingScheme.getWaypoints();
    EnviromentalSimulation enviromentalSimulation;
    boolean attack = false;
    FlockManager flockManager;
    ParameterSimulation parameterSimulation;
    ParameterGatherAndSetter parameterGatherAndSetter;


    //TODO fix this hardcoded path
    public PrintWriter writer14 = new PrintWriter("output/AttackingAndUpdatingTime.txt");

    public ZoneDefence(CollisionHandler collision, FlockManager flockManager, ParameterGatherAndSetter parameterGatherAndSetter) throws IOException {
        this.flockManager = flockManager;
        this.collisionHandler = collision;
        defenderBoids = GameManager.get_team(0);
        attackBoids = GameManager.get_team(1);
        patternHandler = new PatternHandler();
        this.parameterGatherAndSetter = parameterGatherAndSetter;
        waypoints.addAll(parameterGatherAndSetter.returnDifficulty());
        patrollingScheme.getWaypointsA().add(Constants.TARGET.copy());
        patrollingScheme.setup();
    }


    public void run() {
        if (patternHandler.isOnce()) {
            //after sim constructor has completed is the point where the MCTS is running.
            enviromentalSimulation = new EnviromentalSimulation(defenderBoids, patternHandler.getImg().getNewpoints(), attackBoids, collisionHandler);
            parameterSimulation = new ParameterSimulation(defenderBoids, patternHandler.getImg().getNewpoints(), enviromentalSimulation.getSimulator());
            patternHandler.setOnce(false);
        }

        if (enviromentalSimulation != null) {
            if (parameterSimulation.observe(defenderBoids) == 1) {
                enviromentalSimulation.setAiToInnerSimulation(parameterSimulation.updateAi());
                parameterGatherAndSetter.sendParameters(parameterSimulation.updateAi());
                attack = true;
                writer14.write("I started to attack " + "," + Math.round((System.nanoTime() - startTime) / 1000000) + "," + counter + "\n");
                writer14.flush();
            }
        }

        for (BoidGeneric attackBoid : attackBoids) {
            counter++;
            if (counter >= DELAY / 8 && counter <= DELAY * 2) {
                if (!attack) attackBoid.setMovable(false);
                attackBoid.setStationary();
                delay2++;
            }

            if (delay2 >= 200) {
                patternHandler.newObservation(defenderBoids, counter);
                if (attackBoids != null && flag && patternHandler.analyze() == 1) {
                    flag = false;
                    startTime = System.nanoTime();
                }
            }

            // ATACK MODE
            if (attack) {
                attackBoid.setMovable(true);
                PVector attackVector = enviromentalSimulation.returnTargetVector();
                enviromentalSimulation.updateBoids(defenderBoids, attackBoids);
                attackBoid.update(attackVector);
            } else {
                attackBoid.setStationary();
            }
        }

        for (BoidGeneric defenderBoid : defenderBoids) {
            if (defend) {
                defenderBoid.update(patrollingScheme.patrol(defenderBoid.getLocation(), defenderBoid));
                //velocity.limit(1);
                //location.add(velocity.add(patrollingScheme.patrol(defenderBoid.getLocation(), defenderBoid)));
                //acceleration.mult(0);
            } else {
                defenderBoid.setStationary();
            }
        }
        parameterGatherAndSetter.incrementIterations();
    }
}
