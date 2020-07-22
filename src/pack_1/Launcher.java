package pack_1;

import pack_boids.BoidGeneric;
import processing.core.*;
import processing.event.MouseEvent;
import pack_AI.AI_manager;
import pack_technical.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringJoiner;

/*
 * runs the simulation and holds/broadcasts the state
 */
public class Launcher extends PApplet {

    /** Which boids can see the future */
    public enum PredictStates {
        /** No boids can see the future */
        NONE,
        /** Only the selected boid can see the future */
        SELECTED,
        /** All boids can see the future */
        ALL
    }

    /** Length of history to record for each boid */
    public static final int HISTORY_LENGTH = 0;
    /** Requested frames per second from the applet */
    public static final int SPS = 60;
    /** Start time of the application, helps to identity file names */ // TODO change the name and or description of this
    public static final int START_TIME = (int) System.currentTimeMillis()%100;

    public static PApplet applet;


    private PrintWriter locationWriter;
    private ZoneDefence zone;
    private DisplayManager displayManager;
    private GameManager gameManager;
    private IOManager ioManager;
    private static FlockManager flockManager;
    private CollisionHandler collisionHandler;
    private ParameterGatherAndSetter parameterGatherer;

    boolean toBeDisplayed = true;
    static boolean paused = false;
    static boolean showHelpmenu = false;
    static boolean drawTrails = true;
    static boolean showAdvancedMode = false;

    static PredictStates predictState = PredictStates.SELECTED;
    static int simSpeed = 1; // time acceleration

    public static void main(String[] args) {
        System.out.println("args: " + Arrays.toString(args));
        PApplet.main(Launcher.class, args);
    }

    public static void quit(String message, int code) {
        System.out.println(message);
        Launcher.applet.exit();
    }

    // PApplet extension methods

    @Override
    public void settings() {
        // size(960, 540);
        fullScreen();
        noSmooth();// turns off antialiasing
    }

    private void writeLocations(FlockManager flockManager) {
        StringJoiner dataRow = new StringJoiner(",");
        for(BoidGeneric boid : flockManager.getReal_boids()) {
            dataRow = dataRow.add(Float.toString(boid.getLocation().x));
            dataRow = dataRow.add(Float.toString(boid.getLocation().y));
        }
        String toWrite = dataRow.toString() + "\n";
        locationWriter.write(toWrite);
        locationWriter.flush();

    }

    @Override
    public void setup() {
        Launcher.applet = this;
        try {
            locationWriter = new PrintWriter("output/locations.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Client size: " + width + ", " + height);
        new AI_manager();
        //new OutputWriter(); fix or remove, creates loads of empty files, not sure if needed anymore
        flockManager = new FlockManager(true, false);
        displayManager = new DisplayManager(this, flockManager, createFont("Lucida Sans", 12), createFont("Comic Sans MS", 12));
        gameManager = new GameManager(flockManager);
        ioManager = new IOManager(this, flockManager, displayManager, gameManager, Launcher.this);
        collisionHandler = new CollisionHandler();

        try {
            parameterGatherer = new ParameterGatherAndSetter(gameManager,collisionHandler,args);
            zone = new ZoneDefence(collisionHandler,flockManager,parameterGatherer);
        } catch(IllegalArgumentException e) {
            Launcher.quit(e.getMessage(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        frameRate(Launcher.SPS);
        noCursor();// turns off cursor
    }

    @Override
    public void draw() {
        if (toBeDisplayed) {
            // main step event
            background(60);
            collisionHandler.checkCollisions();
            parameterGatherer.gather();
            flockManager.run(simSpeed);
            writeLocations(flockManager);
            ioManager.run();
            zone.run();
            displayManager.draw();
        } else {
            //zone.simulate();
        }
    }

    @Override
    public void keyPressed() {
        if (key != CODED) {
            ioManager.on_key_pressed(key, keyCode);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (mouseButton == LEFT) {
            ioManager.on_left_click(e);
        }
        if (mouseButton == RIGHT) {
            ioManager.on_right_click(e);
        }
    }

    @Override
    public void mouseWheel(MouseEvent e) {
        int wheelMovement = e.getCount();
        ioManager.on_mouse_wheel(wheelMovement);
    }

    // Getters and setters
    public static FlockManager getFlock() {
        return flockManager;
    }

    public boolean isToBeDisplayed() {
        return toBeDisplayed;
    }
    public void setToBeDisplayed(boolean toBeDisplayed) {
        this.toBeDisplayed = toBeDisplayed;
    }

    public static boolean isPaused() {
        return Launcher.paused;
    }
    public static void setPaused(boolean paused) {
        Launcher.paused = paused;
    }

    public static boolean isHelpmenuShowing() {
        return Launcher.showHelpmenu;
    }
    public static void setShowHelpmenu(boolean showHelpmenu) {
        Launcher.showHelpmenu = showHelpmenu;
    }

    public static boolean areTrailsDrawn() {
        return Launcher.drawTrails;
    }
    public static void setDrawTrails(boolean drawTrails) {
        Launcher.drawTrails = drawTrails;
    }

    public static boolean isAdvancedModeShowing() {
        return Launcher.showAdvancedMode;
    }
    public static void setShowAdvancedMode(boolean showAdvancedMode) {
        Launcher.showAdvancedMode = showAdvancedMode;
    }

    public static PredictStates getPredictState() {
        return predictState;
    }
    public static void setPredictState(PredictStates predictState) {
        Launcher.predictState = predictState;
    }

    public static int getSimSpeed() {
        return simSpeed;
    }
    public static void setSimSpeed(int simSpeed) {
        Launcher.simSpeed = simSpeed;
    }

    public static int getRun_moment() {
        return START_TIME;
    }
}
