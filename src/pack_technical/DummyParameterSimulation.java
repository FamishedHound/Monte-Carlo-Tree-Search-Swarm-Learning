package pack_technical;

import pack_1.Constants;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;

import java.util.ArrayList;

/**
 * This class is to be used for debugging. It performs no learning,
 * returns the appropriate codes in its functions to pass checks in
 * ZoneDefence and when asked will return the perfect AI_type.
 */
public class DummyParameterSimulation implements ParameterSimulator {
    AI_type currentAi = Constants.CORRECT_AI_PARAMS;

    @Override
    public AI_type getAi(){
        return currentAi;
    }

    @Override
    public int observe(ArrayList<BoidGeneric> defenders) {
        return 1;
    }

}
