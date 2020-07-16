package pack_technical;

import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import pack_1.Constants;

public class PatternHandler {

    private boolean once = false; //For testing Envriomental simulation once delete later
    private final List<PVector> points = new ArrayList<>();
    private final List<PVector> newpoints = new ArrayList<>();

    public boolean isOnce() {
        return once;
    }

    public void setOnce(boolean once) {
        this.once = once;
    }

    public void clearPoints() {
        this.points.clear();
    }

    public void addPoint(PVector point) {
        this.points.add(point);
    }

    public List<PVector> getNewpoints() {
        return newpoints;
    }

    public void newObservation(ArrayList<BoidGeneric> boids, int counter){
        if(counter%10==0) {
            PVector middleOfTheMass = boids.stream()
                    .map(boid -> boid.getLocation())
                    .reduce(new PVector(0,0), (a, b) -> PVector.add(a, b))
                    .div(boids.size());
            addPoint(middleOfTheMass);
        }
    }

    public int analyze() {
        if(points.size()==100) {
            simplify();
            clearPoints();
            once=true;
            return 1;
        }
        return 0;
    }

    public List<PVector> simplify() {
        List<PVector> buffer = new ArrayList<>();
        for(PVector cord : this.points) {
            if(buffer.size()==3 ) {
                double degree = Math.toDegrees(buffer.get(2).sub(buffer.get(1)).heading() - buffer.get(1).sub(buffer.get(0)).heading());
                // TODO if the amount is in degrees, why are we adding 2pi?!
                //degree+=(2*Math.PI);
                if (Math.abs(180-Math.abs(degree))>10) {
                    newpoints.add(buffer.get(2));
                }
                buffer.remove(0);
            }

            buffer.add(cord);
        }
        return newpoints;
    }
}

