package com.gsom.core;

import com.gsom.enums.GSOMMessages;
import com.gsom.listeners.GSOMRunListener;
import java.util.ArrayList;
import java.util.Map;

import com.gsom.objects.GNode;
import com.gsom.util.GSOMConstants;
import com.gsom.util.Utils;

public class GSOMSmoothner {

    GSOMRunListener rListener;
    public GSOMSmoothner() {
        
        //GSOMConstants.MAX_NEIGHBORHOOD_RADIUS = GSOMConstants.MAX_NEIGHBORHOOD_RADIUS/2;
    }

    public Map<String, GNode> smoothGSOM(Map<String, GNode> map, ArrayList<double[]> inputs) {
        //GSOMConstants.START_LEARNING_RATE = GSOMConstants.START_LEARNING_RATE / 2;
        if(GSOMConstants.START_LEARNING_RATE>0){
            GSOMConstants.START_LEARNING_RATE = (Math.log10(GSOMConstants.START_LEARNING_RATE)/5)+0.25;
        }
        
        if(GSOMConstants.MAX_NEIGHBORHOOD_RADIUS>0){
            GSOMConstants.MAX_NEIGHBORHOOD_RADIUS = (Math.log(GSOMConstants.MAX_NEIGHBORHOOD_RADIUS)*2)+1;
        }
        
        for (int iter = 0; iter < GSOMConstants.MAX_ITERATIONS; iter++) {
            double learningRate = Utils.getLearningRate(iter, map.size());
            double radius = Utils.getRadius(iter, Utils.getTimeConst());
            for (double[] singleInput : inputs) {
                if (singleInput.length == GSOMConstants.DIMENSIONS) {
                    smoothSingleIterSingleInput(map, iter, singleInput, learningRate, radius);
                } else {
                    rListener.stepCompleted(GSOMMessages.SMOOTHING_ERROR);
                }
            }
        }
        return map;
    }

    private void smoothSingleIterSingleInput(Map<String, GNode> map, int iter, double[] input, double learningRate, double radius) {
        GNode winner = Utils.selectWinner(map, input);

        String leftNode = Utils.generateIndexString(winner.getX() - 1, winner.getY());
        String rightNode = Utils.generateIndexString(winner.getX() + 1, winner.getY());
        String topNode = Utils.generateIndexString(winner.getX(), winner.getY() + 1);
        String bottomNode = Utils.generateIndexString(winner.getX(), winner.getY() - 1);

        if (map.containsKey(leftNode)) {
            map.put(leftNode, Utils.adjustNeighbourWeight(map.get(leftNode), winner, input, radius, learningRate));
        } else if (map.containsKey(rightNode)) {
            map.put(rightNode, Utils.adjustNeighbourWeight(map.get(rightNode), winner, input, radius, learningRate));
        } else if (map.containsKey(topNode)) {
            map.put(topNode, Utils.adjustNeighbourWeight(map.get(topNode), winner, input, radius, learningRate));
        } else if (map.containsKey(bottomNode)) {
            map.put(bottomNode, Utils.adjustNeighbourWeight(map.get(bottomNode), winner, input, radius, learningRate));
        }
    }
}
