package pack_AI;

import java.util.Random;

/**
 * a type of AI for one boid, many are created with different parameters and managed by an AIManager,
 * parameters can only be set upon creation, but read at any time (such as boid creation). even though AI's
 * are common to teams, not individuals, this is done on an individual basis for direct user control or
 * tampering with individuals under circumstances.
 *
 * a set of AI_types is an AI_internal_model, which can be altered.
 */
public class AI_type {

	private float alignForce;
	private float separationForce;
	private float cohesionForce;
	private double separationForceWeight;
	private double alignmentForceWeight;
	private double cohesionForceWeight;

    public float getWayPointForce() {
		return wayPointForce;
	}

	public void setWayPointForce(float wayPointForce) {
		this.wayPointForce = wayPointForce;
	}

	float wayPointForce;
	private String ai_name;
	private final double param_a = 0.0001; // learning factor for parameters
	private final double neighbourhood_a = 0.0001; // learning factor for parameters
	Random rng = new Random();
	int wb = AI_manager.getWeight_bound();

	public AI_type(float sns, float ans, float cns, double sw, double aw, double cw,float wayPointForce) {
		this.wayPointForce=wayPointForce;
		separationForce = sns;
		alignForce = ans;
		cohesionForce = cns;
		separationForceWeight = sw;
		alignmentForceWeight = aw;
		cohesionForceWeight = cw;
	}

	public AI_type(float sns, float ans, float cns, double sw, double aw, double cw,float wayPointForce, String name) {
		this(sns, ans, cns, sw, aw, cw, wayPointForce);
		ai_name = name;
	}

	public String get_desc_string() {
		return "      name: " + ai_name + "      sns: " + separationForce + "      sw: " + separationForceWeight
				+ "      ans: " + alignForce + "      aw: " + alignmentForceWeight + "      cns: "
				+ cohesionForce + "      cw: " + cohesionForceWeight;
	}

	public float getAlignForce() {
		return alignForce;
	}

	public void setAlignForce(float alignForce) {
		this.alignForce = alignForce;
	}

	public float getSeparationForce() {
		return separationForce;
	}

	public void setSeparationForce(float separationForce) {
		this.separationForce = separationForce;
	}

	public float getCohesionForce() {
		return cohesionForce;
	}

	public void setCohesionForce(float cohesionForce) {
		this.cohesionForce = cohesionForce;
	}

	public double getSeparationForceWeight() {
		return separationForceWeight;
	}

	public void setSeparationForceWeight(float separationForceWeight) {
		this.separationForceWeight = separationForceWeight;
	}

	public double getAlignmentForceWeight() {
		return alignmentForceWeight;
	}

	public void setAlignmentForceWeight(float alignmentForceWeight) {
		this.alignmentForceWeight = alignmentForceWeight;
	}

	public double getCohesionForceWeight() {
		return cohesionForceWeight;
	}

	public void setCohesionForceWeight(float cohesionForceWeight) {
		this.cohesionForceWeight = cohesionForceWeight;
	}

	public String getAi_name() {
		return ai_name;
	}

	public void setAi_name(String ai_name) {
		this.ai_name = ai_name;
	}

	public void constrain_parameters() {
		separationForceWeight = Math.min(Math.max(separationForceWeight, -wb), wb);
		alignmentForceWeight = Math.min(Math.max(alignmentForceWeight, -wb), wb);
		cohesionForceWeight = Math.min(Math.max(cohesionForceWeight, -wb), wb);

		// round the double counterparts to fit onto pixel units
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


	}

	public void derivative_update(double[] derivatives) {
		// p = p - (tiny val ... 0.01? a) * derivative
		// limit alterations to prevent underflow and overflow errors
		double[] mods = new double[6];
		for (int i = 0; i < mods.length; i++) {
			if (i < 3)
				mods[i] = param_a * derivatives[i];// 5 point precision
			else
				mods[i] = neighbourhood_a * derivatives[i];// 5 point precision
			// the old and new positions are interpolated by a_value
		}
		separationForceWeight = separationForceWeight - mods[0];
		alignmentForceWeight = alignmentForceWeight - mods[1];
		cohesionForceWeight = cohesionForceWeight - mods[2];

	}

	public void setSep_weight(double sep_weight) {
		this.separationForceWeight = sep_weight;
	}

	public void setAli_weight(double ali_weight) {
		this.alignmentForceWeight = ali_weight;
	}

	public void setCoh_weight(double coh_weight) {
		this.cohesionForceWeight = coh_weight;
	}

}
