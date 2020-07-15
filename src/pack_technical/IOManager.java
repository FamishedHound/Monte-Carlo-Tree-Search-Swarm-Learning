package pack_technical;

import pack_1.Launcher;
import pack_1.Launcher.PredictStates;
import pack_boids.BoidStandard;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;

/*
 * handles inputs and outputs, mainly inputs. Input is taken from the launcher class and handed to various managers,
 * for instance telling the display manager do highlight a boid when the flockManager determines a close boid is hovered over.
*/
public class IOManager {

	final int SELECTDISTANCE = 60;
	PVector mouse_pos_vect = new PVector();
	FlockManager flock_ref;
	DisplayManager display_sys_ref;
	GameManager game_sys_ref;
	PApplet parent; // the processing app (allows access to its functions)
	BoidStandard closest_boid = null;
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
				display_sys_ref.highlight_boid(closest_boid, DisplayManager.HOVERVISUALRADIUS);
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
			Launcher.setSimSpeed(PApplet.max(1, Launcher.getSimSpeed() - 1)); // decrease sim speed
		else
			Launcher.setSimSpeed(PApplet.min(50, Launcher.getSimSpeed() + 1));// increase sim speed

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
                if (Launcher.getPredictState() == PredictStates.ALL) {
                    Launcher.setPredictState(PredictStates.NONE);
                } else if (Launcher.getPredictState() == PredictStates.NONE) {
                    Launcher.setPredictState(PredictStates.SELECTED);
                } else if (Launcher.getPredictState() == PredictStates.SELECTED) {
                    Launcher.setPredictState(PredictStates.ALL);
                }
                break;
            case 'g':
            case 'G':
                if (GameManager.selected_boid != null) {
                    PVector dist = mouse_pos_vect.sub(GameManager.getSelected_boid().getLocation());
                    if (dist.magSq() > 900) { // if boid is an adequate distance away (use magSq as more efficient)
                        dist.normalize(); // reduces the 'power' of the pull considerably
                        GameManager.selected_boid.setAcceleration(GameManager.selected_boid.getAcceleration().add(dist));
                    }
                }
                break;
            case '-':
            case '_':
                if (Launcher.getSimSpeed() > 1)
                    Launcher.setSimSpeed(Launcher.getSimSpeed() - 1);
                break;
            case '+':
            case '=':
                if (Launcher.getSimSpeed() < 50)
                    Launcher.setSimSpeed(Launcher.getSimSpeed() + 1);
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
                Launcher.setShowHelpmenu(!Launcher.isHelpmenuShowing());
                break;
            case 'r':
            case 'R':
                GameManager.selected_boid = null;
                parent.setup();
                break;
            case 'a':
            case 'A':
                Launcher.setShowAdvancedMode(!Launcher.isAdvancedModeShowing());
                break;
            case 'f':
            case 'F':
                Launcher.setDrawTrails(!Launcher.areTrailsDrawn());
                break;
            case '#':
                parent.save("BLS"+Launcher.getRun_moment()+parent.frameCount%Integer.MAX_VALUE+".png");
                System.out.println("Took a screenshot at time: "+parent.frameCount%Integer.MAX_VALUE);
                // OutputWriter.setOutput_to_file(true); not currently in use
                break;
            case ' ':
                Launcher.setPaused(!Launcher.isPaused());
                break;
		}
		// special keys
		switch (keyCode) {
            case PConstants.BACKSPACE:
                parent.setup();
                break;
            case PConstants.ESC:
                Launcher.quit("Application closed", 1); // quit properly, closing the file manager
                break;
		}

	}
}
