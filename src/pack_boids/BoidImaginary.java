package pack_boids;

import java.util.List;

/**
 * a fake boid running inside the 'heads' of existing boids,
*/
public class BoidImaginary extends BoidGeneric {

    // technically always a boid standard
    BoidGeneric original; // the real boid that this fake one imitates

    public BoidImaginary(float x, float y, int t, BoidGeneric b) {
        super(x, y, t, b.getId());
        original = b;
    }

    public BoidGeneric getOriginal() {
        return original;
    }

    @Override
    public void run(List<BoidGeneric> boids, boolean simulation) {
        if (boids.get(0).isMoveable()) {
            recordHistory();
            isAlone = true; // is boid uninteracted with?
            move(boids); // unsets isalone if interacted with
            recordAcceleration();
            update();
        }
    }

}
