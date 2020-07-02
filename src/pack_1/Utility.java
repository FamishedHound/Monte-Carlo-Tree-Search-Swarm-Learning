package pack_1;

import processing.core.PVector;
import java.util.Random;

public class Utility {

    /**
     * Gets the square distance between two PVectors. For some reason this
     * doesn't seem to exist in the library
     * @param vector1 First PVector
     * @param vector2 Second PVector
     * @return The distance between the two PVectors
     */
    public static float distSq(PVector vector1, PVector vector2) {
        return (PVector.sub(vector1, vector2).magSq());
    }

    /**
     * Gets a random float between min and max
     * @param min Lower bound of the random float to generate
     * @param max Upper bound of the random float to generate
     * @return A random float between min and max
     */
    public static float randFloat(float min, float max) {
        return (new Random().nextFloat() * (max - min)) + min;
    }
}