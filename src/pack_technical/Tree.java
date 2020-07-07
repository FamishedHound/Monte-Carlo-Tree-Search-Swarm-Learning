package pack_technical;

import pack_boids.BoidGeneric;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Tree {
    //root.depth is always 0
    Node root;
    int maxTreeDepth;
    int maxNodeChildren = 12;


    public Tree(int maxTreeDepth, ArrayList<BoidGeneric> attackBoids) {
        this.root = new Node(0, "ROOT", 0, 0, attackBoids);
        this.maxTreeDepth = maxTreeDepth;
    }

    public Node UCT(Node currentNode, double epsilon) {
        do {
            currentNode.incrementTimesVisited();
            if(currentNode.children.size() < maxNodeChildren){
                return currentNode;
            }

            if(Math.random() < epsilon) {
                currentNode = currentNode.getRandomChild();
                continue;
            }

            currentNode = currentNode.getChildren()
                    .stream()
                    .min(Comparator.comparingDouble(Node::calcUCT))
                    .orElseThrow(Error::new);
        } while(true);
    }


    public Node bestAvgVal() {
        if(root.children.size() == 0){
            return root;
        }
        double bestNode = root.children.get(0).calcUCT();
        int bestNodePos = 0;
        for (int i=0; i<root.children.size()-1; i++){
            if(root.children.get(i).calcUCT() > bestNode){
                bestNode = root.children.get(i).calcUCT();
                bestNodePos = i;
            }
            if(root.children.get(i).nodeSimValue >= 1){
                return root.children.get(i);
            }
        }
        //System.out.println("Node Name: " + root.children.get(bestNodePos).name);
        return root.children.get(bestNodePos);
    }
}