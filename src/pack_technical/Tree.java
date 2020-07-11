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
            if(currentNode.children.size() < maxNodeChildren){
                return currentNode;
            }

            Node selectedNode = currentNode.getRandomChild();
            double randomNum = Math.random();
            for(Node node : currentNode.getChildren()) {
                if(randomNum < epsilon) {
                    continue;
                }
                selectedNode = node.calcUCT() < selectedNode.calcUCT() ? node : selectedNode;
            }
            currentNode = selectedNode;

//            Node max = currentNode.getChildren()
//                    .stream()
//                    .max(Comparator.comparingDouble(Node::calcUCT))
//                    .orElseThrow(Error::new);
//            System.out.println("max " + max.name + ", " +
//                            "visit: " + max.getVisits() +
//                            ", parVisits: " + max.getParent().getVisits() +
//                            ", cumVal: " + max.getCumuValue() +
//                            ", a_x: " + max.getAccelerationAction().x +
//                            ", a_y: " + max.getAccelerationAction().y +
//                            ", UCT " + max.calcUCT());
//
//            Node min = currentNode.getChildren()
//                    .stream()
//                    .min(Comparator.comparingDouble(Node::calcUCT))
//                    .orElseThrow(Error::new);
//            System.out.println("min, " + max.name + ", " +
//                    "visit: " + min.getVisits() +
//                    ", parVisit: " + min.getParent().getVisits() +
//                    ", cumVal: " + min.getCumuValue() +
//                    ", a_x: " + min.getAccelerationAction().x +
//                    ", a_y: " + min.getAccelerationAction().y +
//                    ", UCT " + min.calcUCT() + "\n");
//
//            currentNode = max;
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
        }
        //System.out.println("Node Name: " + root.children.get(bestNodePos).name);
        return root.children.get(bestNodePos);
    }
}