package pack_technical;

import pack_boids.Boid_generic;
import processing.core.PVector;

import java.util.ArrayList;

public class CollisionHandler {
    private static   GameManager manager;
    ArrayList<Boid_generic> team1;
    ArrayList<Boid_generic> team2;
    private final float mass=5;

    public boolean isLose() {
        return lose;
    }

    private boolean lose=false;

    public boolean isVictory() {
        return victory;
    }

    private boolean victory=false;

    public CollisionHandler(GameManager g){
        this.manager=g;
         team1 = manager.get_team(0);
         team2 = manager.get_team(1);
    }

    public boolean doesCollide(Boid_generic boid1,Boid_generic boid2){
        float d = PVector.dist(boid1.getLocation(),boid2.getLocation() );
        if(d<6){  //
         //   System.out.println("I COLLIDE" + boid1.getId());
            return true;
        }
        return false;
    }

    public void checkCollisions(){ //Elastic collisions

        for(Boid_generic b1 : team1){
            for (Boid_generic b2 : team2){
                if(doesCollide(b1,b2)){
//                    PVector differenceOfVelocity1 = PVector.sub(b1.getVelocity(),b2.getVelocity());
//                    PVector differenceOfCentres1 = PVector.sub(b1.getLocation(),b2.getLocation());
//                    PVector differenceOfVectors1 =PVector.sub(b1.getLocation(),b2.getLocation());
//
//                    float magnitudeSquare = differenceOfCentres1.magSq();
//                    //formula
//                    float con = mass*(differenceOfVelocity1.dot(differenceOfCentres1)/magnitudeSquare);
//
//                    PVector v1 = PVector.mult(differenceOfVectors1,con);
//
//              //      System.out.println(b1.getVelocity() + " before1");
//                    b1.getVelocity().add(v1);
//                //    System.out.println(b1.getVelocity() + " after1");
//                    PVector differenceOfVelocity2 = PVector.sub(b2.getVelocity(),b1.getVelocity());
//                    PVector differenceOfCentres2 = PVector.sub(b2.getLocation(),b1.getLocation());
//                    PVector differenceOfVectors2 =PVector.sub(b2.getLocation(),b1.getLocation());
//                    float magnitudeSquare2 = differenceOfCentres2.magSq();
//                    float con2 = mass*(differenceOfVelocity2.dot(differenceOfCentres2)/magnitudeSquare2);
//
//                    PVector v2 = PVector.mult(differenceOfVectors2,con2);
//
//                   // System.out.println(b2.getVelocity() + " before2");
//                    b2.getVelocity().add(v2);
//                   // System.out.println(b2.getVelocity() + " after2");
                    lose=true;
                } else if(PVector.dist(b2.getLocation(),new PVector(550,500f))<=10){
                    victory=true;

                }
            }
        }



    }

}
