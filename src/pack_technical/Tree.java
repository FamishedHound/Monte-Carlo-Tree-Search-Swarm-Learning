package pack_technical;

import pack_boids.BoidGeneric;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Tree {
    //root.depth is always 0
    private Node root;
    private int maxTreeDepth;
    private int maxNodeChildren = 12;


    public Node getRoot() {
        return root;
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


    public Tree(int maxTreeDepth, BoidGeneric attackBoid) {
        resetRoot(attackBoid);
        this.maxTreeDepth = maxTreeDepth;
    }

    public void resetRoot(BoidGeneric attackBoid) {
        this.root = new Node(0, "ROOT", 0, 0, attackBoid);
        this.root.addPresetActionNode(Node.Action.TOWARDS_TARGET);
    }

    public Node addChild(Node node, InnerSimulation innerSimulation) {
        Node childNode = node.addChild(innerSimulation);
        childNode.addPresetActionNode(Node.Action.TOWARDS_TARGET);
        return childNode;
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
                if(node.getSimulationValue() != 1 && node.getSimulationValue() != -1) {
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

        double highestUctWinner = 0;
        double highestUctNeutral = 0;
        List<Node> rootChildrenNodes = root.getChildren();
        Node highestUctWinnerNode = rootChildrenNodes.get(0) ;
        Node highestUctNeutralNode= rootChildrenNodes.get(0);


        for (Node n : rootChildrenNodes){
            if (n.getSimulationValue()==1){
                double currentUctWinner = n.calcUCT();
                if (currentUctWinner > highestUctWinner) {highestUctWinner=currentUctWinner;highestUctWinnerNode = n;}


            } else if (n.getSimulationValue()!=-1){
                double currentUctNeutral = n.calcUCT();
                if (currentUctNeutral > highestUctNeutral){ highestUctNeutral=currentUctNeutral;highestUctNeutralNode=n;}
            }
        }

        if (highestUctWinner!=0){
            return highestUctWinnerNode;
        } else if (highestUctNeutral!=0){
            return highestUctNeutralNode;
        }


        return root;
    }
}