package pack_technical;

import processing.core.PVector;

public class PatternEntry {
    private final PVector radius;

    public PatternEntry(PVector r){
        this.radius=r;
    }

    public PVector getRadius() {
        return radius;
    }

    public float difference(PatternEntry other){
        return PVector.dist(radius,other.getRadius());
    }

}
