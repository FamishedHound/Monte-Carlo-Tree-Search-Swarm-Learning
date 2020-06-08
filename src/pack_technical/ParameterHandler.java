package pack_technical;

import processing.core.PVector;

public class ParameterHandler {
    static PVector currentSum = new PVector(0,0);

    public static PVector[] getEstimations() {
        return estimations;
    }

    public static PVector[] estimations = new PVector[3];
    public void createResultant(PVector sep, PVector ali, PVector coh){
        currentSum = PVector.add(sep,PVector.add(ali,coh));
        estimations[0]=sep;
        estimations[1]=ali;
        estimations[2]=coh;



    }
    public PVector getCurrentResultant(){
        return currentSum;
    }
}
