package pack_1;

import pack_technical.*;
import processing.core.PApplet;

public class Constants {

    //public static final

    public static Object setToDefaultOrInputArg(String inputArgName, Object defaultValue, String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(inputArgName == args[i]) {
                return args[i+1];
            }
        }
        return defaultValue;
    }
}
