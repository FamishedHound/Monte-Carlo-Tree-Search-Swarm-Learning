package pack_technical;

import pack_AI.AI_type;
import pack_boids.Boid_generic;
import pack_boids.Boid_standard;
import processing.core.PApplet;

import java.util.ArrayList;

public abstract class Simulation {

    PApplet parent;
    ArrayList<Boid_generic> defenderBoids;
    ArrayList<Boid_generic> attackBoids;
    AI_type ai_type;
    PatrollingScheme patrollingScheme;
    CollisionHandler collisionHandler;
    ArrayList<int[]> cords;

    public ArrayList<Boid_generic> copyStateOfBoids(ArrayList<Boid_generic> boids) {
        ArrayList<Boid_generic> boidListClone = new ArrayList<>();

        for (Boid_generic boid : boids) {
            Boid_generic bi = new Boid_standard(boid.getLocation().x, boid.getLocation().y, 6, 10);
            bi.setAcceleration(boid.getAcceleration());
            bi.setVelocity(boid.getVelocity());
            bi.setLocation(boid.getLocation());
            boidListClone.add(bi);
        }
        return boidListClone;
    }

    public AI_type getSimulator() {
        return ai_type;
    }



}