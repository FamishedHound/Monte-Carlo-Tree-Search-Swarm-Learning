package pack_technical;

import pack_boids.BoidGeneric;

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
        this.root = new Node(0, "ROOT", 0, 0, attackBoids);
        this.maxTreeDepth = maxTreeDepth;
    }

    public Node UCT(Node currentNode, double epsilon) {
        do {
            if(currentNode.getChildren().size() < maxNodeChildren){
                return currentNode;
            }

            Node selectedNode = currentNode.getRandomChild();
            //logic for epsilon greedy is slightly wrong,
            //not used atm so doesnt matter but should be fixed at some point
//            double randomNum = Math.random();
            for(Node node : currentNode.getChildren()) {
//                if(randomNum < epsilon) {
//                    continue;
//                }
                if(node.getRolloutReward() != 1 && node.getRolloutReward() != -1) {
                    selectedNode = node.calcUCT() > selectedNode.calcUCT() ? node : selectedNode;
                }
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