package pack_technical;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import pack_1.Launcher;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PApplet;
import processing.core.PVector;

/*
 * does game logic sysems, such as holding teams, team colours and can perform higher level game
 * functions such as telling the flockManager to spawn groups of boids on a team, or start a round.
 * 
 * The game manager also creates an AI manager which creates all the AI profiles, which the game
 * manager will load into each team so that they have a common AI type.
 */
public class GameManager {

    static BoidStandard selected_boid = null;

    private static final int team_number = Launcher.applet.width / 75; // not all of these will be used
    private final FlockManager flock_ref;

    @SuppressWarnings("unchecked")
    static ArrayList<BoidGeneric>[] team = new ArrayList[getTeam_number()];
    static Color[] team_cols = new Color[getTeam_number()]; // Array for the teams, index is team, colour is held
    static AI_type[] team_ai = new AI_type[getTeam_number()]; // Array for the teams, index is team, colour is held
    static boolean isSwitched = false;

    public GameManager(FlockManager f) {
        flock_ref = f;
        // assign team colours and ai's
        for (int i = 0; i < getTeam_number(); i++) { // for every team
            team_cols[i] = generate_teamcolour(i);
            team_ai[i] = AI_manager.get_team_ai(i);
            team[i] = new ArrayList<BoidGeneric>();
            team[i].clear(); // teams start empty
        }

    }

    Color generate_teamcolour(int seed) {
        Random rand = new Random(seed);
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }

    public static Color get_team_colour(int i) {
        if ((i >= 0) && (i < getTeam_number()))
            return team_cols[i]; // for every normal team fetch it's colour
        else if (i == getTeam_number()+1) // for the camera team
            return new Color(210, 210, 210);
        else {
            PApplet.print(" error: attempted to access team colour array out of bounds");
            return new Color(255, 0, 0);

        }

    }

    public void spawn_boids(int team_n, int amount, PVector pos) {
        for (int i = 0; i < amount; i++) {
            BoidGeneric b = new BoidStandard(pos.x, pos.y, team_n,i);
            flock_ref.addBoid(b);
            team[team_n].add(b);
        }
    }

    public void delete_selected() {
        if (selected_boid != null)
            selected_boid.setAlive(false);
        selected_boid = null;

    }

    public static ArrayList<BoidGeneric> get_team(int i) {
        return team[i];
    }

    public BoidGeneric get_select_boid() {
        return selected_boid;
    }

    public static Color[] getTeam_cols() {
        return team_cols;
    }

    public static void setTeam_cols(Color[] team_cols) {
        GameManager.team_cols = team_cols;
    }

    public static AI_type[] getTeam_ai() {
        return team_ai;
    }

    public static void setTeam_ai(AI_type[] team_ai) {
        GameManager.team_ai = team_ai;
    }

    public static int getTeam_number() {
        return team_number;
    }

    public static BoidGeneric getSelected_boid() {
        return selected_boid;
    }

    public void setSelected_boid(BoidStandard selected_boid) {
        GameManager.selected_boid = selected_boid;
    }

    public static int get_random_team() {
        //return (int) parent.random(getTeam_number()); previous Alex version
        if (isSwitched){
            return 1;
        }
        isSwitched=true;
        return 0;
    }

    public FlockManager getFlock_ref() {
        return flock_ref;
    }

    public static ArrayList<BoidGeneric>[] getTeam() {
        return team;
    }

    public static boolean isIsSwitched() {
        return isSwitched;
    }

}
