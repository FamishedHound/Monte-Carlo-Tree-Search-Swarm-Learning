package pack_1;

import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_technical.CollisionHandler;
import pack_technical.GameManager;
import processing.core.PVector;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ParameterGatherAndSetter {

    CollisionHandler col;
    private long startTime;
    private long startTimeWithoutwait = -1;
    ArrayList<String> history_of_learning = new ArrayList<>();
    public int iterations = 0;
    private PVector attackerStartPosition;
    ArrayList<PVector> hard = new ArrayList<>();
    ArrayList<PVector> medium = new ArrayList<>();
    ArrayList<PVector> easy = new ArrayList<>();
    int amountOfBoids=0;
    String difficulty;

    public ParameterGatherAndSetter(GameManager game, CollisionHandler col, String[] args) throws IllegalArgumentException {
        if(args.length < 4) {
            throw new IllegalArgumentException("args must be of length at least 4");
        }
        this.col=col;

        this.attackerStartPosition = new PVector(Float.parseFloat(args[0]), Float.parseFloat(args[1]));
        this.difficulty=args[2];
        this.amountOfBoids=Integer.parseInt(args[3]);

        Constants.setParamsFromProgramArgs(Arrays.copyOfRange(args, 4, args.length));

        this.startTime=System.nanoTime();
        game.spawn_boids(0,amountOfBoids,PVector.add(Constants.TARGET, new PVector(-100, 10)));
        game.spawn_boids(1,1,this.attackerStartPosition);
        //game.spawn_boids(1,1,new PVector(1200,510));

        createDifficulties();
    }

    public void createDifficulties(){
        // Old settings
        medium.add(PVector.add(Constants.TARGET, new PVector(-100, 50)));
        medium.add(PVector.add(Constants.TARGET, new PVector(100, 0)));
        medium.add(PVector.add(Constants.TARGET, new PVector(-100, -95)));

        hard.add(PVector.add(Constants.TARGET, new PVector(0, -15)));
        hard.add(PVector.add(Constants.TARGET, new PVector(0, 15)));
        /*
        hard.add(new PVector(20,20));
        hard.add(new PVector(1400,20));
        hard.add(new PVector(1400,900));
        hard.add(new PVector(20,900));
        hard.add(new PVector(500,500));
         */

        easy.add(PVector.add(Constants.TARGET, new PVector(-100, 50))); // 550-100, 500+50
        easy.add(PVector.add(Constants.TARGET, new PVector(200, 0))); // 550+200, 500+0
        easy.add(PVector.add(Constants.TARGET, new PVector(-100, -95))); // 550-100, 500-95
        easy.add(PVector.add(Constants.TARGET, new PVector(40, -195))); // 550+40,  500-195


        // New settings
        /*medium.add(new PVector(530,525));
        medium.add(new PVector(730,425));
        medium.add(new PVector(530,330));

        hard.add(new PVector(530,500));
        hard.add(new PVector(530,405));

        easy.add(new PVector(450,600));
        easy.add(new PVector(650,500));
        easy.add(new PVector(450,405));
        easy.add(new PVector(590,305));*/
    }

    public ArrayList<PVector> returnDifficulty() {
        if(difficulty.equals("hard")){
            return hard;
        }
        if(difficulty.equals("medium")){
            return medium;
        }

        return easy;
    }

    public void gather() {
        try {
            if(col.isLose()){
                generateEndingStatement(0);
                var finalMessage = "Simulation took " + Math.round((System.nanoTime()-startTime)/1000000000) + " s and was a failure";
                Launcher.quit(finalMessage, 0);
            } else if(col.isVictory()){
                generateEndingStatement(1);
                var finalMessage = "Simulation took " + Math.round((System.nanoTime()-startTime)/1000000000) + " s and was a victory";
                Launcher.quit(finalMessage, 0);
            } else if(Math.round((System.nanoTime()-startTime)/1000000000)==300){//timeout after 300 s
                generateEndingStatement(2);
                var finalMessage = "Timeout";
                Launcher.quit(finalMessage, 1);
            }
        } catch(IOException e) {
            Launcher.quit("Failure to write ending statement", 1);
        }
    }

    public void sendParameters(AI_type currentAi) {
        if(startTimeWithoutwait < 0){
            startTimeWithoutwait=System.nanoTime();
        }
        history_of_learning.add(currentAi.getSep_neighbourhood_size() + "," + currentAi.getAli_neighbourhood_size() + "," + currentAi.getCoh_neighbourhood_size() + "," + currentAi.getSep_weight()  + "," + currentAi.getAli_weight() + "," + currentAi.getCoh_weight() + "," +Math.pow(currentAi.getSep_neighbourhood_size()-30,2)+","+Math.pow(currentAi.getAli_neighbourhood_size()-70,2) + "," + Math.pow(currentAi.getCoh_neighbourhood_size()-70,2) + "," + Math.pow(currentAi.getSep_weight()-2,2) + "," + Math.pow(currentAi.getAli_weight()-1.2,2)  + "," + Math.pow(currentAi.getCoh_weight()-0.9f,2) +  "," + Math.pow(currentAi.getWayPointForce()-0.04,2)+"\n");
    }

    public void generateEndingStatement(int v) throws IOException {
        if(Constants.OUTPUT_FILE == null) return;

        ArrayList<String> lines = new ArrayList<>();
        lines.add(AI_manager.getAi_basic().getSep_neighbourhood_size() + "," + AI_manager.getAi_basic().getAli_neighbourhood_size() + "," + AI_manager.getAi_basic().getCoh_neighbourhood_size() + "," + AI_manager.getAi_basic().getSep_weight()  + "," + AI_manager.getAi_basic().getAli_weight() + "," + AI_manager.getAi_basic().getCoh_weight() );
        lines.add(v+","+Math.round((System.nanoTime()-startTime)/1000000000)+","+Math.round((System.nanoTime()-startTimeWithoutwait)/1000000000)+","+ iterations + "," + difficulty+","+amountOfBoids+","+attackerStartPosition.x+","+attackerStartPosition.y+"\n");
        lines.addAll(history_of_learning);

        Path file = Paths.get(Constants.OUTPUT_FILE+".txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }
}