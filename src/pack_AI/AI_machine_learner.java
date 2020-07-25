package pack_AI;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import pack_1.Constants;
import pack_1.Launcher;
import pack_boids.*;
import pack_technical.FlockManager;
import pack_technical.GameManager;
import pack_technical.OutputWriter;
import processing.core.PVector;

/*
 * Adds points and fits curves with every imaginary boid and it's original's original model
 */
public class  AI_machine_learner {

    static final int poly_count = 5; // data points required for one polynomial
    static final int maximum_concurrent_points = 40; // data points required for one polynomial
    int[] prediction_errors = new int[GameManager.getTeam_number()];
    PolynomialCurveFitter fitter = PolynomialCurveFitter.create(4); // Fourth degree polynomial
    // lists of observed points for a parameter for a team
    WeightedObservedPoints[] obs_sw = new WeightedObservedPoints[GameManager.getTeam_number()];
    WeightedObservedPoints[] obs_aw = new WeightedObservedPoints[GameManager.getTeam_number()];
    WeightedObservedPoints[] obs_cw = new WeightedObservedPoints[GameManager.getTeam_number()];
    WeightedObservedPoints[] obs_sns = new WeightedObservedPoints[GameManager.getTeam_number()];
    WeightedObservedPoints[] obs_ans = new WeightedObservedPoints[GameManager.getTeam_number()];
    WeightedObservedPoints[] obs_cns = new WeightedObservedPoints[GameManager.getTeam_number()];
    // lists of coefficients for fitted parameters for a team
    int[] coeff_count = new int[GameManager.getTeam_number()];
    double[][] coeff_sw = new double[GameManager.getTeam_number()][5];
    double[][] coeff_aw = new double[GameManager.getTeam_number()][5];
    double[][] coeff_cw = new double[GameManager.getTeam_number()][5];
    double[][] coeff_sns = new double[GameManager.getTeam_number()][5];
    double[][] coeff_ans = new double[GameManager.getTeam_number()][5];
    double[][] coeff_cns = new double[GameManager.getTeam_number()][5];
    // list of derivatives
    double derivative_sw, derivative_aw, derivative_cw, derivative_sns, derivative_ans, derivative_cns;

    AI_internal_model internal_model_ref; // the model to work with
    BoidStandard parent_boid; // the boid holding this internal model
    int error = 0;
    float[][] points = new float[2][2];
    PVector imaginary_pos, original_pos;

    public AI_machine_learner(BoidStandard boid_standard) {
        parent_boid = boid_standard;
        internal_model_ref = boid_standard.getInternal_model();
        initialise_observed_point_lists();
        for (int i = 0; i < GameManager.getTeam_number(); i++) {
            cull_point_lists(i);
        }
    }

    public int getPrediction_error_for_team(int t) {
        return prediction_errors[t];
    }

    public int setPrediction_error_for_team(int t, int in) {
        return this.prediction_errors[t] = in;
    }

    int calculate_error(BoidImaginary b, float[][] points) {
        if (b != null && b.getOriginal() != null) {
            imaginary_pos = new PVector(points[0][0], points[0][1]);
            original_pos = new PVector(points[1][0], points[1][1]);
            double angle_diff1 = 10 * ((b.getOriginal().getVelocity().heading() - b.getVelocity().heading())) % 360;
            double angle_diff2 = 10 * ((b.getVelocity().heading() - b.getOriginal().getVelocity().heading())) % 360;
            imaginary_pos.sub(original_pos);
            return (int) (-(Math.min(angle_diff1, angle_diff2)) + imaginary_pos.mag());
        }
        return error;
    }

    float[][] calculate_points(BoidImaginary b) {
        points[0][0] = b.getLocation().x;
        points[0][1] = b.getLocation().y;
        points[1][0] = b.getOriginal().getLocation().x;
        points[1][1] = b.getOriginal().getLocation().y;
        return points;
    }

    void initialise_observed_point_lists() {
        for (int i = 0; i < GameManager.getTeam_number(); i++) {
            obs_sw[i] = new WeightedObservedPoints();
            obs_aw[i] = new WeightedObservedPoints();
            obs_cw[i] = new WeightedObservedPoints();
            obs_sns[i] = new WeightedObservedPoints();
            obs_ans[i] = new WeightedObservedPoints();
            obs_cns[i] = new WeightedObservedPoints();
        }

    }

    private void update_estimates(int observed_t) {
        double[] derivatives = { derivative_sw, derivative_aw, derivative_cw, derivative_sns, derivative_ans,
                derivative_cns };
        for (int i = 0; i < derivatives.length; i++) {
            // some derivatives can be as large or small as infinity, this is not desired so
            // limit them
            derivatives[i] = Math.min(Math.max(derivatives[i], -AI_manager.getNeighbourhoodUpperBound()),
                    AI_manager.getNeighbourhoodUpperBound());
        }
        parent_boid.getInternal_model().ai_s[observed_t].learning_update(derivatives);
        System.out.println();
    }

    private void record_error_for_observation_of(int observed_t) {
        double param_sw = internal_model_ref.ai_s[observed_t].getSeparationForceWeight();
        double param_aw = internal_model_ref.ai_s[observed_t].getAlignmentForceWeight();
        double param_cw = internal_model_ref.ai_s[observed_t].getCohesionForceWeight();
        float param_sns = internal_model_ref.ai_s[observed_t].getSeparationForce();
        float param_ans = internal_model_ref.ai_s[observed_t].getAlignForce();
        float param_cns = internal_model_ref.ai_s[observed_t].getCohesionForce();
        // add observed_t points for the observed_t entity
        obs_sw[observed_t].add(param_sw, error); // todo intentionally wrong
        obs_aw[observed_t].add(param_aw, error);
        obs_cw[observed_t].add(param_cw, error);
        obs_sns[observed_t].add(param_sns, error);
        obs_ans[observed_t].add(param_ans, error);
        obs_cns[observed_t].add(param_cns, error);
        coeff_count[observed_t]++;
        if (OutputWriter.isOutput_to_file()) {
            String data = param_sw + "," + error;
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "param_sw");
            data = param_aw + "," + error;
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "param_aw");
            data = param_cw + "," + error;
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "param_cw");
            data = param_sns + "," + error;
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "param_sns");
            data = param_ans + "," + error;
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "param_ans");
            data = param_cns + "," + error;
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "param_cns");
        }
    }

    private void record_polynomials_for_observation_of(int observed_t) {
        coeff_sw[observed_t] = fitter.fit(obs_sw[observed_t].toList());
        coeff_aw[observed_t] = fitter.fit(obs_aw[observed_t].toList());
        coeff_cw[observed_t] = fitter.fit(obs_cw[observed_t].toList());
        coeff_sns[observed_t] = fitter.fit(obs_sns[observed_t].toList());
        coeff_ans[observed_t] = fitter.fit(obs_ans[observed_t].toList());
        coeff_cns[observed_t] = fitter.fit(obs_cns[observed_t].toList());
        if (OutputWriter.isOutput_to_file()) {
            String data = coeff_sw[observed_t][4] + "," + coeff_sw[observed_t][3] + "," + coeff_sw[observed_t][2] + ","
                    + coeff_sw[observed_t][1] + "," + coeff_sw[observed_t][0];
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "poly_sw");
            data = coeff_aw[observed_t][4] + "," + coeff_aw[observed_t][3] + "," + coeff_aw[observed_t][2] + ","
                    + coeff_aw[observed_t][1] + "," + coeff_aw[observed_t][0];
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "poly_aw");
            data = coeff_cw[observed_t][4] + "," + coeff_cw[observed_t][3] + "," + coeff_cw[observed_t][2] + ","
                    + coeff_cw[observed_t][1] + "," + coeff_cw[observed_t][0];
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "poly_cw");
            data = coeff_sns[observed_t][4] + "," + coeff_sns[observed_t][3] + "," + coeff_sns[observed_t][2] + ","
                    + coeff_sns[observed_t][1] + "," + coeff_sns[observed_t][0];
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "poly_sns");
            data = coeff_ans[observed_t][4] + "," + coeff_ans[observed_t][3] + "," + coeff_ans[observed_t][2] + ","
                    + coeff_ans[observed_t][1] + "," + coeff_ans[observed_t][0];
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "poly_ans");
            data = coeff_cns[observed_t][4] + "," + coeff_cns[observed_t][3] + "," + coeff_cns[observed_t][2] + ","
                    + coeff_cns[observed_t][1] + "," + coeff_cns[observed_t][0];
            OutputWriter.output_perspective(parent_boid.getTeam(), observed_t, data, "poly_cns");
        }
    }

    // empties recorded data points to stop infinite accumilation
    private void cull_point_lists(int t) {
        obs_sw[t].clear();
        obs_aw[t].clear();
        obs_cw[t].clear();
        obs_sns[t].clear();
        obs_ans[t].clear();
        obs_cns[t].clear();
        coeff_count[t] = 0;
    }

    int max_distance() {
        return (int) ((Constants.Boids.MAX_SPEED_DEF * 2) * Launcher.HISTORY_LENGTH);
    }

    private double create_new_term(int exponent, double coeffs, double param_x) {
        // term format... e.g. 5x^4 becomes 5(4x^5);
        // is is the point of the derivative
        // so becomes coeff(exponent*x^exponent)
        double term = coeffs * (exponent * Math.pow(Math.abs(param_x), coeffs)); // doe abs work?
        if (Double.isInfinite(term))
            term = 1;
        if (param_x < 0)
            return -term;
        else
            return term;
    }

    private double sum_terms(String param, int observed_t) {
        AI_type parent_ai = parent_boid.getInternal_model().ai_s[observed_t];
        double[] coeffs = new double[5];
        double param_x = 0;
        switch (param) {
        case "sw":
            coeffs = coeff_sw[observed_t];
            param_x = parent_ai.getSeparationForceWeight();
            break;
        case "aw":
            coeffs = coeff_aw[observed_t];
            param_x = parent_ai.getAlignmentForceWeight();
            break;
        case "cw":
            coeffs = coeff_cw[observed_t];
            param_x = parent_ai.getCohesionForceWeight();
            break;
        case "sns":
            coeffs = coeff_sns[observed_t];
            param_x = parent_ai.getSeparationForce();
            break;
        case "ans":
            coeffs = coeff_ans[observed_t];
            param_x = parent_ai.getAlignForce();
            break;
        case "cns":
            coeffs = coeff_cns[observed_t];
            param_x = parent_ai.getCohesionForce();
            break;
        default:
            System.out.println("error: not a real term arguement");
            break;
        }
        double terms_toal = 0;
        for (int e = 1; e < 5; e++) {
            // power of zero can be ignored for calculating the gradient
            terms_toal = terms_toal + create_new_term(e, coeffs[e], param_x);
        }
        return terms_toal;
    }

    private void calculate_derivative(int observed_t) {
        derivative_sw = sum_terms("sw", observed_t);
        derivative_aw = sum_terms("aw", observed_t);
        derivative_cw = sum_terms("cw", observed_t);
        derivative_sns = sum_terms("ans", observed_t);
        derivative_ans = sum_terms("sns", observed_t);
        derivative_cns = sum_terms("cns", observed_t);
    }

    // takes the final state of the imaginary flock
    public void run(FlockManager mind_flock) {
        int observer_t = parent_boid.getTeam();
        for (BoidGeneric in : mind_flock.getAllBoids()) {
            BoidImaginary b = (BoidImaginary) in;
            int observed_t = b.getOriginal().getTeam();
            points = calculate_points(b);
            error = calculate_error(b, points);
            // if error is > max travel speed it is a result of wrapping and can be ignored.
            // do not plot if error is equal to zero as no interations are implied
            // if (Launcher.isSim_drawtrails())
                //draw_error_bars(b, observer_t);
            if (observed_t != observer_t) {
                if (error < max_distance() && !b.isAlone())
                    record_error_for_observation_of(observed_t);
                if (coeff_count[observed_t] > poly_count) {
                    record_polynomials_for_observation_of(observed_t); // do this periodically once data has accumilated
                    calculate_derivative(observed_t); // do this periodically once data has accumilated
                    update_estimates(observed_t); // acts on parent_boid
                }
                if (coeff_count[observed_t] > maximum_concurrent_points)
                    cull_point_lists(observed_t); // empty data every n frames to stop memory leakage
            }
        }
    }
}
