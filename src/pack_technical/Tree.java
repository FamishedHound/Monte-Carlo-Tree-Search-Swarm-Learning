package pack_technical;

import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Tree {
    //root.depth is always 0
    private Node root;
    private int maxTreeDepth;
    private int maxNodeChildren = 12;

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public void setMaxTreeDepth(int maxTreeDepth) {
        this.maxTreeDepth = maxTreeDepth;
    }

    public int getMaxNodeChildren() {
        return maxNodeChildren;
    }

    public void setMaxNodeChildren(int maxNodeChildren) {
        this.maxNodeChildren = maxNodeChildren;
    }


    public Tree(int maxTreeDepth, ArrayList<BoidGeneric> attackBoids) {
        resetRoot(attackBoids);
        this.maxTreeDepth = maxTreeDepth;
    }

    public void resetRoot(ArrayList<BoidGeneric> attackBoids) {
        this.root = new Node(0, "ROOT", 0, 0, attackBoids);
    }

    public Node addChild(Node node, double simulationValue, String name, double childRolloutValue, ArrayList<BoidGeneric> attackBoids, PVector accelerationAction) {
        Node childNode = node.addChild(simulationValue, name, childRolloutValue, attackBoids, accelerationAction);
        childNode.addChild(Node.Action.TOWARDS_TARGET);
        return childNode;
    }

    public Node UCT(Node currentNode, double epsilon) {
        do {
            if(currentNode.getChildren().size() < maxNodeChildren){
                return currentNode;
            }

            Node selectedNode = currentNode.getRandomChild();
            double randomNum = Math.random();
            for(Node node : currentNode.getChildren()) {
                if(randomNum < epsilon) {
                    continue;
                }
                selectedNode = node.calcUCT() > selectedNode.calcUCT() ? node : selectedNode;
            }
            currentNode = selectedNode;
        } while(true);
    }


    public Node bestAvgVal() {
        if(root.getChildren().size() == 0){
            return root;
        }
        double bestNode = root.getChildren().get(0).calcUCT();
        int bestNodePos = 0;
        for (int i=0; i<root.getChildren().size()-1; i++){
            if(root.getChildren().get(i).calcUCT() > bestNode){
                bestNode = root.getChildren().get(i).calcUCT();
                bestNodePos = i;
            }
            if(root.getChild(i).getNodeSimValue() >= 1){
                return root.getChild(i);
            }
        }
        //System.out.println("Node Name: " + root.children.get(bestNodePos).name);
        return root.getChild(bestNodePos);
    }
}