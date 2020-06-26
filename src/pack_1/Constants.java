package pack_1;

import processing.core.PVector;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {

    //new params should be added as a public static field in Constants and a new "else if"
    //block added to Constants.setParamsFromProgramArgs to parse the program arguments accordingly
    //the value can then be used in the code wherever desired.

    //E
    public static int EXAMPLE = 10;

    public static void setParamsFromProgramArgs(String[] args) {
        String arg;
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("E")) {
                EXAMPLE = Integer.parseInt(args[i+1]);
            }
        }
    }

}
