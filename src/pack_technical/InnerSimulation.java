package pack_technical;

import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PApplet;
import processing.core.PVector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pack_1.Constants;

//todo: would be nice to have relevant getters be fail or be otherwise hidden if InnerSimulation.run has not been ran

public class InnerSimulation extends Simulation implements BoidsCloneable {

    private boolean victory = false;
    private boolean nodeExpanded;
    private Random random = new Random();

    private PVector accelerationAction;
    private float closestDistanceToTarget;
    private float currentDistanceToTarget;
    private double rolloutReward;
    private int nodeDepth;
    private boolean simulating = true;
    private BoidGeneric attacker;
    private AI_type simulation_ai;
    private PVector action;



    public InnerSimulation(BoidGeneric attacker, ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, CollisionHandler collisionHandler, PVector action, AI_type simulation_ai) {
        super(defenderBoids, waypointCoords, attacker, collisionHandler, simulation_ai);
        this.simulation_ai = simulation_ai;
        this.attacker = attacker;


        this.action = action;
    }



    public double rollout() {

        for (int j = 0; j < 100; j++) {

            if (CollisionHandler.doesReachTarget(attacker, 0)) {
                return 1;
            }
            attacker.updateAttack(action);
            for (BoidGeneric defenderBoid : defenderBoids) {
                defenderBoid.move(defenderBoids);
                defenderBoid.update();
                if (CollisionHandler.doesCollide(attacker, defenderBoid, 0)) {
                    return -1;
                }

            }
        }
        currentDistanceToTarget = PVector.dist(attacker.getLocation(), Constants.TARGET);
        return 0.5 - (currentDistanceToTarget / 6000);
    }

    public BoidGeneric getAttackerState() {
        return new BoidStandard(attacker);
    }

    public ArrayList<BoidGeneric> getDefendersState() {
        return BoidsCloneable.copyStateOfBoids(defenderBoids);
    }


}