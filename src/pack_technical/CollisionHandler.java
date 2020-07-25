package pack_technical;

import pack_boids.BoidGeneric;

import java.util.ArrayList;

import pack_1.Constants;
import pack_1.Utility;


//todo feel like CollisionHandler probably needs a refactor at some point so it can be used generically and not just for the main game
public class CollisionHandler {
    ArrayList<BoidGeneric> team1;
    ArrayList<BoidGeneric> team2;
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

    public static boolean doesCollide(BoidGeneric boid1, BoidGeneric boid2, float buffer) {
        float d = Utility.distSq(boid1.getLocation(),boid2.getLocation());
        return d < ((boid1.getSize() + boid2.getSize() + buffer) * (boid1.getSize() + boid2.getSize()) + buffer);
    }


    public static boolean doesReachTarget(BoidGeneric attackBoid, float buffer) {
        return (Utility.distSq(attackBoid.getLocation(), Constants.TARGET) <= (Constants.HIT_DISTANCE + buffer) * (Constants.HIT_DISTANCE + buffer)) ? true : false;
    }

    public static boolean checkCollisions(BoidGeneric attackBoid, ArrayList<BoidGeneric> defenderBoids, float buffer) {
        for (BoidGeneric defenderBoid : defenderBoids){
            if(doesCollide(attackBoid, defenderBoid, buffer)){
                return true;
            }
        }
        return false;
    }

    public void checkCollisions(){ //Elastic collisions

        for(BoidGeneric b1 : team1){
            for (BoidGeneric b2 : team2){
                if(doesCollide(b1,b2, 0)){
                    lose=true;
                } else if(Utility.distSq(b2.getLocation(), Constants.TARGET) <= Constants.HIT_DISTANCE_SQ) {
                    victory=true;
                }
            }
        }
    }
}
