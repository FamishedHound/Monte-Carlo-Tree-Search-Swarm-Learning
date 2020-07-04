package pack_technical;

import pack_boids.Boid_generic;

import java.util.ArrayList;

import pack_1.Constants;
import pack_1.Utility;

public class CollisionHandler {
    ArrayList<Boid_generic> team1;
    ArrayList<Boid_generic> team2;
    // TODO Can this be removed?
    private final float mass=5;

    public boolean isLose() {
        return lose;
    }

    private boolean lose=false;

    public boolean isVictory() {
        return victory;
    }

    private boolean victory=false;

    public CollisionHandler(){
        team1 = GameManager.get_team(0);
        team2 = GameManager.get_team(1);
    }

    public boolean doesCollide(Boid_generic boid1,Boid_generic boid2){
        float d = Utility.distSq(boid1.getLocation(),boid2.getLocation());
        return d < ((boid1.getSize() + boid2.getSize()) * (boid1.getSize() + boid2.getSize()));
    }

    public void checkCollisions(){ //Elastic collisions

        for(Boid_generic b1 : team1){
            for (Boid_generic b2 : team2){
                if(doesCollide(b1,b2)){
                    lose=true;
                } else if(Utility.distSq(b2.getLocation(), Constants.TARGET) <= Constants.HIT_DISTANCE_SQ) {
                    victory=true;
                }
            }
        }
    }
}
