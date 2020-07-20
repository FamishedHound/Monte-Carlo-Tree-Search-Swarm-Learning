package pack_technical;

import pack_1.Constants;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;



public class Node {
    private Node parent;
    private List<Node> children;
    private int visits = 0;
    private int depth;
    private boolean expanded = false;
    private boolean maxChildren;
    private double nodeSimValue;
    private double cumuValue = 0;
    private double rolloutReward;
    private String name; //debug only
    private PVector accelerationAction;
    private ArrayList<BoidGeneric> attackBoids;

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

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getRandomChild() {
        return children.get((int) (Math.random()*children.size()));
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getChild(int i) {
        return children.get(i);
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

    public int getVisits() {
        return this.visits;
    }

    public void incrementTimesVisited() {
        this.visits++;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isMaxChildren() {
        return maxChildren;
    }

    public void setMaxChildren(boolean maxChildren) {
        this.maxChildren = maxChildren;
    }

    public double getNodeSimValue() {
        return nodeSimValue;
    }

    public void setNodeSimValue(double nodeSimValue) {
        this.nodeSimValue = nodeSimValue;
    }

    public double getRolloutReward() {
        return rolloutReward;
    }

    public void setRolloutReward(double rolloutReward) {
        this.rolloutReward = rolloutReward;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PVector getAccelerationAction() {
        return accelerationAction.copy();
    }

    public void setAccelerationAction(PVector accelerationAction) {
        this.accelerationAction = accelerationAction;
    }

    /**
     * Returns a deep copy of the attacker.
     * @return
     */
    public ArrayList<BoidGeneric> getAttackBoids() {
        return this.attackBoids;
    }

    public void setAttackBoids(BoidGeneric attackBoids) {
        this.attackBoids.add(0, attackBoids);
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
            node.incrementTimesVisited();
            node.addCumuValue(simVal);
            node = node.parent;
        }
        if (node.parent == null) {
            node.incrementTimesVisited();
            node.setCumuValue(node.getChildren()
                    .stream()
                    .mapToDouble(Node::getCumuValue)
                    .sum());
        }
    }

    public double calcUCT() {
        if (this.getParent() == null || this.visits == 0) {
            //edge case for the root node; ucb is meaningless for the root so just return 0
            return Double.POSITIVE_INFINITY;
        }
            return this.getCumuValue() / visits + 1/Constants.SQRT2 * Math.sqrt(2 * Math.log(this.getParent().getVisits()) / visits);
    }


}