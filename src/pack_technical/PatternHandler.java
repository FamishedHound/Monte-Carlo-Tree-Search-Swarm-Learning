package pack_technical;

import pack_boids.Boid_generic;
import processing.core.PVector;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

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


    public void newObservation(ArrayList<Boid_generic> boids,int coutner){
        if(coutner%10==0) {
            PVector SumOfTheMasses = new PVector(0, 0);
            int counter = 0;
            for (Boid_generic b : boids) {
                SumOfTheMasses = PVector.add(SumOfTheMasses, b.getLocation());

                counter++;
            }
            PVector MiddleOfTheMass = PVector.div(SumOfTheMasses, counter);
            img.getPoints().add(new int[]{(int)MiddleOfTheMass.x,(int)MiddleOfTheMass.y});
            observations.add(new PatternEntry(MiddleOfTheMass));
        }


    }

    public int  analyze() throws IOException {

        if (observations.size() >= 150) observations.clear();
        if (observations.size() < 150 && observations.size() > 50) {
            PatternEntry circle = observations.get(0);
            PVector base = new PVector(550,500);
            float exact = PVector.dist(circle.getRadius(), new PVector(150, 500));

            for (PatternEntry entry : observations) {

                float error = Math.abs(entry.difference(new PatternEntry(base)) / circle.difference(new PatternEntry(base)));

                //System.out.println(entry.difference(new PatternEntry(base)) + "   " + exact);
                //System.out.println(error + " " + (ERROR));
                //Circle error no more errors
                /*if  (Math.abs(error - 1) > ERROR) {
                    //System.out.println("error allowance" + ERROR + " Error real " + error);
                    img.clearMe();
                    return 0;
                }*/
            }
            // when to draw
            if(observations.size()==100) {
                img.drawPattern();
                //img.clearMe();
                radius = circle.difference(new PatternEntry(base));
                once=true;
                return 1;
            }
        }
        return 0;
    }

}
