package pack_AI;

import java.util.Random;

import pack_boids.Boid_generic;
import pack_technical.GameManager;

/*
 * each real boid has one internal model (this)
 * a set of AI_types that can be created imperfectly, and learned upon.
 * holds a single real boids imaginary parameters for every other boid,
 * also holds the error of these imaginary estimations
*/
public class AI_internal_model {
	
	Boid_generic parent_boid;
	AI_type[] ai_s = new AI_type[GameManager.getTeam_number()];

	public AI_internal_model(boolean perfect, Boid_generic parent) {
		parent_boid = parent;
		if (perfect)
			generate_perfect_ais();
		else
			generate_random_ais();
	}

	public void debug_draw() {
		System.out.println("internal model for " + parent_boid.getAi().getAi_name());
		for (int t = 0; t < GameManager.getTeam_number(); t++) {
			System.out.print("t " + t + " :");
			System.out.println(" " + get_ai_description_line_for_team(t));
		}
	}

	/*
	 * remember arguements for and AI_type are: sns, ans ,cns , sw , aw , cw, name
	 * the three rules being seperation, alignment and cohesion, ns = neighbourhood
	 * size and w = weight ( or strength )
	 */
	void generate_perfect_ais() {
		for (int t = 0; t < GameManager.getTeam_number(); t++) {
			generate_perfect_ai(t);
		}
	}

	void generate_perfect_ai(int t) {
		float sns = AI_manager.get_team_ai(t).getSep_neighbourhood_size();
		float ans = AI_manager.get_team_ai(t).getAli_neighbourhood_size();
		float cns = AI_manager.get_team_ai(t).getCoh_neighbourhood_size();
		double sw = AI_manager.get_team_ai(t).getSep_weight();
		double aw = AI_manager.get_team_ai(t).getAli_weight();
		double cw = AI_manager.get_team_ai(t).getCoh_weight();
		String ai_name = AI_manager.get_team_ai(t).getAi_name();
		ai_s[t] = new AI_type(sns, ans, cns, sw, aw, cw,0, ai_name);
	}

	public String get_ai_description_line_for_team(int t) {
		return ai_s[t].get_desc_string();// gets a line of description from a team's ai on this internal model
	}

	void generate_random_ais() {
		Random rng = new Random();
		for (int t = 0; t < GameManager.getTeam_number(); t++) {
			if (t != parent_boid.getTeam()) {
				int nub = AI_manager.getNeighbourhoodUpperBound();
				int nlb = AI_manager.getNeighbourhoodLowerBound();
				int m_2 = AI_manager.getWeight_bound();
				ai_s[t] = new AI_type(rng.nextInt(nub-nlb) + nlb, rng.nextInt(nub-nlb) + 1, rng.nextInt(nub-nlb) + 1,
						rng.nextInt(1+2*m_2) - m_2, rng.nextInt(1+2*m_2) - m_2, rng.nextInt(21) - m_2,0,
						"Generated guess for team " + t);
			} else { // if the team is the known team
				generate_perfect_ai(t);
			}
		}
	}

	void generate_blank_ais() {
		for (int i = 0; i < GameManager.getTeam_number(); i++) {
			ai_s[i] = new AI_type(0, 0, 0, 0f, 0f, 0f,0, "Blank AI for team " + i);
		}
	}

	public AI_type get_ai_team(int t) {
		return ai_s[t];
	}

	public Boid_generic getParent_boid() {
		return parent_boid;
	}

	public void setParent_boid(Boid_generic parent_boid) {
		this.parent_boid = parent_boid;
	}

}
