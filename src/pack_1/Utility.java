package pack_1;

import processing.core.PVector;

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
}