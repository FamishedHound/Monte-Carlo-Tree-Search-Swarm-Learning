package pack_1;

import processing.core.*;
import processing.event.MouseEvent;
import pack_AI.AI_manager;
import pack_technical.*;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

/*
 * runs the simulation and holds/broadcasts the state
 */
public class                                                              Launcher extends PApplet {




	boolean toBeDisplayed = true;
	static boolean sim_paused = false;
	static boolean sim_helpmenu = false;
	static boolean sim_drawtrails = true;
	static boolean sim_advancedmode = false;

	public enum predictStates { // which boids can see the future?
		NONE, SELECTED, ALL
	}

	static predictStates predict_state = predictStates.SELECTED;
	PFont font_1, font_2;
	final static int HISTORYLENGTH = 0; // (1 second)
	private final static int SPS = 60; // steps per second
	static int simspeed = 1; // time acceleration
	static PVector client_dimensions;
	public static FlockManager flock;
	public ParticleManager particle_sys;
	public DisplayManager display_sys; // created later with fonts
	public static GameManager game_sys;
	public IOManager IO_sys;
	public static OutputWriter file_sys;
	private BaseManager base;
	private CollisionHandler collision;
	private ParameterGatherAndSetter empiricBoy;




	private ZoneDefence zone;
	public ZoneDefence getZone() {
		return zone;
	}

	boolean running=true;

	static int run_moment= (int) System.currentTimeMillis()%100; // helps identify each files name

	public static void main(String[] args) {
		System.out.println("args" + Arrays.toString(args));
		String[] pass = new String[args.length];
		for(int i=0;i<args.length;i++){
			pass[i]=args[i];
		}
		PApplet.main("pack_1.Launcher",pass);
	}

	public void settings() {

		// size(960, 540);
		fullScreen();
		noSmooth();// turns off antialiasing
	}

	public void setup() {
		client_dimensions = new PVector(width, height);
		System.out.println("Client size: " + client_dimensions);
		// create fonts
		font_1 = createFont("Lucida Sans", 12);
		font_2 = createFont("Comic Sans MS", 12);
		// create systems
		new AI_manager();
		flock = new FlockManager(this, true);

		particle_sys = new ParticleManager(this);
		display_sys = new DisplayManager(this, flock, font_1, font_2);
		game_sys = new GameManager(this, flock, display_sys);
		IO_sys = new IOManager(this, flock, display_sys, game_sys,this);

		//file_sys = new OutputWriter();
		base = new BaseManager(this);
		collision = new CollisionHandler(game_sys);


		empiricBoy = new ParameterGatherAndSetter(1900,500,0,0,game_sys,collision,args);
		try {
			zone = new ZoneDefence(base,game_sys,this,collision,flock,empiricBoy);
		} catch (IOException e) {
			e.printStackTrace();
		}


		frameRate(getSPS());
		noCursor();// turns off cursor
		//
	}

	public void draw() {
		// inital font
		if (toBeDisplayed) {
			textFont(font_1);
			// main step event
			background(60);
			collision.checkCollisions();
			try {
				empiricBoy.gather();
			} catch (IOException e) {
				e.printStackTrace();
			}
			base.draw();
			flock.run(simspeed);
			game_sys.run();
			IO_sys.run();
		//	particle_sys.draw();
			try {
				zone.run();
			} catch (IOException e) {
				e.printStackTrace();
			}

			display_sys.draw();


		} else {
			//zone.simulate();
		}



	}

	public void mouseWheel(MouseEvent e) {
		int l = e.getCount();
		IO_sys.on_mouse_wheel(l);

	}

	public static int getSps() {
		return SPS;
	}

	public PFont getFont_1() {
		return font_1;
	}

	public PFont getFont_2() {
		return font_2;
	}

	public ParticleManager getParticle_sys() {
		return particle_sys;
	}

	public static OutputWriter getFile_sys() {
		return file_sys;
	}

	public DisplayManager getDisplay_sys() {
		return display_sys;
	}

	public GameManager getGame_sys() {
		return game_sys;
	}

	public IOManager getIO_sys() {
		return IO_sys;
	}

	public void setFlock(FlockManager flock) {
		Launcher.flock = flock;
	}

	public void keyPressed() {
		if (key != CODED) {
			IO_sys.on_key_pressed(key, keyCode);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (mouseButton == LEFT) {
			IO_sys.on_left_click(e);
		}
		if (mouseButton == RIGHT) {
			IO_sys.on_right_click(e);
		}
	}

	public static int getSPS() {
		return SPS;
	}

	public static boolean isSim_paused() {
		return sim_paused;
	}

	public static boolean isSim_helpmenu() {
		return sim_helpmenu;
	}

	public static boolean isSim_drawtrails() {
		return sim_drawtrails;
	}

	public static boolean isSim_advancedmode() {
		return sim_advancedmode;
	}

	public static void setSim_paused(boolean sim_paused) {
		Launcher.sim_paused = sim_paused;
	}

	public static void setSim_helpmenu(boolean sim_helpmenu) {
		Launcher.sim_helpmenu = sim_helpmenu;
	}

	public static void setSim_drawtrails(boolean sim_drawtrails) {
		Launcher.sim_drawtrails = sim_drawtrails;
	}

	public static void setSim_advancedmode(boolean sim_advancedmode) {
		Launcher.sim_advancedmode = sim_advancedmode;
	}

	public static PVector getClient_dimensions() {
		return client_dimensions;
	}

	public static predictStates getPredict_state() {
		return predict_state;
	}

	public static void setPredict_state(predictStates predict_state) {
		Launcher.predict_state = predict_state;
	}

	public static FlockManager getFlock() {
		return flock;
	}

	public static int getSimspeed() {
		return simspeed;
	}

	public static int getRun_moment() {
		return run_moment;
	}

	public static void setSimspeed(int simspeed) {
		Launcher.simspeed = simspeed;
	}

	public static void quit(int code) {
		file_sys.close();
		System.out.println("Program has terminated");
		System.exit(code);
	}

	public static int getHISTORYLENGTH() {
		return HISTORYLENGTH;
	}
	public boolean isRunning() {
		return running;
	}
	public  boolean isToBeDisplayed() {
		return toBeDisplayed;
	}
	public void setToBeDisplayed(boolean toBeDisplayed) {
		this.toBeDisplayed = toBeDisplayed;
	}

}
