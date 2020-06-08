package pack_1;

import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_technical.CollisionHandler;
import pack_technical.GameManager;
import pack_technical.ZoneDefence;
import processing.core.PVector;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

public class ParameterGatherAndSetter {
    // 0-1 : parameters for attackers
    //2 counter
    //
    GameManager game;

    CollisionHandler col;
    private double startTime;
    private int counter =0;
    ArrayList<String> history_of_learning = new ArrayList<>();
    double startTimeWithoutwait;
    public int iterations = 0;
    boolean once=true;
    float x;
    float y;
    ArrayList<PVector> hard = new ArrayList<>();
    ArrayList<PVector> medium = new ArrayList<>();
    ArrayList<PVector> easy = new ArrayList<>();
    int amountOfBoids=0;
    String difficulty;
    String[] args;
    public ParameterGatherAndSetter(int PosX, int PosY, int difficulty, int mode, GameManager game , CollisionHandler col, String[] args)  {
        this.game=game;
        this.args=args;
        this.difficulty=args[3];
        this.col=col;
        this.x=Float.parseFloat(args[0]);
        this.y=Float.parseFloat(args[1]);
        this.counter=Integer.parseInt(args[2]);
        amountOfBoids=Integer.parseInt(args[4]);

        System.out.println("SMARTPP1-LR OLD WAYPOINTS");

        startTime=System.nanoTime();
        game.spawn_boids(0,amountOfBoids,new PVector(450,510));
        game.spawn_boids(1,1,new PVector(Float.parseFloat(args[0]),Float.parseFloat(args[1])));
        //game.spawn_boids(1,1,new PVector(1200,510));

        createDifficulties();
    }
    public void createDifficulties(){
// Old settings
        medium.add(new PVector(450,550));
        medium.add(new PVector(650,500));
        medium.add(new PVector(450,405));

        hard.add(new PVector(550,535));
        hard.add(new PVector(550,485));

        /*
        hard.add(new PVector(20,20));
        hard.add(new PVector(1400,20));
        hard.add(new PVector(1400,900));
        hard.add(new PVector(20,900));
        hard.add(new PVector(500,500));
         */


        easy.add(new PVector(450,550));
        easy.add(new PVector(650,500));
        easy.add(new PVector(450,405));
        easy.add(new PVector(590,305));


// New settings
/*        medium.add(new PVector(530,525));
        medium.add(new PVector(730,425));
        medium.add(new PVector(530,330));

        hard.add(new PVector(530,500));
        hard.add(new PVector(530,405));

        easy.add(new PVector(450,600));
        easy.add(new PVector(650,500));
        easy.add(new PVector(450,405));
        easy.add(new PVector(590,305));*/
    }

    public ArrayList<PVector> returnDifficulty(){
        if(difficulty.equals("hard")){
            return hard;
        }
        if(difficulty.equals("medium")){
            return medium;
        }

        return easy;
    }

    public void gather() throws IOException {
        if(col.isLose()){
            System.out.println("Simulation took " + Math.round((System.nanoTime()-startTime)/1000000000) + " s and was a failure");
            generateEndingStatement(0);
            System.exit(0);
        } else if(col.isVictory()){
            generateEndingStatement(1);
            System.out.println("Simulation took " + Math.round((System.nanoTime()-startTime)/1000000000) + " s and was a victory");
            System.exit(0);
            
            if(Math.round((System.nanoTime()-startTime)/1000000000)==300){//timeout after 300 s
                generateEndingStatement(2);
                System.out.println("Timeout");
                System.exit(0);
            }
        }
    }

    public void sendParameters(AI_type currentAi){
        if(once){
            startTimeWithoutwait=System.nanoTime();
            once=false;
        }
        history_of_learning.add(currentAi.getSep_neighbourhood_size() + "," + currentAi.getAli_neighbourhood_size() + "," + currentAi.getCoh_neighbourhood_size() + "," + currentAi.getSep_weight()  + "," + currentAi.getAli_weight() + "," + currentAi.getCoh_weight() + "," +Math.pow(currentAi.getSep_neighbourhood_size()-30,2)+","+Math.pow(currentAi.getAli_neighbourhood_size()-70,2) + "," + Math.pow(currentAi.getCoh_neighbourhood_size()-70,2) + "," + Math.pow(currentAi.getSep_weight()-2,2) + "," + Math.pow(currentAi.getAli_weight()-1.2,2)  + "," + Math.pow(currentAi.getCoh_weight()-0.9f,2) +  "," + Math.pow(currentAi.getWayPointForce()-0.04,2)+"\n");



    }

    public void generateEndingStatement(int v) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(AI_manager.getAi_basic().getSep_neighbourhood_size() + "," + AI_manager.getAi_basic().getAli_neighbourhood_size() + "," + AI_manager.getAi_basic().getCoh_neighbourhood_size() + "," + AI_manager.getAi_basic().getSep_weight()  + "," + AI_manager.getAi_basic().getAli_weight() + "," + AI_manager.getAi_basic().getCoh_weight() );
        lines.add(v+","+Math.round((System.nanoTime()-startTime)/1000000000)+","+Math.round((System.nanoTime()-startTimeWithoutwait)/1000000000)+","+ iterations + "," + difficulty+","+amountOfBoids+","+x+","+y+"\n");
        lines.addAll(history_of_learning);

        Path file = Paths.get(args[5]+counter+".txt");
        Files.write(file, lines, Charset.forName("UTF-8"));

    }


}