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
     * Set as an argument using (F|-o|--output) fileName
     * for example -o results
     * If ommited, results will not be written to file
     */
    public static String OUTPUT_FILE = null;

    /** Distance required for a 'hit' to be recognized */
    public static final int HIT_DISTANCE = 10;
    /** Square of Constants.HIT_DISTANCE */
    public static final int HIT_DISTANCE_SQ = HIT_DISTANCE * HIT_DISTANCE;

    // TODO Need to ensure the arguments are in range, throw IllegalArgumentException if not
    public static void setParamsFromProgramArgs(String[] args) {
        for(int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "T":
                case "-t":
                case "--target":
                    TARGET = new PVector(Float.parseFloat(args[++i]), Float.parseFloat(args[++i]));
                    break;
                case "F":
                case "-o":
                case "--out":
                    OUTPUT_FILE = args[++i];
                    break;
            }
        }
    }
}
