package pack_technical;

import pack_1.Constants;
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
    double nodeSimValue;
    double cumuValue = 0;
    double rolloutReward;
    String name; //debug only
    PVector accelerationAction;
    ArrayList<BoidGeneric> attackBoids;

    /**
     * Constructor of Node, assigns internal values and initialises storage for children. If not provided, stores a zeroed random acceleration action.
     */
    public Node(double simulationValue, String name, int depth, double rolloutReward, ArrayList<BoidGeneric> attackBoids) {
        this.children = new ArrayList<>();
        this.nodeSimValue = simulationValue;
        //this.name = name;
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
            node = node.parent;
        }
        if (node.parent == null) {
            node.setCumuValue(node.getChildren()
                    .stream()
                    .mapToDouble(Node::getCumuValue)
                    .sum());
        }
    }

    public double calcUCT() {
        //todo - the following line handles the edge case where we have divide by zero=Infty at the start
        //which results in very bad performance this is due to how Tree.bestAvgVal decides action to take
        //based on ucb1 of direct descendents from root only. fix pls
        int visits = (this.getVisits() == 0) ? 1 : this.getVisits();
        if (this.getParent() == null) {
            //edge case for the root node; ucb is meaningless for the root so just return 0
            return 0;
        }
            return this.getCumuValue() / visits + 2 * Constants.SQRT2 * Math.sqrt(2 * Math.log(this.getParent().getVisits()-1) / visits);
    }


}