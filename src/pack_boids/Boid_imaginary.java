package pack_boids;

import pack_1.Launcher;

import java.util.ArrayList;
import processing.core.PConstants;

/*
 * a fake boid running inside the 'heads' of existing boids,
*/
public class Boid_imaginary extends Boid_generic {

    // technically always a boid standard
    Boid_generic original; // the real boid that this fake one imitates

    public Boid_imaginary(float x, float y, int t, Boid_generic b) {
        super(x, y, t, b.getId());
        original = b;
    }

    public Boid_generic getOriginal() {
        return original;
    }

    @Override
    public void run(ArrayList<Boid_generic> boids, boolean real_step, boolean simulation) {
        if (boids.get(0).isMoveable()) {
            record_history();
            isalone = true; // is boid uninteracted with?
            move(boids); // unsets isalone if interacted with
            record_acceleration();
            update();
        }
        if(!simulation && real_step) {
            render_trails(1,simulation);
            render();
        }
    }

    // TODO - Boids shouldn't render. They don't apriori know anything about the applet
    @Override
    public void render() {
        Launcher.applet.fill(fillcol.getRGB());
        Launcher.applet.stroke(linecol.getRGB());
        Launcher.applet.pushMatrix();
        Launcher.applet.translate(location.x, location.y);
        Launcher.applet.rotate(velocity.heading());
        Launcher.applet.beginShape(PConstants.TRIANGLES);
        Launcher.applet.vertex(size, 0);
        Launcher.applet.vertex(-size, size / 2);
        Launcher.applet.vertex(-size, -size / 2);
        Launcher.applet.endShape();
        Launcher.applet.popMatrix();
    }
}
