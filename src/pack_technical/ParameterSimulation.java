package pack_technical;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import pack_1.Utility;
import pack_AI.AI_manager;
import pack_AI.AI_type;
import pack_boids.BoidGeneric;
import pack_boids.BoidStandard;
import processing.core.PVector;

import java.util.*;

public class ParameterSimulation extends Thread{
    private boolean once=true;
    private ArrayList<BoidGeneric> defenders;
    private final ArrayList<int[]> pattern;
    private final AI_type currentAi;
    private final Random rand = new Random();
    private PatrollingScheme scheme ;
    private final Map<Integer,ArrayList<BoidGeneric>> observations = new HashMap<>();
    int frameCount=0;
    int innerFrameCount=0;
    Integer nextWaypoint;
    PolynomialCurveFitter fitter ;

    int k =0;
    private final int howManyErrors=20;
    private  double learningRate = 1;
    private double fastLearningRate = 15;
    private final double learningRateParameters=0.002;
    private int begin=0;
    private int end =-1;

    Map<Integer,ArrayList<WeightedObservedPoint>> sepBoidError= new HashMap<>();
    Map<Integer,ArrayList<WeightedObservedPoint>> cohBoidError= new HashMap<>();
    Map<Integer,ArrayList<WeightedObservedPoint>> aliBoidError= new HashMap<>();
    Map<Integer,ArrayList<WeightedObservedPoint>> sepWBoidError= new HashMap<>();
    Map<Integer,ArrayList<WeightedObservedPoint>> cohWBoidError= new HashMap<>();
    Map<Integer,ArrayList<WeightedObservedPoint>> aliWBoidError= new HashMap<>();
    Map<Integer,ArrayList<WeightedObservedPoint>> wayPointForceBoidError= new HashMap<>();

    ArrayList<ArrayList<BoidGeneric>>  stack = new ArrayList<>();

    Map<Integer,Double> sepEst= new HashMap<>();
    Map<Integer,Double> cohEst= new HashMap<>();
    Map<Integer,Double> aliEst= new HashMap<>();
    Map<Integer,Double> sepWEst= new HashMap<>();
    Map<Integer,Double> cohWEst= new HashMap<>();
    Map<Integer,Double> aliWEst= new HashMap<>();
    Map<Integer,Double> wayPointEst= new HashMap<>();

    ArrayList<Integer> resultsInt = new ArrayList<>();
    ArrayList<Float> resultsFloat = new ArrayList<>();

    Map<Integer,PVector> endPositions= new HashMap<>();

    private final ArrayList<PVector> initialLocation = new ArrayList<>();
    private final ArrayList<PVector> endingLocation = new ArrayList<>();

    public ParameterSimulation(ArrayList<BoidGeneric> defenders , ArrayList<int[]> pattern, AI_type currentAi) {
        this.currentAi=currentAi;
        this.scheme= new PatrollingScheme(currentAi.getWayPointForce());
        this.pattern=pattern;
    }

    public void assigntBoidsToTheirSimulations(ArrayList<BoidGeneric> pop , Map<Integer,ArrayList<WeightedObservedPoint>> map ){
        int couter=0;
        for(BoidGeneric g : pop){
            map.put(couter,new ArrayList<WeightedObservedPoint>());
            couter++;
        }
        //System.out.println(" Population before applying " + Arrays.toString(map.get(0).toArray()));
    }
    public void run(){
        setUpCheckPoints();
        learnTheErrors(sepBoidError,1,observations.get(1));
        calculateNewParameter(1);
        learningRate*=0.90 ;
        fastLearningRate*=0.90;
        clearMapping();

        //System.out.println("I have finished with  " + currentAi.getSep_neighbourhood_size() + " " + currentAi.getAli_neighbourhood_size() + " " + currentAi.getCoh_neighbourhood_size() + " " + currentAi.getSep_weight()  + " " + currentAi.getAli_weight() + " " + currentAi.getCoh_weight()+ " " + currentAi.getWayPointForce());

    }


    public void setUpCheckPoints(){

        for(int[] cord : pattern){
            scheme.getWaypoints().add(new PVector(cord[0],cord[1]));
        }
        //basically same as code in simulation
        //FOLLOW THE SIMILLAR WAYPOINT AS DEFENDERS
        float shortestDistanceSq = 3000 * 3000;
        int counter = 0;
        int positionInTheList = 0;
        float shortestVectorAngle=0;
        float nextToShortestVectorAngle=0;
        for(int i=0;i<scheme.getWaypoints().size();i++) {
            PVector checkpoint = scheme.getWaypoints().get(i);
            PVector nextCheckPoint = scheme.getWaypoints().get((i+1)%scheme.getWaypoints().size());

            float distanceSq = Utility.distSq(observations.get(1).get(0).getLocation(), checkpoint);

            // System.out.println(distance);
            if (distanceSq < shortestDistanceSq) {
                shortestDistanceSq = distanceSq;
                positionInTheList = counter;
                shortestVectorAngle = PVector.angleBetween(observations.get(1).get(0).getLocation(), checkpoint);
                nextToShortestVectorAngle = PVector.angleBetween(observations.get(1).get(0).getLocation(), nextCheckPoint);
            }
            counter++;
        }
        //scheme.setup();

        if (shortestVectorAngle < nextToShortestVectorAngle) {
            nextWaypoint = positionInTheList;
        }
        else{
            nextWaypoint = (positionInTheList + 1) % scheme.getWaypoints().size();
        }

        scheme.currentPosition = nextWaypoint;
    }
    public void clearMapping(){
        sepBoidError.clear();
        cohBoidError.clear();
        aliBoidError.clear();
        sepWBoidError.clear();
        cohWBoidError.clear();
        aliWBoidError.clear();
        wayPointForceBoidError.clear();

        sepEst.clear();
        cohEst.clear();
        aliEst.clear();
        sepWEst.clear();
        cohWEst.clear();
        aliWEst.clear();
        wayPointEst.clear();
        observations.clear();

        endPositions.clear();
        initialLocation.clear();
        endingLocation.clear();
        this.scheme = new PatrollingScheme(currentAi.getWayPointForce());
        scheme.setWaypointforce(currentAi.getWayPointForce());
        frameCount=0;
    }

    // TODO - in what way does this calculate distances?
    public void calculateDistance(){
        for (Map.Entry<Integer, ArrayList<BoidGeneric>> entry : observations.entrySet()) {
            if (entry.getKey() == 2) {
                int counter =0;
                for (BoidGeneric def : entry.getValue()) {
                    endPositions.put(counter, def.getLocation());
                    counter++;
                }
            } else if (entry.getKey() == 1) {
                generatePopulationAndMapsForPoints(entry.getValue());
            }
        }
    }

    public void generatePopulationAndMapsForPoints(ArrayList<BoidGeneric> attacker){
        ArrayList<BoidGeneric> sep = copyTheStateOfAttackBoids(attacker,0);
      //  System.out.println(" Population before applying " + Arrays.toString(attacker.toArray()));
        assigntBoidsToTheirSimulations(sep,sepBoidError);

        ArrayList<BoidGeneric> ali = copyTheStateOfAttackBoids(attacker,0);
        assigntBoidsToTheirSimulations(ali,aliBoidError);

        ArrayList<BoidGeneric> coh = copyTheStateOfAttackBoids(attacker,0);
        assigntBoidsToTheirSimulations(coh,cohBoidError);

        ArrayList<BoidGeneric> sepW = copyTheStateOfAttackBoids(attacker,0);
        assigntBoidsToTheirSimulations(sepW,sepWBoidError);

        ArrayList<BoidGeneric> aliW = copyTheStateOfAttackBoids(attacker,0);
        assigntBoidsToTheirSimulations(aliW,aliWBoidError);

        ArrayList<BoidGeneric> cohW = copyTheStateOfAttackBoids(attacker,0);
        assigntBoidsToTheirSimulations(cohW,cohWBoidError);

        ArrayList<BoidGeneric> waypoints = copyTheStateOfAttackBoids(attacker,0);
        assigntBoidsToTheirSimulations(waypoints,wayPointForceBoidError);
    }
    // Mode 1 sep 2 ali 3 coh 4 sepWeight 5 aliW 6 cohW

    public void calculateNewParameter(int mode){
        int counter=0;

        float averageSep=0;

        switch (mode){

            case 1:
                for(Map.Entry<Integer,ArrayList<WeightedObservedPoint>> boid: sepBoidError.entrySet()){
                    fitter=  PolynomialCurveFitter.create(2);

                    int erased = 0;
                    double estimation = 0;
                    double[] coefincients = fitter.fit(boid.getValue());
                    double terms_toal = 0;
                        for (int e = 1; e < 3; e++) {
                            // power of zero can be ignored for calculating the gradient
                            terms_toal = terms_toal + create_new_term(e, coefincients[e], currentAi.getSep_neighbourhood_size());
                        }
                        estimation = currentAi.getSep_neighbourhood_size() - terms_toal * learningRate;
                    if (estimation >= AI_manager.neighbourhoodSeparation_upper_bound || estimation <= AI_manager.neighbourhoodSeparation_lower_bound) {
                            estimation = currentAi.getSep_neighbourhood_size();
                        }
                    sepEst.put(boid.getKey(),estimation);

                }
                for(Map.Entry<Integer,Double> sep : sepEst.entrySet()){
                    averageSep+=sep.getValue();
                }

                resultsInt.add((int)averageSep/sepEst.size());
                currentAi.setSep_neighbourhood_size(averageSep /sepEst.size());
                break;
            case 2:
                for(Map.Entry<Integer,ArrayList<WeightedObservedPoint>> boid: aliBoidError.entrySet()){
                    fitter=  PolynomialCurveFitter.create(2);

                    double[] coefincients = fitter.fit(boid.getValue());
                    double terms_toal = 0;
                    for (int e = 1; e < 3; e++) {
                        // power of zero can be ignored for calculating the gradient
                        terms_toal = terms_toal + create_new_term(e, coefincients[e], currentAi.getAli_neighbourhood_size());
                    }
                    double estimation = currentAi.getAli_neighbourhood_size()-terms_toal*fastLearningRate;
                    if(estimation>= AI_manager.getNeighbourhoodUpperBound() || estimation <= AI_manager.getNeighbourhoodLowerBound()){
                        estimation=currentAi.getAli_neighbourhood_size();
                    }
                    aliEst.put(boid.getKey(),estimation);

                }
                 averageSep=0;
                for(Map.Entry<Integer,Double> sep : aliEst.entrySet()){
                    averageSep+=sep.getValue();
                }
                resultsInt.add((int)averageSep/aliEst.size());
                currentAi.setAli_neighbourhood_size(averageSep /aliEst.size());
                break;
            case 3:
                for(Map.Entry<Integer,ArrayList<WeightedObservedPoint>> boid: cohBoidError.entrySet()){
                    fitter=  PolynomialCurveFitter.create(2);

                    double[] coefincients = fitter.fit(boid.getValue());
                    double terms_toal = 0;
                    for (int e = 1; e < 3; e++) {
                        // power of zero can be ignored for calculating the gradient
                        terms_toal = terms_toal + create_new_term(e, coefincients[e], currentAi.getCoh_neighbourhood_size());
                    }
                    double estimation = currentAi.getCoh_neighbourhood_size()-terms_toal*fastLearningRate;
                    if(estimation>= AI_manager.getNeighbourhoodUpperBound() || estimation <= AI_manager.getNeighbourhoodLowerBound()){
                        estimation=currentAi.getCoh_neighbourhood_size();
                    }
                    cohEst.put(boid.getKey(),estimation);

                }
                averageSep=0;
                for(Map.Entry<Integer,Double> sep : cohEst.entrySet()){
                    averageSep+=sep.getValue();
                }
                resultsInt.add((int)averageSep/cohEst.size());
                currentAi.setCoh_neighbourhood_size(averageSep /cohEst.size());
                break;

            case 4:
                for(Map.Entry<Integer,ArrayList<WeightedObservedPoint>> boid: sepWBoidError.entrySet()){

                    fitter=  PolynomialCurveFitter.create(2);
                    double[] coefincients = fitter.fit(boid.getValue());
                    double terms_toal = 0;
                    for (int e = 1; e < 3; e++) {
                        // power of zero can be ignored for calculating the gradient
                        terms_toal = terms_toal + create_new_term(e, coefincients[e], currentAi.getSep_weight());
                    }
                    double estimation = currentAi.getSep_weight()-terms_toal*learningRateParameters;
                    if(estimation>= 5 || estimation <= 0){
                        estimation=currentAi.getSep_weight();
                    }
                    sepWEst.put(boid.getKey(),estimation);

                }
                averageSep=0;
                for(Map.Entry<Integer,Double> sep : sepWEst.entrySet()){
                    averageSep+=sep.getValue();
                }
                resultsFloat.add(averageSep /sepWEst.size());
                currentAi.setSep_weight(averageSep /sepWEst.size());
                break;

            case 5:
                for(Map.Entry<Integer,ArrayList<WeightedObservedPoint>> boid: aliWBoidError.entrySet()){
                    fitter=  PolynomialCurveFitter.create(2);
                    double[] coefincients = fitter.fit(boid.getValue());
                    double terms_toal = 0;
                    for (int e = 1; e < 3; e++) {
                        // power of zero can be ignored for calculating the gradient
                        terms_toal = terms_toal + create_new_term(e, coefincients[e], currentAi.getAli_weight());
                    }
                    double estimation = currentAi.getAli_weight()-terms_toal*learningRateParameters;
                    if(estimation>= 5 || estimation <= 0){
                        estimation=currentAi.getAli_weight();
                    }
                    aliWEst.put(boid.getKey(),estimation);

                }
                averageSep=0;
                for(Map.Entry<Integer,Double> sep : aliWEst.entrySet()){
                    averageSep+=sep.getValue();
                }
                resultsFloat.add(averageSep /aliWEst.size());
                currentAi.setAli_weight(averageSep /aliWEst.size());
                break;

            case 6:
                for(Map.Entry<Integer,ArrayList<WeightedObservedPoint>> boid: cohWBoidError.entrySet()){

                    fitter=  PolynomialCurveFitter.create(2);
                    double[] coefincients = fitter.fit(boid.getValue());
                    double terms_toal = 0;
                    for (int e = 1; e < 3; e++) {
                        // power of zero can be ignored for calculating the gradient
                        terms_toal = terms_toal + create_new_term(e, coefincients[e], currentAi.getCoh_weight());
                    }
                    double estimation = currentAi.getCoh_weight()-terms_toal*learningRateParameters;
                    if(estimation>= 5 || estimation <= 0){
                        estimation=currentAi.getCoh_weight();
                    }
                    cohWEst.put(boid.getKey(),estimation);

                }
                averageSep=0;
                for(Map.Entry<Integer,Double> sep : cohWEst.entrySet()){
                    averageSep+=sep.getValue();
                }
                resultsFloat.add(averageSep /cohWEst.size());
                currentAi.setCoh_weight(averageSep /cohWEst.size());
                break;

            case 7:
                for(Map.Entry<Integer,ArrayList<WeightedObservedPoint>> boid: wayPointForceBoidError.entrySet()){

                    fitter=  PolynomialCurveFitter.create(2);
                    double[] coefincients = fitter.fit(boid.getValue());
                   // System.out.println("my coefficents " + Arrays.toString(coefincients));
                    double terms_toal = 0;
                    for (int e = 1; e < 3; e++) {
                        // power of zero can be ignored for calculating the gradient
                        terms_toal = terms_toal + create_new_term(e, coefincients[e], currentAi.getWayPointForce());
                    }
                    double estimation = currentAi.getWayPointForce()-terms_toal*10;

                    if(estimation > 0.1f || estimation < 0.01f){
                        estimation=currentAi.getWayPointForce();
                    }
                    wayPointEst.put(boid.getKey(),estimation);

                }

                averageSep=0;
                for(Map.Entry<Integer,Double> sep : wayPointEst.entrySet()){
                    averageSep+=sep.getValue();
                }
                System.out.println("average sep " + averageSep + " size  " +  sepEst.size() + " together " + (int)averageSep/sepEst.size() );
                resultsFloat.add(averageSep /wayPointEst.size());
                currentAi.setWayPointForce(averageSep /wayPointEst.size());
                break;

        }
    }
    private double create_new_term(int exponent, double coeffs, double param_x) {
        // term format... e.g. 5x^4 becomes 5(4x^5);
        // is is the point of the derivative
        // so becomes coeff(exponent*x^exponent)
        double term = coeffs * (exponent * Math.pow(param_x, exponent-1)); // doe abs work?

            return term;
    }

    public static float randFloat(float min, float max) {

        Random rand = new Random();

        float result = rand.nextFloat() * (max - min) + min;

        return result;

    }
    // Mode 1 sep 2 ali 3 coh 4 sepWeight 5 aliW 6 cohW

    public void learnTheErrors(Map<Integer,ArrayList<WeightedObservedPoint>> map,int mode,ArrayList<BoidGeneric> defenders ){

        calculateDistance();
        PatrollingScheme schemeCopy = new PatrollingScheme(currentAi.getWayPointForce());
        for(PVector k : scheme.getWaypoints()) {
            schemeCopy.getWaypoints().add(new PVector(k.x, k.y));
        }

        for(int j=0;j<howManyErrors;j++) {
            schemeCopy.currentPosition = nextWaypoint;
            float xValue =0;
            if (mode == 1) {
                xValue = randFloat(AI_manager.neighbourhoodSeparation_lower_bound,AI_manager.neighbourhoodSeparation_upper_bound);
            } else if(mode>1 && mode <=3){
                xValue = randFloat(AI_manager.getNeighbourhoodLowerBound(),AI_manager.getNeighbourhoodUpperBound());
            } else if (mode==7) {
                xValue = randFloat(0.01f,0.1f);
            } else {
                xValue = randFloat(0.01f,5);
            }
            AI_type sepAi = currentAi;
            ArrayList<BoidGeneric> simulationBoids = copyTheStateOfAttackBoids(defenders,0);
            switch (mode) {
                case 1:
                    sepAi = new AI_type( xValue, currentAi.getAli_neighbourhood_size(), currentAi.getCoh_neighbourhood_size(), currentAi.getSep_weight(), currentAi.getAli_weight(), currentAi.getCoh_weight(),currentAi.getWayPointForce(), ":(");
                    break;
                case 2:
                    sepAi = new AI_type(currentAi.getSep_neighbourhood_size(),  xValue, currentAi.getCoh_neighbourhood_size(),currentAi.getSep_weight(), currentAi.getAli_weight(), currentAi.getCoh_weight(),currentAi.getWayPointForce(), ":(");
                    break;
                case 3:
                    sepAi = new AI_type(currentAi.getSep_neighbourhood_size(), currentAi.getAli_neighbourhood_size(),  xValue,currentAi.getSep_weight(), currentAi.getAli_weight(), currentAi.getCoh_weight(),currentAi.getWayPointForce(), ":(");
                    break;
                case 4:
                    sepAi = new AI_type(currentAi.getSep_neighbourhood_size(), currentAi.getAli_neighbourhood_size(), currentAi.getCoh_neighbourhood_size(), xValue, currentAi.getAli_weight(), currentAi.getCoh_weight(),currentAi.getWayPointForce(), ":(");
                    break;
                case 5:
                    sepAi = new AI_type(currentAi.getSep_neighbourhood_size(), currentAi.getAli_neighbourhood_size(), currentAi.getCoh_neighbourhood_size(), currentAi.getSep_weight(), xValue, currentAi.getCoh_weight(),currentAi.getWayPointForce(), ":(");
                    break;
                case 6:
                    sepAi = new AI_type(currentAi.getSep_neighbourhood_size(), currentAi.getAli_neighbourhood_size(), currentAi.getCoh_neighbourhood_size(),currentAi.getSep_weight(), currentAi.getAli_weight(), xValue,currentAi.getWayPointForce(), ":(");
                    break;
                case 7:
                    sepAi = new AI_type(currentAi.getSep_neighbourhood_size(), currentAi.getAli_neighbourhood_size(), currentAi.getCoh_neighbourhood_size(),currentAi.getSep_weight(), currentAi.getAli_weight(), currentAi.getCoh_weight(),xValue, ":(");
                    break;
            }

            for (int i = 0; i < frameCount; i++) {
                int counter=0;
                for (BoidGeneric def : simulationBoids) {
                    def.setAi(sepAi);
                    PVector acceleration =def.getAcceleration();
                    PVector velocity = def.getVelocity();
                    PVector location = def.getLocation();
                    def.run(simulationBoids, true, true); //Alex Part where he applies all forces

                    velocity.limit(1);
                    //My force
                    location.add(velocity.add(acceleration.add(schemeCopy.patrol(def.getLocation(), def)/*patrolling.patrol(be.getLocation(),be)*/)));
                    if(i==frameCount-1) {
                        WeightedObservedPoint point = new WeightedObservedPoint(1, xValue, PVector.dist(endPositions.get(counter), location));
                        map.get(counter).add(point);
                    }

                    acceleration.mult(0);
                    counter++;
                }
            }
        }
    }

    public int observe(ArrayList<BoidGeneric> defenders) {
        int numFrames = 20;
        if(stack.size()<numFrames) {
            stack.add(copyTheStateOfAttackBoids(defenders,0));
            end = (end + 1) % numFrames;
        } else {
            begin = (begin + 1) % numFrames;
            copyTheStateOfAttackBoids(stack.get(begin),0);
            end = (end + 1) % numFrames;
            stack.set(end,copyTheStateOfAttackBoids(defenders,0));
        }

        if(stack.size()== numFrames && once) {
            frameCount=numFrames;
            ArrayList<BoidGeneric> initialStateForCalculation = stack.get(begin);
            ArrayList<BoidGeneric> endStateForCalculation = stack.get(end);
            observations.put(1, initialStateForCalculation); //initial state
            observations.put(2, endStateForCalculation);//end state
            new Thread(this).start();
            once=false;
        }

        if(observations.size()==0  && !once) {
            k++;
            once=true;
            return 1;
        }

        return 0;
    }

    // TODO - In what way does this update AI? Surely it gets AI.
    public AI_type updateAi(){
        return currentAi;
    }

    public ArrayList<BoidGeneric> copyTheStateOfAttackBoids(ArrayList<BoidGeneric> boids, int mode) {
        ArrayList<BoidGeneric> boidListClone = new ArrayList<>();

        for(BoidGeneric boid : boids){
            //nadaj im tutaj acceleration velocity etc..
            BoidGeneric bi = new BoidStandard(boid.getLocation().x,boid.getLocation().y,6,10);
            bi.setAi(currentAi);

            bi.setAcceleration(boid.getAcceleration());
            bi.setVelocity(boid.getVelocity());
            bi.setLocation(boid.getLocation());
            if(mode==1){
                initialLocation.add(bi.getLocation());
            } else if (mode==2){
                endingLocation.add(bi.getLocation());
            }
            boidListClone.add(bi);
        }
        return boidListClone;
    }

}
