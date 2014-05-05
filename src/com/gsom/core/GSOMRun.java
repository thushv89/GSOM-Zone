package com.gsom.core;

import com.gsom.enums.GSOMMessages;
import com.gsom.enums.InitType;
import com.gsom.enums.InputDataType;
import com.gsom.listeners.GSOMRunListener;
import com.gsom.listeners.InputParsedListener;
import com.gsom.objects.GCluster;
import com.gsom.objects.GNode;
import com.gsom.util.GSOMConstants;
import com.gsom.util.Utils;
import com.gsom.util.input.parsing.InputParser;
import com.gsom.util.input.parsing.InputParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GSOMRun implements InputParsedListener {

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


    public GSOMRun(InitType initType, GSOMRunListener listener) {
        this.listener = listener;
        this.initType = initType;

        parserFactory = new InputParserFactory();
        trainer = new GSOMTrainer(initType);
        adjuster = new GCoordAdjuster();
        smoothner = new GSOMSmoothner();
        tester = new GSOMTester();

    }

    public void runTraining(String fileName, InputDataType type) {

        if (type == InputDataType.FLAGS) {
            parser = parserFactory.getInputParser(InputDataType.FLAGS);
        } else if (type == InputDataType.NUMERICAL) {
            parser = parserFactory.getInputParser(InputDataType.NUMERICAL);
        }
        parser.parseInput(this, fileName);
    }

    private void runAllSteps() {

        GSOMConstants.FD = GSOMConstants.SPREAD_FACTOR / GSOMConstants.DIMENSIONS;

        map = trainer.trainNetwork(parser.getStrForWeights(), parser.getWeights());
        listener.stepCompleted(GSOMMessages.TRAINING_COMPLTETED);

                double maxDist = 0.0;
        for (String k : map.keySet()) {
            ArrayList<double[]> w = new ArrayList<>();
            double localMax = 0.0;
            GNode gn = map.get(k);
            int x = gn.getX();
            int y = gn.getY();
            String lNode = Utils.generateIndexString(x - 1, y);
            if (map.containsKey(lNode)) {
                w.add(map.get(lNode).getWeights());
            }
            String rNode = Utils.generateIndexString(x + 1, y);
            if (map.containsKey(rNode)) {
                w.add(map.get(rNode).getWeights());
            }
            String tNode = Utils.generateIndexString(x, y + 1);
            if (map.containsKey(tNode)) {
                w.add(map.get(tNode).getWeights());
            }
            String bNode = Utils.generateIndexString(x, y - 1);
            if (map.containsKey(bNode)) {
                w.add(map.get(bNode).getWeights());
            }

            for (double[] arr : w) {
                double dist = Utils.calcEucDist(gn.getWeights(), arr, GSOMConstants.DIMENSIONS);
                if (dist > localMax) {
                    localMax = dist;
                }
            }

            if (localMax > maxDist) {
                maxDist = localMax;
            }
        }

        System.out.println("Max val (train) " + maxDist);
        
        map = adjuster.adjustMapCoords(map);
        listener.stepCompleted(GSOMMessages.NODE_ADJUST_COMPLETED);

        smoothner.smoothGSOM(map, parser.getWeights());
        listener.stepCompleted(GSOMMessages.SMOOTHING_COMPLETED);

        tester.testGSOM(map, parser.getWeights(), parser.getStrForWeights());
        this.testResults = tester.getTestResultMap();

        maxDist = 0.0;
        for (String k : map.keySet()) {
            ArrayList<double[]> w = new ArrayList<>();
            double localMax = 0.0;
            GNode gn = map.get(k);
            int x = gn.getX();
            int y = gn.getY();
            String lNode = Utils.generateIndexString(x - 1, y);
            if (map.containsKey(lNode)) {
                w.add(map.get(lNode).getWeights());
            }
            String rNode = Utils.generateIndexString(x + 1, y);
            if (map.containsKey(rNode)) {
                w.add(map.get(rNode).getWeights());
            }
            String tNode = Utils.generateIndexString(x, y + 1);
            if (map.containsKey(tNode)) {
                w.add(map.get(tNode).getWeights());
            }
            String bNode = Utils.generateIndexString(x, y - 1);
            if (map.containsKey(bNode)) {
                w.add(map.get(bNode).getWeights());
            }

            for (double[] arr : w) {
                double dist = Utils.calcEucDist(gn.getWeights(), arr, GSOMConstants.DIMENSIONS);
                if (dist > localMax) {
                    localMax = dist;
                }
            }

            if (localMax > maxDist) {
                maxDist = localMax;
            }
        }

        System.out.println("Max val (smooth) " + maxDist);
    }

    public void runClustering(int distance, double proxWeight) {
        clusterer = new KMeanClusterer2(map, distance, proxWeight);
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



    public ArrayList<ArrayList<GCluster>> getAllClusters() {
        return this.allClusters;
    }

    public int getBestCount() {
        return this.bestCCount;
    }

    public Map<String, double[]> getNodeWeights() {
        Map<String, double[]> weights = new HashMap<String, double[]>();
        for (Map.Entry<String, GNode> entry : this.map.entrySet()) {
            weights.put(entry.getKey(), entry.getValue().getWeights());
        }
        return weights;
    }

}
