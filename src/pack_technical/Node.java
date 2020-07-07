package pack_technical;

import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Node {
    Node parent;
    List<Node> children;
    int timesVisited = 0;
    int depth;
    double avgEstimatedValue = 0;
    double nodeSimValue;
    double rolloutReward;
    double uct = 0;
    String name;
    PVector accelerationAction;
    ArrayList<BoidGeneric> attackBoids;

    /**
     * Constructor of Node, assigns internal values and initialises storage for children. If not provided, stores a zeroed random acceleration action.
     */
    public Node(double simulationValue, String name, int depth, double rolloutReward, ArrayList<BoidGeneric> attackBoids) {
        this.children = new ArrayList<>();
        this.nodeSimValue = simulationValue;
        this.name = name;
        this.depth = depth;
        this.rolloutReward = rolloutReward;
        this.attackBoids = attackBoids;
        this.accelerationAction = new PVector(0, 0, 0);
    }

    public Node(double simulationValue, String name, int depth, double rolloutReward, ArrayList<BoidGeneric> attackBoids, PVector randomAccelerationAction) {
        this(simulationValue, name, depth, rolloutReward, attackBoids);
        this.accelerationAction = randomAccelerationAction;
    }

    /**
     * Adds a node to the list of children for the calling parent node.
     * @return
     */
    public Node addChild(double simulationValue, String name, double childRolloutValue, ArrayList<BoidGeneric> attackBoids, PVector randomAccelerationAction) {
        Node childNode = new Node(simulationValue, name, this.depth+1, childRolloutValue, attackBoids, randomAccelerationAction);
        childNode.parent = this;
        this.children.add(childNode);
        childNode.backPropagate(); // this being here rather than main algo loop makes things unclear
        return childNode;
    }

    /**
     * Updates the stats of all older generation nodes (father/ grandfather etc) via recursion.
     */
    public void backPropagate() {
        this.avgEstimatedValue = rolloutReward;
        if(children.size() > 0) {
            for (Node child : children) {
                this.avgEstimatedValue += (child.avgEstimatedValue / this.timesVisited);
            }
        }else{
            this.avgEstimatedValue = nodeSimValue;
        }

        updateUCT();
    }


    public void updateUCT() {
        if (parent != null) {
            this.uct = calcUCT(parent.timesVisited);
            parent.backPropagate();
        } else {
            this.uct = calcUCT(this.timesVisited);
        }
    }

    public double calcUCT(int parentVisits) {
        //why is this 2*sqrt(2) rather than 1/sqrt(2) as standard? more exploration required??
        if (parent != null) {
            return this.avgEstimatedValue + ((2*1.414) * (Math.sqrt(2 * Math.log(parentVisits) / (this.timesVisited))));
        } else {
            return this.avgEstimatedValue + ((2*1.414) * (Math.sqrt(2 * Math.log(parentVisits) / (this.timesVisited))));
        }
    }

    public PVector getAccelerationAction() {
        return accelerationAction.copy();
    }

    /**
     * Returns a deep copy of the attacker.
     * @return
     */
    public BoidGeneric getAttackBoids() {
        return new BoidStandard(this.attackBoids.get(0));
    }

    public Node getRandomChild() {
        return children.get((int) (Math.random()*children.size()));
    }

    public List<Node> getChildren() {
        return children;
    }

    public double getUCT() {
        return uct;
    }

    public void incrementTimesVisited() {
        this.timesVisited++;
    }
}