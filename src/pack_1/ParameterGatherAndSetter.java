package pack_1;

import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_technical.CollisionHandler;
import pack_technical.GameManager;
import processing.core.PVector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

public class ParameterGatherAndSetter {

    private CollisionHandler collisionHandler;
    private final PVector attackerStartPosition;
    private final long startTime;
    private long startTimeWithoutwait = -1;
    private ArrayList<String> historyOfLearning = new ArrayList<>();
    private int iterations = 0;

    private enum Difficulty {
        EASY,
        MEDIUM,
        HARD;
        static Difficulty fromString(String difficulty) throws IllegalArgumentException {
            switch(difficulty.toLowerCase()) {
                case "easy":
                    return EASY;
                case "medium":
                    return MEDIUM;
                case "hard":
                    return HARD;
                default:
                    throw new IllegalArgumentException("Difficulty must be 'easy', 'medium', or 'hard'.");
            }
        }
    }
    Difficulty difficulty;
    EnumMap<Difficulty, ArrayList<PVector>> defenderBoidWaypoints = new EnumMap<>(Difficulty.class);

    int amountOfBoids=0;

    public ParameterGatherAndSetter(GameManager game, CollisionHandler col, String[] args) throws IllegalArgumentException {
        if(args.length < 4) {
            throw new IllegalArgumentException("Arguments must be [attackerStartX] [attackerStartY] [difficulty] [amountOfBoids]");
        }
        this.collisionHandler=col;

        this.attackerStartPosition = new PVector(Float.parseFloat(args[0]), Float.parseFloat(args[1]));
        this.difficulty=Difficulty.fromString(args[2]);
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
        this.defenderBoidWaypoints.put(Difficulty.EASY, new ArrayList<>() {
            {
                add(PVector.add(Constants.TARGET, new PVector(-100, 50)));
                add(PVector.add(Constants.TARGET, new PVector(200, 0)));
                add(PVector.add(Constants.TARGET, new PVector(-100, -95)));
                add(PVector.add(Constants.TARGET, new PVector(40, -195)));
            }
        });
        this.defenderBoidWaypoints.put(Difficulty.MEDIUM, new ArrayList<>() {
            {
                add(PVector.add(Constants.TARGET, new PVector(-100, 50)));
                add(PVector.add(Constants.TARGET, new PVector(100, 0)));
                add(PVector.add(Constants.TARGET, new PVector(-100, -95)));
            }
        });
        this.defenderBoidWaypoints.put(Difficulty.HARD, new ArrayList<>() {
            {
                add(PVector.add(Constants.TARGET, new PVector(0, -15)));
                add(PVector.add(Constants.TARGET, new PVector(0, 15)));
            }
        });

        /*
        hard.add(new PVector(20,20));
        hard.add(new PVector(1400,20));
        hard.add(new PVector(1400,900));
        hard.add(new PVector(20,900));
        hard.add(new PVector(500,500));
         */


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
        return this.defenderBoidWaypoints.get(difficulty);
    }

    public void gather() {
        try {
            if(collisionHandler.isLose()){
                generateEndingStatement(0);
                String finalMessage = "Simulation took " + Math.round((System.nanoTime()-startTime)/1000000000) + " s and was a failure";
                Launcher.quit(finalMessage, 0);
            } else if(collisionHandler.isVictory()){
                generateEndingStatement(1);
                String finalMessage = "Simulation took " + Math.round((System.nanoTime()-startTime)/1000000000) + " s and was a victory";
                Launcher.quit(finalMessage, 0);
            } else if(Math.round((System.nanoTime()-startTime)/1000000000)==300){//timeout after 300 s
                generateEndingStatement(2);
                String finalMessage = "Timeout";
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
        historyOfLearning.add(currentAi.getSep_neighbourhood_size() + "," + currentAi.getAli_neighbourhood_size() + "," + currentAi.getCoh_neighbourhood_size() + "," + currentAi.getSep_weight()  + "," + currentAi.getAli_weight() + "," + currentAi.getCoh_weight() + "," +Math.pow(currentAi.getSep_neighbourhood_size()-30,2)+","+Math.pow(currentAi.getAli_neighbourhood_size()-70,2) + "," + Math.pow(currentAi.getCoh_neighbourhood_size()-70,2) + "," + Math.pow(currentAi.getSep_weight()-2,2) + "," + Math.pow(currentAi.getAli_weight()-1.2,2)  + "," + Math.pow(currentAi.getCoh_weight()-0.9f,2) +  "," + Math.pow(currentAi.getWayPointForce()-0.04,2)+"\n");
    }

    public void generateEndingStatement(int v) throws IOException {
        if(Constants.OUTPUT_FILE == null) return;

        ArrayList<String> lines = new ArrayList<>();
        lines.add(AI_manager.getAi_basic().getSep_neighbourhood_size() + "," + AI_manager.getAi_basic().getAli_neighbourhood_size() + "," + AI_manager.getAi_basic().getCoh_neighbourhood_size() + "," + AI_manager.getAi_basic().getSep_weight()  + "," + AI_manager.getAi_basic().getAli_weight() + "," + AI_manager.getAi_basic().getCoh_weight() );
        lines.add(v+","+Math.round((System.nanoTime()-startTime)/1000000000)+","+Math.round((System.nanoTime()-startTimeWithoutwait)/1000000000)+","+ iterations + "," + difficulty+","+amountOfBoids+","+attackerStartPosition.x+","+attackerStartPosition.y+"\n");
        lines.addAll(historyOfLearning);

        Path file = Paths.get(Constants.OUTPUT_FILE+".txt");
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public void incrementIterations() {
        this.iterations++;
    }
}