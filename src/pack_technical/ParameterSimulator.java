package pack_technical;

import pack_AI.AI_type;
import pack_boids.BoidGeneric;

import java.util.ArrayList;

/**
 * Interface used to interact with an object which learns parameters.
 * Not complete in any way, right now just used to be able to pass perfect parameters
 * to ZoneDefence without modifcation of existing code
 *
 * to be renamed at a later date
 */
public interface ParameterSimulator {

    AI_type getAi();
    int observe(ArrayList<BoidGeneric> defenders);
}
