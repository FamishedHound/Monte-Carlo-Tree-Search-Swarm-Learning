package pack_technical;

import pack_1.Constants;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import processing.core.PVector;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tree {
    //root.depth is always 0
    private Node root;
    private int maxTreeDepth;
    private int maxNodeChildren = 12;
    private Node rootNode;
    private Random random = new Random();
    public Node getRoot() {
        return root;
    }
    private BoidGeneric attackBoid;
    private ArrayList<BoidGeneric> defenders;
    private ArrayList<Node> children;
    private List<PVector> waypoints;
    AI_type simulation_ai;
    CollisionHandler collisionHandler;
    PatrollingScheme patrollingScheme;
    public Tree(PatrollingScheme patrollingScheme, BoidGeneric attackBoid, ArrayList<BoidGeneric> defenders, AI_type simulation_ai, List<PVector> waypoints, CollisionHandler collisionHandler) {
        this.attackBoid = attackBoid;
        this.defenders= defenders;
        this.waypoints = waypoints;
        this.simulation_ai=simulation_ai;
        this.collisionHandler = collisionHandler;
        rootNode=new Node(null,defenders,attackBoid,null);
        //generateChildren(rootNode);
        this.patrollingScheme = patrollingScheme;
    }

    public PVector[] getPossibleActions(int noAction){

        PVector[] possibleAction = new PVector[noAction];
        for (int i=0 ; i<noAction-1;i++){
            possibleAction[i] = createRandomVector();
        }
        possibleAction[noAction-1] = PVector.sub(Constants.TARGET, attackBoid.getLocation()).setMag(Constants.Boids.MAX_ACC_ATTACK);
        return possibleAction;
    }
    PVector createRandomVector() {
        PVector randomAcceleration = new PVector(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1);
        return randomAcceleration.setMag(Constants.Boids.MAX_ACC_ATTACK);
    }
    public void generateChildren(Node n){
        for (PVector action : getPossibleActions(2)){
            n.addChild(new Node(action,n.getDefendersBoidState(),n.getAttackBoidState(),n));
        }
    }
    public void iterateTree(){
        Node selection = findNodeToRollout();
        double value = selection.simulateRollout(patrollingScheme,waypoints,collisionHandler,simulation_ai,
               selection.getParent().getAttackBoidState(),selection.getParent().getDefendersBoidState());
        System.out.println(selection + " value = " + value);
        selection.backPropagate(value);

    }

    public Node findNodeToRollout(){
        if (rootNode.getChildren().isEmpty()){
            return continueExpansion(rootNode);
        }
       Node selectedNode =  selectionForExpansion(rootNode);
       while (!selectedNode.getChildren().isEmpty()){
            if (selectedNode.getVisits() == 0)
                return selectedNode;
           selectedNode =  selectionForExpansion(selectedNode);
       }
       selectedNode = continueExpansion(selectedNode);
       return selectedNode;
    }

    public Node selectionForExpansion(Node currentNode) {
        Stream<Node> stream = currentNode.getChildren().stream();
        Optional<Node> nodeToConsider = stream.collect(Collectors.maxBy(Comparator.comparing(Node::calcUCT)));

        return nodeToConsider.get();
    }

    public Node continueExpansion(Node n){
        generateChildren(n);
        return selectionForExpansion(n);
    }

    public Node selectOptimalAction() {
//        Stream<Node> stream = rootNode.getChildren().stream();
//        Node toReturn  = stream.collect(Collectors.maxBy(Comparator.comparing(Node::calcUCT))).get();
        double best = 0;
        Node nodeWin = rootNode.getChildren().get(0);
        for(Node n : rootNode.getChildren()){
            if (n.calcUCT()>best){
                best = n.calcUCT();
                nodeWin = n;
            }
        }
        double heh = nodeWin.getCumuValue();
        return nodeWin;
    }




}