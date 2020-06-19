package pack_technical;

import pack_boids.Boid_generic;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.LinkedList;


public class Node {
    Node parent;
    LinkedList<Node> children;

    int timesVisited = 1, depth;
    double avgEstimatedValue = 0, nodeSimValue, rolloutValue;
    double uct = 0;
    String name = "Root";

    PVector MrLeandroVector;
    ArrayList<Boid_generic> attacker;

    /**
     * Constructor of Node, assigns internal values and initialises storage for children.
     */
    public Node(double simulationValue, String name, int depth, double rolloutValue) {
        this.children = new LinkedList<Node>();
        this.nodeSimValue = simulationValue;
        this.name = name;
        this.depth = depth;
        this.rolloutValue = rolloutValue;
    }

    /**
     * Adds a node to the list of children for the calling parent node.
     * @return
     */
    public Node addChild(double simulationValue, String name, double childRolloutValue) {
        Node childNode = new Node(simulationValue, name, this.depth+1, childRolloutValue);
        childNode.parent = this;
        this.children.add(childNode);
        childNode.backPropagate(); // this being here rather than main algo loop makes things unclear
        return childNode;
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
            return this.avgEstimatedValue + ((2*1.414) * (Math.sqrt(2 * Math.log(parentVisits+1) / (this.timesVisited))));
        } else {
            return this.avgEstimatedValue + ((2*1.414) * (Math.sqrt(2 * Math.log(parentVisits+1) / (this.timesVisited))));
        }
    }

    /**
     * Updates the stats of all older generation nodes (father/ grandfather etc) via recursion.
     */
    public void backPropagate() {
        this.avgEstimatedValue = rolloutValue;
        this.timesVisited++;
        if(children.size() > 0) {
            for (Node child : children) {
                this.avgEstimatedValue += (child.avgEstimatedValue / children.size());
            }
        }else{
            this.avgEstimatedValue = nodeSimValue;
        }

        updateUCT();
    }


    public void storeDetails(PVector MrLeandroVector, ArrayList<Boid_generic> attacker){
        this.MrLeandroVector = MrLeandroVector;
        this.attacker = attacker;
    }

}