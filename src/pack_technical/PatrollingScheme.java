package pack_technical;

import pack_boids.Boid_generic;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

import pack_1.Utility;

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

    private final ArrayList<PVector> waypointsA = new ArrayList<>();
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
        currWaypoint = waypoints.get(currentPosition);//iterator.next();
        // TODO Magic Numbers!!
        if(Utility.distSq(location,currWaypoint) <= 5 * 5) { // was 2
            currentPosition = (currentPosition + 1) % waypoints.size();
        }

        currWaypoint = waypoints.get(currentPosition);//iterator.next();

        PVector target = PVector.sub(currWaypoint,b.getLocation());
        target.setMag(waypointforce); // was 0.03
        return target;
    }
}
