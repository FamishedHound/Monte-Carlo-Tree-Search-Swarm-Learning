package pack_technical;

import pack_boids.BoidGeneric;

import java.util.ArrayList;

import pack_1.Constants;
import pack_1.Utility;

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

    public static boolean doesCollide(ArrayList<BoidGeneric> attackBoids, ArrayList<BoidGeneric> defenderBoids, float buffer) {
        for(BoidGeneric attackBoid : attackBoids){
            for (BoidGeneric defenderBoid : defenderBoids){
                if(doesCollide(attackBoid, defenderBoid, buffer)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean doesReachTarget(ArrayList<BoidGeneric> attackBoids, float buffer) {
        for(BoidGeneric attackBoid : attackBoids){
            if((Utility.distSq(attackBoid.getLocation(), Constants.TARGET) <= (Constants.HIT_DISTANCE + buffer) * (Constants.HIT_DISTANCE + buffer))) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesCollide(BoidGeneric boid1, BoidGeneric boid2){
        float d = Utility.distSq(boid1.getLocation(),boid2.getLocation());
        return d < ((boid1.getSize() + boid2.getSize()) * (boid1.getSize() + boid2.getSize()));
    }

    public static boolean doesCollide(BoidGeneric boid1, BoidGeneric boid2, float buffer){
        float d = Utility.distSq(boid1.getLocation(),boid2.getLocation());
        return d < ((boid1.getSize() + boid2.getSize() + buffer) * (boid1.getSize() + boid2.getSize()) + buffer);
    }

    public void checkCollisions(){ //Elastic collisions

        for(BoidGeneric b1 : team1){
            for (BoidGeneric b2 : team2){
                if(doesCollide(b1,b2)){
                    lose=true;
                } else if(Utility.distSq(b2.getLocation(), Constants.TARGET) <= Constants.HIT_DISTANCE_SQ) {
                    victory=true;
                }
            }
        }
    }
}
