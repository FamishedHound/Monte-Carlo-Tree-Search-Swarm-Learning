package pack_technical;

public class Tree {
    //root.depth is always 0
    Node root;
    int maxTreeDepth;
    int maxNodeChildren = 12;

    //todo overload  constructor to make creating a node easier
    public Tree(int maxTreeDepth) {
        this.root = new Node(0, "ROOT", 0, 0);
        this.maxTreeDepth = maxTreeDepth;
    }

    public Node UCT(Node currentNode) {
        if(currentNode.children.size() < maxNodeChildren){
            return currentNode;
        }

        while(true){
            if(currentNode.children.size() < maxNodeChildren){
                return currentNode;
            }

            Node bestNode = null;
            for(Node child : currentNode.children){
                if(bestNode == null){
                    bestNode = child;
                }else if((bestNode.uct < child.uct) && (child.depth < maxTreeDepth + root.depth) && (child.nodeSimValue != -1) && (child.nodeSimValue != 1)){
                    bestNode = child;
                }
            }
            currentNode = bestNode;
        }
    }


    public Node bestAvgVal() {
        if(root.children.size() == 0){
            return root;
        }
        double bestNode = root.children.get(0).avgEstimatedValue;
        int bestNodePos = 0;
        for (int i=0; i<root.children.size()-1; i++){
            if(root.children.get(i).avgEstimatedValue > bestNode){
                bestNode = root.children.get(i).avgEstimatedValue;
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