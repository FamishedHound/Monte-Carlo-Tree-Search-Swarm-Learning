package pack_technical;

import pack_boids.Boid_generic;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

public class PatrollingScheme {
    public float getWaypointforce() {
        return waypointforce;
    }

    public void setWaypointforce(float waypointforce) {
        this.waypointforce = waypointforce;
    }

    float waypointforce;

    public PatrollingScheme(float waypointforce){
        this.waypointforce=waypointforce;
    }
    public ArrayList<PVector> getWaypoints() {
        return waypoints;
    }
    private PVector currWaypoint = new PVector(0,0);

    public void setCurrWaypointA(PVector currWaypoint) {
        this.currWaypointA = currWaypoint;
    }

    public PVector getCurrWaypointA() {
        return currWaypointA;
    }

    private PVector currWaypointA = new PVector(0,0);
    private ArrayList<PVector> waypoints = new ArrayList<>();

    public ArrayList<PVector> getWaypointsA() {
        return waypointsA;
    }

    private ArrayList<PVector> waypointsA = new ArrayList<>();
    public Iterator<PVector> iterator;
    private Iterator<PVector> iteratorA;
    public int currentPosition = 0;

    public PVector getCurrWaypoint() {
        return currWaypoint;
    }

    public Iterator<PVector> getIterator() {
        return iterator;
    }

    public void setCurrWaypoint(PVector currWaypoint) {
        this.currWaypoint = currWaypoint;
    }

    public void setup(){
        iterator = waypoints.iterator();
        currWaypoint = iterator.next();

        currentPosition = 0;
        //iteratorA =waypoints.iterator();
        //currWaypointA=new PVector(550,500);

    }
    public void copy(){

    }

    public void setWaypoints(ArrayList<PVector> waypoints) {
        this.waypoints = waypoints;
    }

    public void restartIterator(){
        iterator = waypoints.iterator();
    }

    public void setIterator(Iterator<PVector> iterator) {
        this.iterator = iterator;
    }

    public PVector patrol(PVector location, Boid_generic b){

        /*if (!iterator.hasNext()){   // the ! is important
            iterator = waypoints.iterator();

            currentPosition = 0;
        }*/
        currWaypoint = waypoints.get(currentPosition);//iterator.next();
        if(Math.abs(PVector.dist(location,currWaypoint))<=5) { // was 2
            currentPosition = (currentPosition + 1) % waypoints.size();
        }

        currWaypoint = waypoints.get(currentPosition);//iterator.next();

        PVector targer = PVector.sub(currWaypoint,b.getLocation());
        targer.setMag(waypointforce); // was 0.03
        return targer;



    }

    public PVector attacking(PVector location, Boid_generic b){
        if(currWaypointA!=new PVector(550,500) && PVector.dist(b.getLocation(),currWaypointA)<=10){
            currWaypointA=new PVector(550,500);

        }
        PVector targer = PVector.sub(currWaypointA,b.getLocation());
        targer.setMag(0.09f);
        return targer;
    }
}
