package pack_technical;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PatternImage {


    private ArrayList<int[]> points = new ArrayList<>();

    public ArrayList<int[]> getNewpoints() {
        return newpoints;
    }

    private ArrayList<int[]> newpoints = new ArrayList<>();
    final BufferedImage image = new BufferedImage ( 1000, 1000, BufferedImage.TYPE_INT_ARGB );
    final BufferedImage image2 = new BufferedImage ( 1000, 1000, BufferedImage.TYPE_INT_ARGB );

        final Graphics2D graphics2D = image.createGraphics ();
    final Graphics2D graphics2Di = image2.createGraphics ();
        Random rand = new Random();
        private int counter=0;
        public PatternImage(){
            graphics2D.setPaint ( Color.WHITE );
            graphics2D.fillRect ( 0,0,1000,1000 );
            graphics2Di.setPaint ( Color.WHITE );
            graphics2Di.fillRect ( 0,0,1000,1000 );
        }
        public void drawPattern() throws IOException {
            graphics2D.setPaint ( Color.BLACK );
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




            //graphics2D.drawOval(5, 5, 100, 100);

            graphics2D.dispose ();



            //ImageIO.write ( image, "png", new File( "output/" + counter + rand.nextInt(10) + 1 + rand.nextInt(100)+".jpeg" ) );
            simplify(points);
            points.clear();
            counter++;
        }

        public ArrayList<int[]> simplify(ArrayList<int[]> points) throws IOException {
            ArrayList<int[]> buffer = new ArrayList<>();
                boolean flag = false;

                for(int[] cord : points){
                    if(buffer.size()==3 ) {

                        double degree = Math.toDegrees(Math.atan2(buffer.get(2)[0] - buffer.get(1)[0], buffer.get(2)[1] - buffer.get(1)[1]) -
                                Math.atan2(buffer.get(0)[0] - buffer.get(1)[0], buffer.get(0)[1] - buffer.get(1)[1]));

                        degree+=(Math.PI*2);
                        //System.out.println("degree " + Math.abs(180-Math.abs(degree)));
                        if (Math.abs(180-Math.abs(degree))<=10) {
                           // System.out.println("I am in");
                            //newpoints.add(buffer.get(0));
                           // newpoints.add(buffer.get(2));

                        } else {
                            //newpoints.addAll(buffer);
                            newpoints.add(buffer.get(2));
                        }
                        buffer.remove(0);
                    }


                        buffer.add(cord);
                    }

            graphics2Di.setPaint ( Color.BLACK );
            int[] current = null;
          //  System.out.println(" new Points" + newpoints);
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

            graphics2Di.dispose ();
            //ImageIO.write ( image2, "png", new File( "output/" + "TRANSLATED" + rand.nextInt(10) + 1 + rand.nextInt(100)+".jpeg" ));
            return newpoints;
        }



        public void clearMe(){
            points.clear();
        }
    public ArrayList<int[]> getPoints() {
        return points;
    }

}
