package pack_technical;

import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Node {
    Node parent;
    List<Node> children;
    int visits = 0;
    int depth;
    double avgEstimatedValue = 0;
    double nodeSimValue;
    double cumuValue = 0;
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

    public Node getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public double getAvgEstimatedValue() {
        return avgEstimatedValue;
    }

    public double getNodeSimValue() {
        return nodeSimValue;
    }

    public double getRolloutReward() {
        return rolloutReward;
    }

    public int getVisits() {
        return this.visits;
    }

    public void incrementTimesVisited() {
        this.visits++;
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

    /**
     * Adds a node to the list of children for the calling parent node.
     * @return
     */
    public Node addChild(double simulationValue, String name, double childRolloutValue, ArrayList<BoidGeneric> attackBoids, PVector randomAccelerationAction) {
        Node childNode = new Node(simulationValue, name, this.depth+1, childRolloutValue, attackBoids, randomAccelerationAction);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    public double getUCT() {
        return uct;
    }

    public double getCumuValue() {
        return cumuValue;
    }

    public void setCumuValue(double cumuValue) {
        this.cumuValue = cumuValue;
    }

    public void addCumuValue(double cumuValue) {
        this.cumuValue += cumuValue;
    }

    /**
     * Updates the stats of all older generation nodes (father/ grandfather etc) via recursion.
     */
    public void backPropagate(double simVal) {
        Node node = this;
        while (node.parent != null) {
            node.addCumuValue(simVal);
            node = this.parent;
        }
        if (node.parent == null) {
            node.setCumuValue(node.getChildren()
                    .stream()
                    .mapToDouble(Node::getCumuValue)
                    .sum());
        }
    }

    public void backPropagate() {
        this.avgEstimatedValue = rolloutReward;
        if(children.size() > 0) {
            for (Node child : children) {
                this.avgEstimatedValue += (child.avgEstimatedValue / this.visits);
            }
        }else{
            this.avgEstimatedValue = nodeSimValue;
        }

        updateUCT();
    }


    public void updateUCT() {
        if (parent != null) {
            this.uct = calcUCT(parent.visits);
            parent.backPropagate();
        } else {
            this.uct = calcUCT(this.visits);
        }
    }

    public double calcUCT(int parentVisits) {
        //why is this 2*sqrt(2) rather than 1/sqrt(2) as standard? more exploration required??
        if (parent != null) {
            return this.avgEstimatedValue + ((2*1.414) * (Math.sqrt(2 * Math.log(parentVisits) / (this.visits))));
        } else {
            return this.avgEstimatedValue + ((2*1.414) * (Math.sqrt(2 * Math.log(parentVisits) / (this.visits))));
        }
    }

    public double calcUCT() {
        int visits = (this.getVisits() == 0) ? 1 : this.getVisits();
        if (this.getParent() == null) {
            return this.getCumuValue() / visits + ((2 * 1.414) * (1.414));
        }
            return this.getCumuValue() / visits + 2 * 1.414 * Math.sqrt(2 * Math.log(this.getParent().getVisits()-1) / this.getVisits());
    }


}