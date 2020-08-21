package pack_technical;

import pack_1.Constants;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Node  {
    private Node parent;
    private PApplet parento;
    private List<Node> children;
    private int visits = 0;
//    private int depth;
    private boolean expanded;
//    private double simulationValue;
    private double cumuValue = 0;
    private double rolloutReward;

    public PVector getAction() {
        return action;
    }

    //    private String name; //debug only
    private PVector action;




    private BoidGeneric attackBoid;
    private ArrayList<BoidGeneric> defenders;
    private InnerSimulation innerSimulation;
    private Random random = new Random();
    /**
     * Constructor of Node, assigns internal values and initialises storage for children. If not provided, stores a zeroed random acceleration action.
     */
    public Node(PVector action, ArrayList<BoidGeneric> defenders, BoidGeneric attackBoids, Node parent) {
        this.children = new ArrayList<>();
        this.defenders = defenders;
        this.parent=parent;
        this.setAttackBoids(attackBoids);
        this.expanded = true;
        this.action = action;
        this.attackBoid = attackBoids;
    }
    public void addChild(Node e){
        this.children.add(e);
    }
    public double simulateRollout(PatrollingScheme patrollingScheme,List<PVector> waypoints, CollisionHandler collisionHandler, AI_type ai,BoidGeneric attacker , ArrayList<BoidGeneric> defenders){
       innerSimulation = new InnerSimulation(parento,patrollingScheme,new BoidStandard(attacker),
           BoidsCloneable.copyStateOfBoids(defenders),waypoints,collisionHandler,action,ai);
       double rolloutValue = innerSimulation.rollout(patrollingScheme);
       this.defenders = innerSimulation.getDefendersState();
       this.attackBoid = innerSimulation.getAttackerState();
       return rolloutValue;
    }

    public Node getParent() {
        return parent;
    }

    public void setCumuValue(double c){
        cumuValue = c;
    }

    public Node getRandomChild() {
        return children.get((int) (Math.random()*children.size()));
    }

    public List<Node> getChildren() {
        return children;
    }



    /**
     * Adds a node to the list of children for the calling parent node.
     * @return
     */




    public int getVisits() {
        return this.visits;
    }

    public void incrementTimesVisited() {
        this.visits++;
    }






    public BoidGeneric getAttackBoidState() {
        return new BoidStandard(this.attackBoid);

    }
    public ArrayList<BoidGeneric> getDefendersBoidState(){
        return BoidsCloneable.copyStateOfBoids(this.defenders);
    }




    public void setAttackBoids(BoidGeneric attackBoid) {
        this.attackBoid = new BoidStandard(attackBoid);
    }

    public double getCumuValue() {
        return cumuValue;
    }



    public void addCumuValue(double cumuValue) {
        this.cumuValue += cumuValue;
    }

    /**
     * Updates the stats of all older generation nodes (father/ grandfather etc) via recursion.
     */

    public void backPropagate(double simVal) {
        Node node = this;
        while (node.getParent() != null) {
            node.incrementTimesVisited();
            node.addCumuValue(simVal);
            node = node.getParent();
        }
        if (node.parent == null) {
            node.incrementTimesVisited();
            node.setCumuValue(node.getChildren()
                    .stream()
                    .mapToDouble(Node::getCumuValue)
                    .sum());
        }

    }


    public  double calcUCT() {
        if (visits==0)  {
           // System.out.println(visits + " "  + Double.MAX_VALUE);
            return Double.MAX_VALUE;
        }
        //System.out.println(visits + " "  +cumuValue / visits + 2*Constants.SQRT2 * Math.sqrt(2 * Math.log(parent.getVisits()) / visits));
        return cumuValue / visits + 2*Constants.SQRT2 * Math.sqrt(2 * Math.log(parent.getVisits()) / visits);
    }



//    @Override
//    public int compareTo(Node o) {
//        if (o.calcUCT() > calcUCT())  return -1;
//        if (o.calcUCT() == calcUCT()) return 0;
//        return 1;
//    }
}
