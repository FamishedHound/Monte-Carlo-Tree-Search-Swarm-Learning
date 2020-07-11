package pack_technical;

import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

import pack_1.Utility;

public class PatrollingScheme {

    float waypointforce;
    private volatile ArrayList<PVector> waypoints = new ArrayList<>();
    private PVector currWaypointA = new PVector(0,0);
    private PVector currWaypoint = new PVector(0,0);
    public volatile Iterator<PVector> iterator;
    private Iterator<PVector> iteratorA;
    public volatile int currentPosition = 0;
    private final ArrayList<PVector> waypointsA = new ArrayList<>();

    public PatrollingScheme(float waypointforce){
        this.waypointforce=waypointforce;
    }

    public float getWaypointforce() {
        return waypointforce;
    }

    public void setWaypointforce(float waypointforce) {
        this.waypointforce = waypointforce;
    }

    public ArrayList<PVector> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<PVector> waypoints) {
        this.waypoints = waypoints;
    }

    public PVector getCurrWaypointA() {
        return currWaypointA;
    }

    public void setCurrWaypointA(PVector currWaypoint) {
        this.currWaypointA = currWaypoint;
    }

    public ArrayList<PVector> getWaypointsA() {
        return waypointsA;
    }

    public PVector getCurrWaypoint() {
        return currWaypoint;
    }

    public void setCurrWaypoint(PVector currWaypoint) {
        this.currWaypoint = currWaypoint;
    }

    public Iterator<PVector> getIterator() {
        return iterator;
    }

    public void setup(){
        iterator = waypoints.iterator();
        currWaypoint = iterator.next();

        currentPosition = 0;
    }

    public void restartIterator(){
        iterator = waypoints.iterator();
    }

    public void setIterator(Iterator<PVector> iterator) {
        this.iterator = iterator;
    }

    public PVector patrol(PVector location, BoidGeneric b){
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
