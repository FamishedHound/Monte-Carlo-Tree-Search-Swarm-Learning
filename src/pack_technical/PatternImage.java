package pack_technical;

import processing.core.PVector;

import java.util.List;
import java.util.ArrayList;

public class PatternImage {


    private final List<PVector> points = new ArrayList<>();
    private final List<PVector> newpoints = new ArrayList<>();


    public PatternImage() {
    }

    public List<PVector> simplify() {
        List<PVector> buffer = new ArrayList<>();
        for(PVector cord : this.points) {
            if(buffer.size()==3 ) {
                double degree = Math.toDegrees(buffer.get(2).sub(buffer.get(1)).heading() - buffer.get(1).sub(buffer.get(0)).heading());
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


    public List<PVector> getPoints() {
        return points;
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
}
