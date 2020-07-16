package pack_technical;

import processing.core.PVector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PatternImage {


    private final ArrayList<int[]> points = new ArrayList<>();
    private final ArrayList<int[]> newpoints = new ArrayList<>();


    public PatternImage() {
    }

    public ArrayList<int[]> simplify() {
        ArrayList<int[]> buffer = new ArrayList<>();
        for(int[] cord : this.points) {
            if(buffer.size()==3 ) {
                double degree = Math.toDegrees(Math.atan2(buffer.get(2)[0] - buffer.get(1)[0], buffer.get(2)[1] - buffer.get(1)[1]) -
                    Math.atan2(buffer.get(0)[0] - buffer.get(1)[0], buffer.get(0)[1] - buffer.get(1)[1]));

                // TODO if the amount is in degrees, why are we adding 2pi?!
                //degree+=(360);
                if (Math.abs(180-Math.abs(degree))>10) {
                    newpoints.add(buffer.get(2));
                }
                buffer.remove(0);
            }

            buffer.add(cord);
        }
        return newpoints;
    }


    public ArrayList<int[]> getPoints() {
        return points;
    }

    public void clearPoints() {
        this.points.clear();
    }

    public void addPoints(PVector point) {

    };

    public ArrayList<int[]> getNewpoints() {
        return newpoints;
    }

    public void clearMe(){
        points.clear();
    }
}
