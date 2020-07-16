package pack_technical;

import processing.core.PVector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PatternImage {


    private final ArrayList<int[]> points = new ArrayList<>();
    private final ArrayList<int[]> newpoints = new ArrayList<>();
    final BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
    final BufferedImage image2 = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics2D = image.createGraphics();
    final Graphics2D graphics2Di = image2.createGraphics();

    public PatternImage() {
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0,0,1000,1000);
        graphics2Di.setPaint(Color.WHITE);
        graphics2Di.fillRect(0,0,1000,1000);
    }

    public void drawPattern() {
        graphics2D.setPaint(Color.BLACK);
        int[] current = null;
        for(int[] cords : points){
            graphics2D.drawOval(cords[0],cords[1],3,3);
        }

        for(int[] cords : points){
            if(current==null){
                current=cords;
            } else {
                graphics2D.drawLine(current[0],current[1],cords[0],cords[1]);
                current=cords;
            }
        }

        graphics2D.dispose();

        simplify(points);
        points.clear();
    }

    public ArrayList<int[]> simplify(ArrayList<int[]> points) {
        ArrayList<int[]> buffer = new ArrayList<>();
        for(int[] cord : points) {
            if(buffer.size()==3 ) {
                double degree = Math.toDegrees(Math.atan2(buffer.get(2)[0] - buffer.get(1)[0], buffer.get(2)[1] - buffer.get(1)[1]) -
                    Math.atan2(buffer.get(0)[0] - buffer.get(1)[0], buffer.get(0)[1] - buffer.get(1)[1]));

                // TODO if the amount is in degrees, why are we adding 2pi?!
                degree+=(Math.PI*2);
                if (Math.abs(180-Math.abs(degree))>10) {
                    newpoints.add(buffer.get(2));
                }
                buffer.remove(0);
            }

            buffer.add(cord);
        }

        graphics2Di.setPaint(Color.BLACK);
        int[] current = null;
        for(int[] cords : newpoints){
            graphics2Di.drawOval(cords[0],cords[1],3,3);
        }

        for(int[] cords : newpoints){
            if(current==null){
                current=cords;
            } else {
                graphics2Di.drawLine(current[0],current[1],cords[0],cords[1]);
                current=cords;
            }
        }

        graphics2Di.dispose();
        return newpoints;
    }


    public ArrayList<int[]> getPoints() {
        return points;
    }

    public void addPoints(PVector point) {

    };

    public ArrayList<int[]> getNewpoints() {
        return newpoints;
    }

    public void clearMe(){
        points.clear();
    }
}
