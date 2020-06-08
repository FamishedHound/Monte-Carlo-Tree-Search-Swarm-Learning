package pack_technical;

//import javafx.scene.shape.Ellipse;
import processing.core.PApplet;

import java.awt.*;
import java.util.Random;

public class BaseManager {
    private PApplet app;

    public BaseManager(PApplet p){
        this.app = p;

    }
    public void draw(){
        //_______________________________Circle
        /*app.fill(105,105,105);
        app.ellipse(150f,500f,300f,300f);
        app.fill(255,0,0);

        app.rect(150,500f,10f,10f);
        Random rand = new Random();
        int a = rand.nextInt(100)+1;
        */
        // target not needed if circle is back
        app.fill(255,0,0);
        app.rect(550,500f,10f,10f);

        // different edges Triangle
        app.fill(105,105,105);
        app.line(450,800,850,500);
        app.line(850,500,450,100);
        app.line(450,100,450,800);
    }

}
