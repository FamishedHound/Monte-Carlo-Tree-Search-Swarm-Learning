package pack_technical;

import pack_1.Launcher;
import pack_1.Launcher.predictStates;
import pack_boids.Boid_generic;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;

/*
 * handles inputs and outputs, mainly inputs. Input is taken from the launcher class and handed to various managers,
 * for instance telling the display manager do highlight a boid when the flock determines a close boid is hovered over.
*/
public class IOManager {

	final int SELECTDISTANCE = 60;
	PVector mouse_pos_vect = new PVector();
	FlockManager flock_ref;
	DisplayManager display_sys_ref;
	GameManager game_sys_ref;
	PApplet parent; // the processing app (allows access to its functions)
	Boid_generic closest_boid = null;
	Launcher launcher;
	int amountBoidsToSpawn=10;
	boolean flag = true;
	public IOManager(PApplet p, FlockManager f, DisplayManager d, GameManager g,Launcher l) {
		this.launcher = l;
		parent = p;
		flock_ref = f;
		display_sys_ref = d;
		game_sys_ref = g;
	}

	public void attempt_highlight_closest() {
		if (flock_ref.get_boid_count() > 0) {
			closest_boid = flock_ref.get_nearest_boid(SELECTDISTANCE);
			if (closest_boid != null)
				display_sys_ref.highlight_boid(closest_boid);
		}
	}

	public void run() {
		get_mouse_pos_vect();
		attempt_highlight_closest();
	}

	private void get_mouse_pos_vect() {
		mouse_pos_vect = new PVector(parent.mouseX, parent.mouseY);
	}

	public void on_mouse_wheel(int l) {
		if (l > 0 && l != 0)
			Launcher.setSimspeed(PApplet.max(1, Launcher.getSimspeed() - 1)); // decrease sim speed
		else
			Launcher.setSimspeed(PApplet.min(50, Launcher.getSimspeed() + 1));// increase sim speed

	}

	public void on_left_click(MouseEvent e) {
		GameManager.selected_boid = closest_boid;
	}

	public void on_right_click(MouseEvent e) {
		if(flag) {
			amountBoidsToSpawn=20;
			flag=false;
		} else {
			amountBoidsToSpawn=2;
		}
		System.out.println(flag);
		game_sys_ref.spawn_boids(GameManager.get_random_team(), amountBoidsToSpawn, mouse_pos_vect);
	}

	public void on_key_pressed(char key, int keyCode) {
		// letter input
		switch (key) {
		case 'l':
		case 'L':
			if (Launcher.getPredict_state() == predictStates.ALL) {
				Launcher.setPredict_state(predictStates.NONE);
				break;
			}
			if (Launcher.getPredict_state() == predictStates.NONE) {
				Launcher.setPredict_state(predictStates.SELECTED);
				break;
			}
			if (Launcher.getPredict_state() == predictStates.SELECTED) {
				Launcher.setPredict_state(predictStates.ALL);
				break;
			}
			break;
		case 'g':
		case 'G': // technically grabs the future position
			if (GameManager.selected_boid != null) {
				PVector dist = mouse_pos_vect.sub(GameManager.getSelected_boid().get_future_location());
				if (dist.magSq() > 900) { // if boid is an adequate distance away (use magSq as more efficient)
					dist.normalize(); // reduces the 'power' of the pull considerably
					GameManager.selected_boid.setAcceleration(GameManager.selected_boid.getAcceleration().add(dist));
				}
			}
			break;
		case '-':
		case '_':
			if (Launcher.getSimspeed() > 1)
				Launcher.setSimspeed(Launcher.getSimspeed() - 1);
			break;
		case '+':
		case '=':
			if (Launcher.getSimspeed() < 50)
				Launcher.setSimspeed(Launcher.getSimspeed() + 1);
			break;
		case 'x':
			launcher.setToBeDisplayed(false);
			break;
		case 'X':
			game_sys_ref.delete_selected();
			break;
		case 'd':

			launcher.setToBeDisplayed(true);
			break;
		case 'D':
			GameManager.selected_boid = null;
			break;
		case '/':
		case '?':
			Launcher.setSim_helpmenu(!Launcher.isSim_helpmenu());
			break;
		case 'r':

		case 'R':
			GameManager.selected_boid = null;
			parent.setup();
			break;
		case 'a':
		case 'A':
			Launcher.setSim_advancedmode(!Launcher.isSim_advancedmode());
			break;
		case 'f':
		case 'F':
			Launcher.setSim_drawtrails(!Launcher.isSim_drawtrails());
			break;
		case '#':
			parent.save("BLS"+Launcher.getRun_moment()+parent.frameCount%Integer.MAX_VALUE+".png");
			System.out.println("Took a screenshot at time: "+parent.frameCount%Integer.MAX_VALUE);
			// OutputWriter.setOutput_to_file(true); not currently in use
			break;
		case ' ':
			Launcher.setSim_paused(!Launcher.isSim_paused());
			break;
		}
		// special keys
		switch (keyCode) {
		case PConstants.BACKSPACE:
			parent.setup();
			break;
		case PConstants.ESC:
			Launcher.quit(1); // quit properly, closing the file manager
			break;
		}

	}
}
