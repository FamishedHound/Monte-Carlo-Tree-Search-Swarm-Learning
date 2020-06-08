package pack_AI;

import java.util.Random;

/*
 * a type of AI for one boid, many are created with different parameters and managed by an AIManager,
 * parameters can only be set upon creation, but read at any time (such as boid creation). even though AI's
 * are common to teams, not individuals, this is done on an individual basis for direct user control or
 * tampering with individuals under circumstances.
 * 
 * a set of AI_types is an AI_internal_model, which can be altered.
 */
public class AI_type {

	private float ali_neighbourhood_size, sep_neighbourhood_size, coh_neighbourhood_size;
	private double db_ali_neighbourhood_size, db_sep_neighbourhood_size, db_coh_neighbourhood_size;



	// the doubles have advanced precicion, and can change by very small values.
	private double sep_weight, ali_weight, coh_weight;;

	public float getWayPointForce() {
		return wayPointForce;
	}

	public void setWayPointForce(float wayPointForce) {
		this.wayPointForce = wayPointForce;
	}

	float wayPointForce;
	private String ai_name;
	private double param_a = 0.0001; // learning factor for parameters
	private double neighbourhood_a = 0.0001; // learning factor for parameters
	Random rng = new Random();
	int nub = AI_manager.getNeighbourhoodUpperBound();
	int nlb = AI_manager.getNeighbourhoodLowerBound();
	int wb = AI_manager.getWeight_bound();

	public AI_type(float sns, float ans, float cns, double sw, double aw, double cw,float wayPointForce, String name) {
		this.wayPointForce=wayPointForce;
		sep_neighbourhood_size = sns;
		ali_neighbourhood_size = ans;
		coh_neighbourhood_size = cns;
		db_sep_neighbourhood_size = sep_neighbourhood_size;
		db_ali_neighbourhood_size = ali_neighbourhood_size;
		db_coh_neighbourhood_size = coh_neighbourhood_size;
		sep_weight = sw;
		ali_weight = aw;
		coh_weight = cw;
		ai_name = name;
	}

	public void randomise() {
		System.out.println("double "+ rng.nextDouble());
		db_sep_neighbourhood_size = db_sep_neighbourhood_size+rng.nextDouble();
		db_ali_neighbourhood_size = rng.nextInt(nub - nlb) + nlb;
		db_coh_neighbourhood_size = rng.nextInt(nub - nlb) + nlb;
		sep_weight = rng.nextInt(1 + 2 * wb) - wb;
		ali_weight = rng.nextInt(1 + 2 * wb) - wb;
		coh_weight = rng.nextInt(1 + 2 * wb) - wb;

	}

	public String get_desc_string() {
		return "      name: " + ai_name + "      sns: " + sep_neighbourhood_size + "      sw: " + sep_weight
				+ "      ans: " + ali_neighbourhood_size + "      aw: " + ali_weight + "      cns: "
				+ coh_neighbourhood_size + "      cw: " + coh_weight;
	}

	public float getAli_neighbourhood_size() {
		return ali_neighbourhood_size;
	}

	public void setAli_neighbourhood_size(float ali_neighbourhood_size) {
		this.ali_neighbourhood_size = ali_neighbourhood_size;
	}

	public float getSep_neighbourhood_size() {
		return sep_neighbourhood_size;
	}

	public void setSep_neighbourhood_size(float sep_neighbourhood_size) {
		this.sep_neighbourhood_size = sep_neighbourhood_size;
	}

	public float getCoh_neighbourhood_size() {
		return coh_neighbourhood_size;
	}

	public void setCoh_neighbourhood_size(float coh_neighbourhood_size) {
		this.coh_neighbourhood_size = coh_neighbourhood_size;
	}

	public double getSep_weight() {
		return sep_weight;
	}

	public void setSep_weight(float sep_weight) {
		this.sep_weight = sep_weight;
	}

	public double getAli_weight() {
		return ali_weight;
	}

	public void setAli_weight(float ali_weight) {
		this.ali_weight = ali_weight;
	}

	public double getCoh_weight() {
		return coh_weight;
	}

	public void setCoh_weight(float coh_weight) {
		this.coh_weight = coh_weight;
	}

	public String getAi_name() {
		return ai_name;
	}

	public void setAi_name(String ai_name) {
		this.ai_name = ai_name;
	}

	public void constrain_parameters() {
		sep_weight = Math.min(Math.max(sep_weight, -wb), wb);
		ali_weight = Math.min(Math.max(ali_weight, -wb), wb);
		coh_weight = Math.min(Math.max(coh_weight, -wb), wb);
		db_sep_neighbourhood_size = Math.min(Math.max(db_sep_neighbourhood_size, nlb), nub);
		db_ali_neighbourhood_size = Math.min(Math.max(db_ali_neighbourhood_size, nlb), nub);
		db_coh_neighbourhood_size = Math.min(Math.max(db_coh_neighbourhood_size, nlb), nub);
		// round the double counterparts to fit onto pixel units
		sep_neighbourhood_size = (int) Math.round(db_sep_neighbourhood_size);
		ali_neighbourhood_size = (int) Math.round(db_ali_neighbourhood_size);
		coh_neighbourhood_size = (int) Math.round(db_coh_neighbourhood_size);
	}

	public void learning_update(double[] derivatives) {
		if (rng.nextInt(500) == 0)
			neighbourhood_deviation();
		// this is a useful method for upsetting a "settled neighbourhood" value
		// and gathering a greater spread of polynomial data

			derivative_update(derivatives);
		constrain_parameters();
	}

	private void neighbourhood_deviation() {
		int factor = 2;
		if (rng.nextInt(2) == 0)
			factor = -factor; // negative
		db_sep_neighbourhood_size = db_sep_neighbourhood_size + factor * rng.nextFloat();
		db_ali_neighbourhood_size = db_ali_neighbourhood_size + factor * rng.nextFloat();
		db_coh_neighbourhood_size = db_coh_neighbourhood_size + factor * rng.nextFloat();

	}

	public void derivative_update(double[] derivatives) {
		// p = p - (tiny val ... 0.01? a) * derivative
		// limit alterations to prevent underflow and overflow errors
		double[] mods = new double[6];
		for (int i = 0; i < mods.length; i++) {
			if (i < 3)
				mods[i] = (double) param_a * derivatives[i];// 5 point precision
			else
				mods[i] = (double) neighbourhood_a * derivatives[i];// 5 point precision
			// the old and new positions are interpolated by a_value
		}
		sep_weight = sep_weight - mods[0];
		ali_weight = ali_weight - mods[1];
		coh_weight = coh_weight - mods[2];
		db_sep_neighbourhood_size = db_sep_neighbourhood_size - mods[3];
		db_ali_neighbourhood_size = db_ali_neighbourhood_size - mods[4];
		db_coh_neighbourhood_size = db_coh_neighbourhood_size - mods[5];
	}
	public void setDb_ali_neighbourhood_size(double db_ali_neighbourhood_size) {
		this.db_ali_neighbourhood_size = db_ali_neighbourhood_size;
	}

	public void setDb_sep_neighbourhood_size(double db_sep_neighbourhood_size) {
		this.db_sep_neighbourhood_size = db_sep_neighbourhood_size;
	}

	public void setDb_coh_neighbourhood_size(double db_coh_neighbourhood_size) {
		this.db_coh_neighbourhood_size = db_coh_neighbourhood_size;
	}

	public void setSep_weight(double sep_weight) {
		this.sep_weight = sep_weight;
	}

	public void setAli_weight(double ali_weight) {
		this.ali_weight = ali_weight;
	}

	public void setCoh_weight(double coh_weight) {
		this.coh_weight = coh_weight;
	}

}
