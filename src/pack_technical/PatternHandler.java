package pack_technical;

import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;

import pack_1.Constants;

public class PatternHandler {
    public ArrayList<PatternEntry> getObservations() {
        return observations;
    }

    public boolean isOnce() {
        return once;
    }

    public void setOnce(boolean once) {
        this.once = once;
    }

    //For testing Envriomental simulation once delete later
    private boolean once = false;


    ArrayList<PatternEntry> observations = new ArrayList<>();
    public float ERROR= 0.2f;

    public float getRadius() {
        return radius;
    }

    public float radius;

    public PatternImage getImg() {
        return img;
    }

    public PatternImage img = new PatternImage();


    public void newObservation(ArrayList<BoidGeneric> boids, int counter){
        if(counter%10==0) {
            PVector middleOfTheMass = boids.stream()
                    .map(boid -> boid.getLocation())
                    .reduce(new PVector(0,0), (a, b) -> PVector.add(a, b))
                    .div(boids.size());
            img.addPoint(middleOfTheMass);
            observations.add(new PatternEntry(middleOfTheMass));
        }
    }

    public int analyze() {

        if (observations.size() >= 150) {
            observations.clear();
        }
        if (observations.size() < 150 && observations.size() > 50) {
            PatternEntry circle = observations.get(0);
            PatternEntry base = new PatternEntry(Constants.TARGET);

            if(observations.size()==100) {
                img.simplify();
                img.clearPoints();
                //img.clearMe();
                radius = circle.difference(base);
                once=true;
                return 1;
            }
        }
        return 0;
    }

}
