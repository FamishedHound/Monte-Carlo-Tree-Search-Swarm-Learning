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

public class InnerSimulation  implements BoidsCloneable {


    private float currentDistanceToTarget;

    private boolean simulating = true;
    private BoidGeneric attacker;
    private AI_type simulation_ai;
    private PVector action;
    PApplet parent;
    ArrayList<BoidGeneric> defenderBoids;
    PatrollingScheme patrollingScheme;
    CollisionHandler collisionHandler;
    public InnerSimulation(PApplet parent, PatrollingScheme patrollingScheme,BoidGeneric attacker, ArrayList<BoidGeneric> defenderBoids, List<PVector> waypointCoords, CollisionHandler collisionHandler, PVector action, AI_type simulation_ai) {
        this.defenderBoids = defenderBoids;
        this.simulation_ai = simulation_ai;
        this.attacker = attacker;
        this.parent = parent;
        this.patrollingScheme=patrollingScheme;
        this.action = action;
        this.collisionHandler=collisionHandler;
    }



    public double rollout(PatrollingScheme patrollingSchemes) {
        //System.out.println("Attacker Start" + attacker.getLocation());
        for (int i = 0; i < 200; i++) {
            attacker.updateAttack(action);
            for (BoidGeneric defenderBoid : defenderBoids) {
                defenderBoid.update(patrollingSchemes.patrol(defenderBoid.getLocation(), defenderBoid));

                //  parent.fill(255,213,10);
                //  parent.rect(defenderBoid.getLocation().x, defenderBoid.getLocation().y,10f,10f);
                if (CollisionHandler.doesCollide(attacker, defenderBoid, 20)) {
                    return -1;
                }

            }
           // parent.fill(55,213,10);
            //action =  PVector.sub(Constants.TARGET, attacker.getLocation()).setMag(Constants.Boids.MAX_ACC_ATTACK);

            // parent.rect(attacker.getLocation().x, attacker.getLocation().y,10f,10f);
            if (CollisionHandler.doesReachTarget(attacker, 20)) {
                return 1;
            }


        }
        //System.out.println("Attacker End" + attacker.getLocation());
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