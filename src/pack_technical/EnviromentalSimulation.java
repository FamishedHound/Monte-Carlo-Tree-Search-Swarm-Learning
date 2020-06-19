package pack_technical;

import pack_1.Launcher;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.Boid_generic;
import pack_boids.Boid_standard;
import processing.core.PApplet;
import processing.core.PVector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

//todo move maxTreeDepth to Constants

public class EnviromentalSimulation extends Thread {
    ArrayList<Boid_generic> defenders;
    ArrayList<Boid_generic> attackBoids;

    Tree MCT;

    AI_type simulator;
    PApplet parent;
    PatrollingScheme scheme;
    ArrayList<int[]> cords;

    FlockManager flock;
    double startTime = 0;
    int maxTreeDepth = 20;
    int actionCounter = 0;
    boolean treeReady = false;
    boolean dangerClose = false;

    CollisionHandler handler;

    public AI_type getSimulator() {
        return simulator;
    }


    public EnviromentalSimulation(int sns, int ans, int cns, double sw, double aw, double cw, String name, ArrayList<Boid_generic> defenders, PApplet parent, ArrayList<int[]> cords, ArrayList<Boid_generic> attackers, CollisionHandler handler) throws IOException {
        this.parent = parent;
        this.handler = handler;
        this.cords = cords;
        this.defenders = defenders;

        simulator = new AI_type(randFloat(AI_manager.neighbourhoodSeparation_lower_bound, AI_manager.neighbourhoodSeparation_upper_bound), 70, 70, 2.0, 1.2, 0.9f, 0.04f, "Simulator2000");

        defenders = copyTheStateOfAttackBoids(defenders);
        this.attackBoids = copyTheStateOfAttackBoids(attackers);

        this.flock = new FlockManager(parent, true, true);
        this.scheme = new PatrollingScheme(simulator.getWayPointForce());
        for (Boid_generic g : defenders) {
            g.setAi(simulator);
        }

        for (int[] cord : cords) {
            scheme.getWaypoints().add(new PVector(cord[0], cord[1]));
        }
        //FOLLOW THE SIMILLAR WAYPOINT AS DEFENDERS
        float shortestDistance = 3000;
        int counter = 0;
        int positionInTheList = 0;
        for (PVector checkpoint : scheme.getWaypoints()) {
            float distance = PVector.dist(defenders.get(0).getLocation(), checkpoint);
            counter++;
            if (distance < shortestDistance) {
                shortestDistance = distance;
                positionInTheList = counter;
            }
        }

        scheme.setup();

        for (int i = 0; i < positionInTheList + 1; i++) {
            if (!scheme.getIterator().hasNext()) {
                // if the end of the list of waypoints has been reached, reassigns the iterator
                // to scheme so it can begin from the beginning again
                scheme.setIterator(scheme.getWaypoints().iterator());
            }
            scheme.setCurrWaypoint(scheme.getIterator().next());
        }
        startTime = System.nanoTime();

        MCT = new Tree(maxTreeDepth);
        //the PVector would be a random vector, but for the root it is just 0.
        //TODO: abstract below line to Node constructor if it proves easy
        MCT.root.storeDetails(new PVector(0,0,0), attackBoids);
        new Thread(this).start();
    }


    public void setAiToInnerSimulation(AI_type t) {
        simulator = t;
    }


    public boolean isSimulating() {
        return true;
    }


    public static float randFloat(float min, float max) {
        Random rand = new Random();
        float result = rand.nextFloat() * (max - min) + min;
        return result;
    }


    public PVector reutrnTargetVecotr() {
        Node bestSim = MCT.bestAvgVal();
        PVector bestVector = bestSim.MrLeandroVector;
        try {
            MCT.root = new Node(0, "root", 0, 0);
            MCT.root.storeDetails(new PVector(0,0,0), attackBoids);
            dangerClose = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(actionCounter > 10){
            System.gc();
            System.runFinalization();
            actionCounter = 0;
        }else{
            actionCounter++;
        }


        return bestVector;
    }


    public void updateBoids(ArrayList<Boid_generic> defenders, ArrayList<Boid_generic> attacker) {
        this.defenders = copyTheStateOfAttackBoids(defenders);
        this.attackBoids = copyTheStateOfAttackBoids(attacker);
    }


    public void run() {
        while (true) {
            try {
                Node n = MCT.UCT(MCT.root);
                InnerSimulation newSim;
                if(n.parent == null){
                    newSim = new InnerSimulation(simulator, defenders, cords, attackBoids, handler, parent, n.depth);
                }else {
                    newSim = new InnerSimulation(simulator, defenders, cords, n.parent.attacker, handler, parent, n.depth);
                }
                newSim.run1();

                if(newSim.avgReward < 0){
                    dangerClose = true;
                }else{
                    dangerClose = false;
                }

                double simVal = 0;
                if (newSim.attackBoids.get(0).isHasFailed()) {
                    simVal = -1 ;
                } else if (newSim.victory) {
                    simVal = 1;
                } else {
                    if(!dangerClose) {
                        simVal = 0.5 - (newSim.currentDistance / 6000);
                    }
                }

                String nodeName = n.name + "." + n.children.size(); 
                n.addChild(simVal, nodeName, newSim.avgReward);
                n.children.get(n.children.size()-1).storeDetails(newSim.MrLeandroVector, newSim.attackBoids);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public ArrayList<Boid_generic> copyTheStateOfAttackBoids(ArrayList<Boid_generic> boids) {
        ArrayList<Boid_generic> boidListClone = new ArrayList<>();

        for (Boid_generic boid : boids) {
            Boid_generic bi = new Boid_standard(parent, boid.getLocation().x, boid.getLocation().y, 6, 10);
            bi.setAcceleration(boid.getAcceleration());
            bi.setVelocity(boid.getVelocity());
            bi.setLocation(boid.getLocation());
            boidListClone.add(bi);
        }
        return boidListClone;
    }
}
