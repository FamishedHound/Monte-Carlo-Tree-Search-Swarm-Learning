package pack_technical;

import pack_1.Launcher;
import pack_AI.AI_manager;
import pack_boids.Boid_generic;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PVector;

/*
 * Draws many visuals to screen including plenty of UI elements and interactions. Most functions are called
 * by running the system but some (such as highlight boid) are called from the gamemanager if it decides to.
 * render states are held by the launcher.
 */
public class DisplayManager {

	PFont font_1, font_2;
	final int TRANSPARENTALPHA = 100;
	final int GREYCOLOUR = 220;
	final int HOVERVISUALRADIUS = 8;
	final int SELECTVISUALRADIUS = 12;
	final int CURSORSIZE = 5;
	private FlockManager flock_ref = null;
	final int borderoffset = 5;
	final int inneroffset = 2;
	PApplet parent; // the processing app (allows access to its functions)

	public DisplayManager(PApplet p, FlockManager f, PFont f1, PFont f2) {
		parent = p;
		flock_ref = f;
		font_1 = f1;
		font_2 = f2;
	}

	void draw_gui_frames() {
		parent.stroke(GREYCOLOUR);
		parent.fill(40, TRANSPARENTALPHA);
		// top border
		parent.rect(borderoffset, borderoffset, parent.width - borderoffset * 2, borderoffset + 30);
		// bottom border
		parent.rect(inneroffset, parent.height - borderoffset - 30, parent.width - borderoffset, inneroffset + 30);
		// top partitions
		parent.line(inneroffset + 130, borderoffset + 7, inneroffset + 130, borderoffset + 29);
		parent.line(inneroffset + 386, borderoffset + 7, inneroffset + 386, borderoffset + 29);
	}

	void draw_info() {
		parent.textSize(12);
		parent.fill(GREYCOLOUR);
		parent.textAlign(PConstants.LEFT);
		parent.text("Client Width: " + parent.width, borderoffset + inneroffset + 1, 20);
		parent.text("Client Height: " + parent.height, borderoffset + inneroffset + 1, 35);
		parent.text("Selected:", borderoffset + inneroffset + 140, 20);
		if (GameManager.selected_boid != null)
			parent.text("" + GameManager.selected_boid, borderoffset + inneroffset + 140, 35);
		// interaction text
		parent.textFont(font_2);
		parent.text(
				"Press the '?' key to show or hide the list of controls.            Press the 'a' key to toggle advanced detail:",
				borderoffset + inneroffset + 390, 27);
		if (Launcher.isSim_advancedmode()) {
			parent.text("(ON)", borderoffset + inneroffset + 980, 27);
		} else {
			parent.text("(OFF)", borderoffset + inneroffset + 980, 27);
		}
		parent.textFont(font_1);
		// partition
		parent.line(inneroffset + 730, borderoffset + 7, inneroffset + 730, borderoffset + 29);
		// team colour of selected boid
		if (GameManager.selected_boid != null)
			parent.fill(GameManager.selected_boid.get_colour().getRGB());
		parent.text("Team", borderoffset + inneroffset + 335, 20);
	}

	void highlight_boid(Boid_generic b) { // called from IO manager
		parent.noFill();
		parent.stroke(GREYCOLOUR, TRANSPARENTALPHA);
		parent.pushMatrix();
		parent.translate(b.getLocation_history().x, b.getLocation_history().y);
		parent.rotate(parent.frameCount / 35);
		parent.rect(-HOVERVISUALRADIUS, -HOVERVISUALRADIUS, 2 * HOVERVISUALRADIUS, 2 * HOVERVISUALRADIUS);
		parent.popMatrix();
	}

	void super_highlight_boid(Boid_generic b) { // called from game manager
		parent.noFill();
		parent.stroke(GREYCOLOUR, TRANSPARENTALPHA);
		parent.pushMatrix();
		parent.translate(b.getLocation_history().x, b.getLocation_history().y);
		parent.rotate(parent.frameCount / 35);
		parent.rect(-SELECTVISUALRADIUS, -SELECTVISUALRADIUS, 2 * SELECTVISUALRADIUS, 2 * SELECTVISUALRADIUS);
		parent.popMatrix();
	}

	void draw_cursor() {
		parent.stroke(GREYCOLOUR);
		parent.line(parent.mouseX - CURSORSIZE, parent.mouseY, parent.mouseX + CURSORSIZE, parent.mouseY);
		parent.line(parent.mouseX, parent.mouseY - CURSORSIZE, parent.mouseX, parent.mouseY + CURSORSIZE);
	}

	void draw_ai_indicator(PVector pos) {
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.stroke(GREYCOLOUR);
		parent.line(-4, -1.5f, 0, +4);
		parent.line(+4, -1.5f, 0, +4);
		parent.popMatrix();
	}

	void draw_teams() {
		parent.textAlign(PConstants.CENTER);
		for (int i = 0; i < GameManager.getTeam_number(); i++) {
			if (GameManager.get_team(i).size() > 0) {
				parent.fill(GameManager.get_team_colour(i).getRGB());
				parent.textSize(9);
				String ai_name = AI_manager.get_team_ai(i).getAi_name();
				parent.text(ai_name, borderoffset + 35 + (i * 73), parent.height - borderoffset * 4 - 2);
				parent.textSize(12);
				parent.text("| #" + GameManager.get_team(i).size() + " |", borderoffset + 35 + (i * 73),
						parent.height - borderoffset - inneroffset - 2);
			} else {
				parent.fill(GREYCOLOUR);
				parent.textSize(9);
				parent.text("empty", borderoffset + 35 + (i * 73), parent.height - borderoffset * 4 - 2);
				parent.textSize(12);
				parent.text("| #0 |", borderoffset + 35 + (i * 73), parent.height - borderoffset - inneroffset - 2);
			}

		}
	}

	private void draw_writer_status() {
		// currently always on
	}

	void draw_ai_internals() {
		parent.textSize(12);
		parent.fill(GREYCOLOUR);
		parent.textAlign(PConstants.LEFT);
		if (GameManager.selected_boid != null) {
			parent.text(
					"Internal guesses of selected boid with " + GameManager.selected_boid.getAi().getAi_name() + " ai:",
					borderoffset, borderoffset + 55);
			parent.textSize(10);
			if (GameManager.selected_boid != null)
				for (int t = 0; t < GameManager.getTeam_number(); t++) {
					parent.text(
							" t:" + t + ":"
									+ GameManager.selected_boid.getInternal_model().get_ai_description_line_for_team(t),
							borderoffset, borderoffset + 75 + t * 10);
				}
		} else {
			parent.text("No advanced details to view - nothing selected", borderoffset, borderoffset + 55);
		}
	}

	void draw_performance() {
		parent.textSize(12);
		parent.fill(GREYCOLOUR);
		parent.textAlign(PConstants.RIGHT);
		parent.text("Agent Count: " + flock_ref.get_boid_count(), parent.width - borderoffset - inneroffset, 35);
		if (parent.frameRate < Launcher.getSPS() - 5)
			parent.fill(220, 10, 10); // dropping frames gives red colour
		else
			parent.fill(GREYCOLOUR); // standard fps colour
		parent.text("Steps p/s: " + Math.round(parent.frameRate), parent.width - borderoffset - inneroffset, 20);
	}

	void draw_paused() {
		parent.textSize(20);
		parent.fill(GREYCOLOUR);
		parent.textAlign(PConstants.CENTER);
		parent.text("Paused", parent.width / 2, parent.height / 2);
	}

	void draw_help() {
		parent.textSize(12);
		parent.fill(GREYCOLOUR);
		// on the left
		parent.textAlign(PConstants.LEFT);
		if (GameManager.selected_boid != null) {
			parent.text("' x ' key - delete selected", borderoffset, parent.height - 90);
			parent.text("' d ' key -  deselects the current boid", borderoffset, parent.height - 70);
			parent.text("' g ' key -  hold to grab a boid along the mouse path", borderoffset, parent.height - 50);
		}
		// on the right
		parent.textAlign(PConstants.RIGHT);
		parent.text("Mouse Wheel - accelerates or decelerates time", parent.width - borderoffset - 70,
				parent.height - 170);
		parent.text("(" + Launcher.getSimspeed() + ")", parent.width - borderoffset, parent.height - 170);
		parent.text("' l ' key  - changes machine learning settings", parent.width - borderoffset - 70,
				parent.height - 150);
		parent.text("(" + Launcher.getPredict_state() + ")", parent.width - borderoffset, parent.height - 150);
		parent.text("' r ' key -  resets the simulation, clearing all boids", parent.width - borderoffset - 70,
				parent.height - 130);
		parent.text("(" + flock_ref.get_boid_count() + ")", parent.width - borderoffset, parent.height - 130);
		parent.text("' f ' key -  toggles the display of future location", parent.width - borderoffset - 70,
				parent.height - 110);
		if (Launcher.isSim_drawtrails())
			parent.text("(ON)", parent.width - borderoffset, parent.height - 110);
		else
			parent.text("(OFF)", parent.width - borderoffset, parent.height - 110);
		parent.text("Space Bar -  pauses the simulation", parent.width - borderoffset - 70, parent.height - 90);
		if (Launcher.isSim_paused())
			parent.text("(RUNNING)", parent.width - borderoffset, parent.height - 90);
		else
			parent.text("(PAUSED)", parent.width - borderoffset, parent.height - 90);
		parent.text("Left Mouse - selects a boid to view additional options", parent.width - borderoffset,
				parent.height - 70);
		parent.text("Right Mouse - spawn a flock of boids at the position", parent.width - borderoffset,
				parent.height - 50);

	}

	void attempt_draw_ai_indicator() {
		if (GameManager.selected_boid != null) {
			draw_ai_indicator(new PVector(GameManager.selected_boid.getTeam() * 73 + 34 + borderoffset,
					parent.height - borderoffset - 38));
		}

	}

	public void draw() {
		// IO draw procedures are called from IO manager (highlight,super highlight)
		draw_gui_frames();
		attempt_draw_ai_indicator();
		if (Launcher.isSim_paused())
			draw_paused();
		if (Launcher.isSim_helpmenu())
			draw_help();
		if (Launcher.isSim_advancedmode()) {
			draw_ai_internals();
			draw_writer_status();
		}
		draw_info();
		draw_teams();
		draw_performance();
		draw_cursor();
	}
}
