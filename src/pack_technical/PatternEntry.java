package pack_technical;

import processing.core.PVector;

public class PatternEntry {
    public PVector getRadius() {
        return radius;
    }

    private PVector radius;
    public PatternEntry(PVector r){
        this.radius=r;
    }

    public float difference(PatternEntry other){
        float diff = PVector.dist(radius,other.getRadius());

        return diff;
    }

}
