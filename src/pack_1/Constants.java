package pack_1;

import processing.core.PVector;

public class Constants {

    //new params should be added as a public static field in Constants and a new "case"
    //block added to Constants.setParamsFromProgramArgs to parse the program arguments accordingly
    //the value can then be used in the code wherever desired.

    //E
    public static int EXAMPLE = 10;

    // T
    public static PVector TARGET = new PVector(550,500);

    public static final int HIT_DISTANCE = 10;
    public static final int HIT_DISTANCE_SQ = HIT_DISTANCE * HIT_DISTANCE;

    public static void setParamsFromProgramArgs(String[] args) {
        for(int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "E":
                case "-e":
                case "--example":
                    EXAMPLE = Integer.parseInt(args[++i]);
                    break;
                case "T":
                case "-t":
                case "--target":
                    TARGET = new PVector(Float.parseFloat(args[++i]), Float.parseFloat(args[++i]));
                    break;
            }
        }
    }

}
