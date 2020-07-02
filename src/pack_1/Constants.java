package pack_1;

import processing.core.PVector;

public class Constants {

    //new params should be added as a public static field in Constants and a new "case"
    //block added to Constants.setParamsFromProgramArgs to parse the program arguments accordingly
    //the value can then be used in the code wherever desired.

    /**
     * The target for the attack boids.
     * Default to (550, 500). Set as an argument using
     * (T|-t|--target) x y, for example -t 400 300
     */
    public static PVector TARGET = new PVector(550,500);

    /**
     * File to output results to
     * Set as an argument using (-o|--output) fileName
     * for example -o results
     * If ommited, results will not be written to file
     */
    public static String OUTPUT_FILE = null;

    /** Distance required for a 'hit' to be recognized */
    public static final int HIT_DISTANCE = 10;
    /** Square of Constants.HIT_DISTANCE */
    public static final int HIT_DISTANCE_SQ = HIT_DISTANCE * HIT_DISTANCE;

    /** Distance required to count as collision between boids and its square */
    public static final float COLLISION_DISTANCE = 6;
    public static final float COLLISION_DISTANCE_SQ = COLLISION_DISTANCE * COLLISION_DISTANCE;

    public static class Boids {
        public static final float MAX_STEER = 0.02f;
        public static final float MAX_SPEED = 3.0f;
        public static final float MAX_SPEED_ATTACK = 1f;
        public static final float SIZE = 6.0f;
    }


    public static void setParamsFromProgramArgs(String[] args) throws IllegalArgumentException {
        for(int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "-t":
                case "--target":
                    if(i + 2 >= args.length) {
                        throw new IllegalArgumentException("Command line argument --target requires two parameters, x, and y, for the starting position");
                    }
                    TARGET = new PVector(Float.parseFloat(args[++i]), Float.parseFloat(args[++i]));
                    break;
                case "-o":
                case "--output":
                    if(i + 1 >= args.length) {
                        throw new IllegalArgumentException("Command line argument --out requires one parameter, the file name to output to");
                    }
                    OUTPUT_FILE = args[++i];
                    break;
            }
        }
    }
}
