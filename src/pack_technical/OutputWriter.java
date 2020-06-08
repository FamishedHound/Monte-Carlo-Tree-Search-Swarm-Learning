package pack_technical;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import pack_1.Launcher;
import pack_AI.AI_manager;

public class OutputWriter {

	static boolean output_to_file = true;
	int max_record_length = 100;
	int current_record_length = 0;
	// camera is team -1 
	// list of all the writers
	static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();
	// plot param,error
	static ArrayList<PrintWriter> PARAM_seperation_weight_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> PARAM_alignment_weight_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> PARAM_cohesion_weight_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> PARAM_seperation_neighbourhood_size_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> PARAM_alignment_neighbourhood_size_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> PARAM_cohesion_neighbourhood_size_writers = new ArrayList<PrintWriter>();
	// plot polynomials
	static ArrayList<PrintWriter> POLY_seperation_weight_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> POLY_alignment_weight_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> POLY_cohesion_weight_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> POLY_seperation_neighbourhood_size_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> POLY_alignment_neighbourhood_size_writers = new ArrayList<PrintWriter>();
	static ArrayList<PrintWriter> POLY_cohesion_neighbourhood_size_writers = new ArrayList<PrintWriter>();
	
	public OutputWriter() {
		// parameter writers
		PARAM_seperation_weight_writers.clear();
		PARAM_alignment_weight_writers.clear();
		PARAM_cohesion_weight_writers.clear();
		PARAM_seperation_neighbourhood_size_writers.clear();
		PARAM_alignment_neighbourhood_size_writers.clear();
		PARAM_cohesion_neighbourhood_size_writers.clear();
		// polynomial writers
		POLY_seperation_weight_writers.clear();
		POLY_alignment_weight_writers.clear();
		POLY_cohesion_weight_writers.clear();
		POLY_seperation_neighbourhood_size_writers.clear();
		POLY_alignment_neighbourhood_size_writers.clear();
		POLY_cohesion_neighbourhood_size_writers.clear();
		create_all_param_writers();
	}

	static void create_all_param_writers() {
		PrintWriter sw, aw, cw, sns, ans, cns, swp, awp, cwp, snsp, ansp, cnsp;
		// creates one extra team for the camera team that is non-interactive.
		for (int observer = 0; observer <= GameManager.getTeam_number()+1; observer++) {
			for (int observed = 0; observed <= GameManager.getTeam_number()+1; observed++) {
				try {
					// param writers
					sw = new PrintWriter(get_file_name(observer, observed, "param_sw") + ".txt", "UTF-8");
					PARAM_seperation_weight_writers.add(sw);
					writers.add(sw);
					aw = new PrintWriter(get_file_name(observer, observed, "param_aw") + ".txt", "UTF-8");
					PARAM_alignment_weight_writers.add(aw);
					writers.add(aw);
					cw = new PrintWriter(get_file_name(observer, observed, "param_cw") + ".txt", "UTF-8");
					PARAM_cohesion_weight_writers.add(cw);
					writers.add(cw);
					sns = new PrintWriter(get_file_name(observer, observed, "param_sns") + ".txt", "UTF-8");
					PARAM_seperation_neighbourhood_size_writers.add(sns);
					writers.add(sns);
					ans = new PrintWriter(get_file_name(observer, observed, "param_ans") + ".txt", "UTF-8");
					PARAM_alignment_neighbourhood_size_writers.add(ans);
					writers.add(ans);
					cns = new PrintWriter(get_file_name(observer, observed, "param_cns") + ".txt", "UTF-8");
					PARAM_cohesion_neighbourhood_size_writers.add(cns);
					writers.add(cns);
					// polynomial writers
					swp = new PrintWriter(get_file_name(observer, observed, "poly_sw") + ".txt", "UTF-8");
					POLY_seperation_weight_writers.add(swp);
					writers.add(swp);
					awp = new PrintWriter(get_file_name(observer, observed, "poly_aw") + ".txt", "UTF-8");
					POLY_alignment_weight_writers.add(awp);
					writers.add(awp);
					cwp = new PrintWriter(get_file_name(observer, observed, "poly_cw") + ".txt", "UTF-8");
					POLY_cohesion_weight_writers.add(cwp);
					writers.add(cwp);
					snsp = new PrintWriter(get_file_name(observer, observed, "poly_sns") + ".txt", "UTF-8");
					POLY_seperation_neighbourhood_size_writers.add(snsp);
					writers.add(snsp);
					ansp = new PrintWriter(get_file_name(observer, observed, "poly_ans") + ".txt", "UTF-8");
					POLY_alignment_neighbourhood_size_writers.add(ansp);
					writers.add(ansp);
					cnsp = new PrintWriter(get_file_name(observer, observed, "poly_cns") + ".txt", "UTF-8");
					POLY_cohesion_neighbourhood_size_writers.add(cnsp);
					writers.add(cnsp);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					System.out.println("error: file handling has failed");
					e.printStackTrace();
				}
			}
		}
	}

	public static int get_index(int p1, int p2) {
		return (p1 * (GameManager.getTeam_number() + 2)) + p2 % GameManager.getTeam_number();
	}

	public static void output_perspective(int observer_t, int observed_t, String value, String param) {
		int index = get_index(observer_t, observed_t);
		switch (param) {
		case "param_sw":
			if (PARAM_seperation_weight_writers.size() >= observer_t)
				PARAM_seperation_weight_writers.get(index).println(value);
			break;
		case "param_aw":
			if (PARAM_alignment_weight_writers.size() >= observer_t)
				PARAM_alignment_weight_writers.get(index).println(value);
			break;
		case "param_cw":
			if (PARAM_cohesion_weight_writers.size() >= observer_t)
				PARAM_cohesion_weight_writers.get(index).println(value);
			break;
		case "param_sns":
			if (PARAM_seperation_neighbourhood_size_writers.size() >= observer_t)
				PARAM_seperation_neighbourhood_size_writers.get(index).println(value);
			break;
		case "param_ans":
			if (PARAM_alignment_neighbourhood_size_writers.size() >= observer_t)
				PARAM_alignment_neighbourhood_size_writers.get(index).println(value);
			break;
		case "param_cns":
			if (PARAM_cohesion_neighbourhood_size_writers.size() >= observer_t)
				PARAM_cohesion_neighbourhood_size_writers.get(index).println(value);
			break;
			// polynomial
		case "poly_sw":
			if (POLY_seperation_weight_writers.size() >= observer_t)
				POLY_seperation_weight_writers.get(index).println(value);
			break;
		case "poly_aw":
			if (POLY_alignment_weight_writers.size() >= observer_t)
				POLY_alignment_weight_writers.get(index).println(value);
			break;
		case "poly_cw":
			if (POLY_cohesion_weight_writers.size() >= observer_t)
				POLY_cohesion_weight_writers.get(index).println(value);
			break;
		case "poly_sns":
			if (POLY_seperation_neighbourhood_size_writers.size() >= observer_t)
				POLY_seperation_neighbourhood_size_writers.get(index).println(value);
			break;
		case "poly_ans":
			if (POLY_alignment_neighbourhood_size_writers.size() >= observer_t)
				POLY_alignment_neighbourhood_size_writers.get(index).println(value);
			break;
		case "poly_cns":
			if (POLY_cohesion_neighbourhood_size_writers.size() >= observer_t)
				POLY_cohesion_neighbourhood_size_writers.get(index).println(value);
			break;
		default:
			System.out.println("error: non existent perspective to write");
			break;
		}
	}

	static String get_file_name(int t1, int t2, String type) {
		return Launcher.getRun_moment() + " " + AI_manager.get_team_ai(t1).getAi_name() + " observing "
				+ AI_manager.get_team_ai(t2).getAi_name() + " " + type;
	}

	// called by the launcher when it shuts down
	public void close() {
		for (PrintWriter w : writers) {
			w.close();
		}
		// cull empty files
		for (int t1 = 0; t1 <= GameManager.getTeam_number()+1; t1++) {
			for (int t2 = 0; t2 <= GameManager.getTeam_number()+1; t2++) {
				String file_name = get_file_name(t1, t2, "param_sw");
				File test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "param_aw");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "param_cw");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "param_sns");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "param_ans");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "param_cns");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "poly_sw");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "poly_aw");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "poly_cw");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "poly_sns");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "poly_ans");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
				file_name = get_file_name(t1, t2, "poly_cns");
				test = new File(file_name + ".txt");
				if (test.length() == 0)
					test.delete();
			}
		}
	}

	public static boolean isOutput_to_file() {
		return output_to_file;
	}

	public static void setOutput_to_file(boolean output_to_file) {
		OutputWriter.output_to_file = output_to_file;
	}
}
