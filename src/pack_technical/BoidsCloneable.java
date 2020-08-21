package pack_technical;

import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;

import java.util.ArrayList;

public interface BoidsCloneable {
     static ArrayList<BoidGeneric> copyStateOfBoids(ArrayList<BoidGeneric> boids) {
        ArrayList<BoidGeneric> boidListClone = new ArrayList<>();

        for (BoidGeneric boid : boids) {
            BoidGeneric bi = new BoidStandard(boid);
            boidListClone.add(bi);
        }
        return boidListClone;
    }
}
