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

    public ArrayList<BoidGeneric> getBoids() {
        return boids;
    }

    private final boolean defend = true;
    private final ArrayList<BoidGeneric> boids;
    private final ArrayList<BoidGeneric> attackBoids;
    static int counter = 0;
    boolean flag = true;
    int DELAY = 200;
    int delay2 = 0;
    CollisionHandler handler;
    PatternHandler pattern;
    //timing simulation/real world
    float time = 0;
    long startTime = 0;
    float circumfence;
    private final PatrollingScheme patrolling = new PatrollingScheme(0.04f);
    private final ArrayList<PVector> waypoints = patrolling.getWaypoints();
    EnviromentalSimulation sim;
    boolean attack = false;
    FlockManager flock;
    int timer = 0;
    ParameterSimulation param;
    ParameterGatherAndSetter output;


    //TODO fix this hardcoded path
    public PrintWriter writer14 = new PrintWriter("output/AttackingAndUpdatingTime.txt");

    public ZoneDefence(CollisionHandler collision, FlockManager flock, ParameterGatherAndSetter output) throws IOException {
        this.flock = flock;
        this.handler = collision;
        boids = GameManager.get_team(0);
        attackBoids = GameManager.get_team(1);
        pattern = new PatternHandler();
        this.output = output;
        waypoints.addAll(output.returnDifficulty());
        patrolling.getWaypointsA().add(Constants.TARGET.copy());
        patrolling.setup();
    }


    public void run() {
        if (pattern.isOnce()) {
            //after sim constructor has completed is the point where the MCTS is running.
            sim = new EnviromentalSimulation(boids, pattern.getImg().getNewpoints(), attackBoids, handler, Constants.DEBUG_SIM_LIMIT);
            param = new ParameterSimulation(boids, pattern.getImg().getNewpoints(), sim.getSimulator());
            pattern.setOnce(false);
        }

        if (sim != null) {
            if (param.observe(boids) == 1) {
                sim.setAiToInnerSimulation(param.updateAi());
                output.sendParameters(param.updateAi());
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
                pattern.newObservation(boids, counter);
                if (attackBoids != null && flag && pattern.analyze() == 1) {
                    circumfence = (float) (Math.PI * 2 * pattern.getRadius());
                    time = (circumfence / boids.get(0).getVelocity().mag());
                    System.out.println(boids.get(0).getVelocity().mag() + "   " + circumfence + "   " + time + "  " + (float) startTime);
                    flag = false;
                    startTime = System.nanoTime();
                }
            }

            // ATACK MODE
            if (attack) {
                attackBoid.setMovable(true);
                PVector attackVector;
                do {
                    attackVector = sim.returnTargetVector(boids, attackBoids);
                } while (attackVector == null);
                attackBoid.update(attackVector);
            } else {
                attackBoid.setStationary();
            }
        }

        for (BoidGeneric defenderBoid : boids) {
            if (defend) {
                PVector acceleration = defenderBoid.getAcceleration();
                PVector velocity = defenderBoid.getVelocity();
                //PVector velocity = new PVector(0,0);
                PVector location = defenderBoid.getLocation();
                velocity.limit(1);
                location.add(velocity.add(patrolling.patrol(defenderBoid.getLocation(), defenderBoid)));
                acceleration.mult(0);
            } else {
                defenderBoid.setStationary();
            }
        }
        output.incrementIterations();
    }
}
