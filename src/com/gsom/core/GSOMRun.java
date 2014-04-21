package com.gsom.core;

import com.gsom.core.cluster.ClusterQualityEvaluator;
import com.gsom.core.cluster.SilhouetteCoeffEval;
import com.gsom.enums.GSOMMessages;
import com.gsom.enums.InitType;
import com.gsom.enums.InputDataType;
import com.gsom.listeners.GSOMRunListener;
import com.gsom.listeners.InputParsedListener;
import com.gsom.objects.GCluster;
import com.gsom.objects.GNode;
import com.gsom.util.GSOMConstants;
import com.gsom.util.input.parsing.InputParser;
import com.gsom.util.input.parsing.InputParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GSOMRun implements InputParsedListener{

    private InputParserFactory parserFactory;
    private InputParser parser;
    private GSOMTrainer trainer;
    private GCoordAdjuster adjuster;
    private GSOMSmoothner smoothner;
    private GSOMTester tester;
    private KMeanClusterer2 clusterer;
    
    private Map<String, GNode> map;
    private Map<String, String> testResults;
    private ArrayList<ArrayList<GCluster>> allClusters;
    private int bestCCount;
    
    private GSOMRunListener listener;
   
    private InitType initType;
    
    private ClusterQualityEvaluator coeffEval;
    
    public GSOMRun(InitType initType,GSOMRunListener listener) {
        this.listener = listener;
        this.initType = initType;
        
        parserFactory = new InputParserFactory();
        trainer = new GSOMTrainer(initType);
        adjuster = new GCoordAdjuster();
        smoothner = new GSOMSmoothner();
        tester = new GSOMTester();
        
    }

    public void runTraining(String fileName, InputDataType type) {

        if (type==InputDataType.FLAGS) {
            parser = parserFactory.getInputParser(InputDataType.FLAGS);
        } else if (type==InputDataType.NUMERICAL) {
            parser = parserFactory.getInputParser(InputDataType.NUMERICAL);
        }
        parser.parseInput(this, fileName);
    }

    private void runAllSteps(){
        
        GSOMConstants.FD = GSOMConstants.SPREAD_FACTOR/GSOMConstants.DIMENSIONS;
        
        map = trainer.trainNetwork(parser.getStrForWeights(), parser.getWeights());
        listener.stepCompleted(GSOMMessages.TRAINING_COMPLTETED);
        
        map = adjuster.adjustMapCoords(map);
        listener.stepCompleted(GSOMMessages.NODE_ADJUST_COMPLETED);
        
        map = smoothner.smoothGSOM(map, parser.getWeights());
        listener.stepCompleted(GSOMMessages.SMOOTHING_COMPLETED);
        
        tester.testGSOM(map, parser.getWeights(), parser.getStrForWeights());
        this.testResults = tester.getTestResultMap();
        
    }
    
    public void runClustering(int distance,double proxWeight){
        clusterer = new KMeanClusterer2(map,distance,proxWeight);
        this.allClusters = clusterer.getAllClusters();
        
        listener.stepCompleted(GSOMMessages.CLUSTERING_COMPLETED);
        listener.stepCompleted("------------------------------------------------");     
    }

    @Override
    public void inputParseComplete() {
        listener.stepCompleted("Input parsing,normalization completed");
        GSOMConstants.DIMENSIONS = parser.getWeights().get(0).length;
        runAllSteps();
    }

 

    public Map<String, GNode> getGSOMMap() {
        return this.map;
    }

    public Map<String, String> getTestResultMap() {
        return this.testResults;
    }

    public double getSilCoeff(int numCluster){
        //return clusterer.getSilhoutteCoefficient(numCluster);
        coeffEval.evaluate(this.allClusters.get(numCluster-2));
        SilhouetteCoeffEval sCoeffEval = (SilhouetteCoeffEval)coeffEval;
        return sCoeffEval.getSilCoeff();
    }
    
    public ArrayList<ArrayList<GCluster>> getAllClusters() {
        return this.allClusters;
    }

    public int getBestCount(){
        return this.bestCCount;
    }
    
    public Map<String,double[]> getNodeWeights(){
        Map<String,double[]> weights = new HashMap<String, double[]>();
        for(Map.Entry<String,GNode> entry : this.map.entrySet()){
            weights.put(entry.getKey(), entry.getValue().getWeights());
        }
        return weights;
    }
    
}
