package pack_technical;

import pack_boids.Boid_generic;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    private int x;
    private int y;
    private PApplet parent;
    private ZoneDefence zone;
    ArrayList<Boid_generic> clones;
    CollisionHandler handler;
    private boolean success=true;

    public void setMode(int mode) {
        this.mode = mode;
    }

    private int mode;
    PVector target ;
    PVector direction = new PVector();
    Random rand = new Random();
    PatrollingScheme scheme;
    enum ACTION{

    }
    public Simulation(ArrayList<Boid_generic> clones, PApplet p, ZoneDefence z,CollisionHandler handler,PatrollingScheme scheme) {
        this.handler=handler;
        this.scheme=scheme;
        this.x = x;
        this.y = y;
        this.parent = p;
        this.zone = z;
        this.clones = clones;
        this.mode=0;
        direction= new PVector(550,500);
    }


    public int getMode() {
        return mode;
    }

    public PVector getDirection() {
        return direction;
    }

    public void simulate() {

        //System.out.println(clones +"af");
        //System.out.println(clones);

        for (Boid_generic b : clones) {
            PVector acceleration = b.getAcceleration();
            PVector velocity = b.getVelocity();
            PVector location = b.getLocation();
            for (Boid_generic c :zone.getBoids()){
                if(handler.doesCollide(b,c)) {
                   // System.out.println("WE COLLIDED");
                    success = false;
                    PVector newDirection = new PVector(0,0);
                    int ran1 = rand.nextInt(600)+1;
                    int ran2 = rand.nextInt(600)+1;
                    for(Boid_generic bc:clones){


                        bc.setLocation(new PVector(location.x+ran1,location.y-ran2));
                        newDirection=new PVector(location.x+ran1,location.y-ran2);
                        scheme.setCurrWaypointA(new PVector(location.x+ran1,location.y-ran2));
                    }
                    direction=newDirection;
                    mode =1;
                    success=true;

                }
            }

            //System.out.println(b.getLocation());
            //b.move(clones);
            //b.update();
            target = zone.attack(b,2);
            PVector turnLeft = new PVector(location.x+20,location.y-100);

            velocity.limit(1);

            location.add(velocity.add(acceleration.add(target)));

            acceleration.mult(0);
            //parent.fill(105, 105, 105);
            // parent.shape(new PShape(2),b.getLocation().x,b.getLocation().y);
            if(success)
           // parent.ellipse(location.x, location.y, 10f, 10f);
            if(location== new PVector(550,500)){
                clones=null;
            }

        }
    }
}
