

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class AnalysisScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        int numberOfSimulations =3;
            Runtime rt = Runtime.getRuntime();

            int i=0;
        while(i<2) {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "java -jar C:\\Users\\LukePc\\Desktop\\japa\\BOIDSs\\src\\BOIDS.jar 10 10 20");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            p.wait();
            i++;
        }

}




}
