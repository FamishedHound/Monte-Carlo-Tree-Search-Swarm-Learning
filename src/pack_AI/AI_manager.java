package pack_AI;

import java.util.ArrayList;

import pack_technical.GameManager;
import processing.core.PApplet;

/*
 * created by the game manager, creates many different AI types to be loaded into a team slot
 * these ai's are static and are not learned upon.
 */
public class AI_manager {

	static AI_type ai_basic, ai_clump, ai_flee, ai_synched, ai_fixed, ai_vagabond, ai_magnet, ai_camera;
	static ArrayList<AI_type> ai_list = new ArrayList<AI_type>(); // An ArrayList for all ai types
	//changed
	//final static int neighbourhood_upper_bound = 100; //300
	final static int neighbourhood_upper_bound = 90;
	final static int neighbourhood_lower_bound = 50;  //20

	final public static int neighbourhoodSeparation_upper_bound = 60;
	final public static int neighbourhoodSeparation_lower_bound = 20;

	static int weight_bound = 10;

	public AI_manager() {
		create_AIs();
	}

	public void create_AIs() {
		/*
		 * remember arguements for and AI_type are: sns, ans ,cns , sw , aw , cw, name
		 * the three rules being seperation, alignment and cohesion, ns = neighbourhood
		 * size and w = weight ( or strength ) BOUNDS 300 to 20,-10 to +10,-10 to
		 * +10,-10 to +10
		 */
		ai_camera = new AI_type(0, 0, 0, 0.0f, 0.0f, 0.0f,0,"camera");
		ai_list.add(ai_basic = new AI_type(30, 70, 70, 2.0f, 1.2f, 0.9f,0.04f, "basic"));
		ai_list.add(ai_clump = new AI_type(0, 0, 0, 100f, 1.2f, 2.9f,0, "attacker"));
		ai_list.add(ai_flee = new AI_type(300, 70, 500, 2.0f, 0.0f, -3.9f,0, "flee"));
		ai_list.add(ai_synched = new AI_type(30, 190, 70, 1.2f, 3.0f, 0.9f,0, "synched"));
		ai_list.add(ai_fixed = new AI_type(70, 190, 190, 7.0f, 3.2f, 3.0f,0, "fixed"));
		ai_list.add(ai_vagabond = new AI_type(60, 70, 290, 2.1f, -1.2f, 1.1f,0, "vagabond"));
		ai_list.add(ai_magnet = new AI_type(90, 70, 70, -7.0f, -1.2f, 0.3f,0, "magnet"));
		ai_list.add(ai_clump = new AI_type(20, 150, 150, 7.0f, 1.2f, 2.9f,0, "clump"));
		//ai_list.add(ai_basic = new AI_type(20, 150, 150, 2.0f, 1.2f, 0.9f,0, "att"));
	}

	public static AI_type get_team_ai(int t) {
		if (t == GameManager.getTeam_number()+1) { // if camera object
			return ai_camera; // assigns a blank ai
		}
		if (ai_list.size() > 0) {
			if (ai_list.get(t % ai_list.size()) != null)
				return ai_list.get(t % ai_list.size());
			else {
				PApplet.print(" 02: ai list is empty");
				return ai_basic;
			}

		}
		return ai_basic;
	}

	public ArrayList<AI_type> getAi_list() {
		return ai_list;
	}

	public static AI_type getAi_basic() {
		return ai_basic;
	}

	public static AI_type getAi_clump() {
		return ai_clump;
	}

	public static AI_type getAi_flee() {
		return ai_flee;
	}

	public static AI_type getAi_synched() {
		return ai_synched;
	}

	public static AI_type getAi_fixed() {
		return ai_fixed;
	}

	public static AI_type getAi_vagabond() {
		return ai_vagabond;
	}

	public static AI_type getAi_magnet() {
		return ai_magnet;
	}

	public static int getWeight_bound() {
		return weight_bound;
	}

	public static int getNeighbourhoodUpperBound() {
		return neighbourhood_upper_bound;
	}

	public static int getNeighbourhoodLowerBound() {
		return neighbourhood_lower_bound;
	}

}
