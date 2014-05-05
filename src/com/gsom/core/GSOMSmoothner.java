package com.gsom.core;

import com.gsom.enums.GSOMMessages;
import com.gsom.listeners.GSOMRunListener;
import java.util.ArrayList;
import java.util.Map;

import com.gsom.objects.GNode;
import com.gsom.util.GSOMConstants;
import com.gsom.util.Utils;
import java.util.HashMap;

public class GSOMSmoothner {

    
    
    GSOMRunListener rListener;
    public GSOMSmoothner() {
        
    }

    public void smoothGSOM(Map<String, GNode> map, ArrayList<double[]> inputs) {
        GSOMConstants.START_LEARNING_RATE = GSOMConstants.START_LEARNING_RATE / 4;
        GSOMConstants.MAX_NEIGHBORHOOD_RADIUS = GSOMConstants.MAX_NEIGHBORHOOD_RADIUS / 2;
        /*if(GSOMConstants.START_LEARNING_RATE>0){
            GSOMConstants.START_LEARNING_RATE = (Math.log10(GSOMConstants.START_LEARNING_RATE)/5)+0.25;
        }
        
        if(GSOMConstants.MAX_NEIGHBORHOOD_RADIUS>0){
            GSOMConstants.MAX_NEIGHBORHOOD_RADIUS = (Math.log(GSOMConstants.MAX_NEIGHBORHOOD_RADIUS)*2)+1;
        }*/
        
        for (int iter = 0; iter < GSOMConstants.MAX_ITERATIONS; iter++) {
            double learningRate = Utils.getLearningRate(iter, map.size());
            double radius = Utils.getRadius(iter, Utils.getTimeConst());
            for (double[] singleInput : inputs) {
                if (singleInput.length == GSOMConstants.DIMENSIONS) {
                    smoothSingleIterSingleInput(map, singleInput, learningRate, radius);
                } else {
                    rListener.stepCompleted(GSOMMessages.SMOOTHING_ERROR);
                }
            }
        }
        
    }

    private void smoothSingleIterSingleInput(Map<String, GNode> map, double[] input, double learningRate, double radius) {
        GNode winner = Utils.selectWinner(map, input);
        
        String leftNode = Utils.generateIndexString(winner.getX() - 1, winner.getY());
        String rightNode = Utils.generateIndexString(winner.getX() + 1, winner.getY());
        String topNode = Utils.generateIndexString(winner.getX(), winner.getY() + 1);
        String bottomNode = Utils.generateIndexString(winner.getX(), winner.getY() - 1);

        if (map.containsKey(leftNode)) {
            double[] oldW = map.get(leftNode).getWeights().clone();
            Utils.adjustNeighbourWeight(map.get(leftNode), winner, input, radius, learningRate);
            double sum = 0.0;
            for(int i=0;i<GSOMConstants.DIMENSIONS;i++){
                sum += (oldW[i]-map.get(leftNode).getWeights()[i])*(oldW[i]-map.get(leftNode).getWeights()[i]);
            }
            //System.out.println("After adapting: "+Math.sqrt(sum));
        } else if (map.containsKey(rightNode)) {
            double[] oldW = map.get(rightNode).getWeights().clone();
            Utils.adjustNeighbourWeight(map.get(rightNode), winner, input, radius, learningRate);
            double sum = 0.0;
            for(int i=0;i<GSOMConstants.DIMENSIONS;i++){
                sum += (oldW[i]-map.get(rightNode).getWeights()[i])*(oldW[i]-map.get(rightNode).getWeights()[i]);
            }
            //System.out.println("After adapting: "+Math.sqrt(sum));
        } else if (map.containsKey(topNode)) {
            double[] oldW = map.get(topNode).getWeights().clone();
            Utils.adjustNeighbourWeight(map.get(topNode), winner, input, radius, learningRate);
            double sum = 0.0;
            for(int i=0;i<GSOMConstants.DIMENSIONS;i++){
                sum += (oldW[i]-map.get(topNode).getWeights()[i])*(oldW[i]-map.get(topNode).getWeights()[i]);
            }
            //System.out.println("After adapting: "+Math.sqrt(sum));
        } else if (map.containsKey(bottomNode)) {
            double[] oldW = map.get(bottomNode).getWeights().clone();
            Utils.adjustNeighbourWeight(map.get(bottomNode), winner, input, radius, learningRate);
            double sum = 0.0;
            for(int i=0;i<GSOMConstants.DIMENSIONS;i++){
                sum += (oldW[i]-map.get(bottomNode).getWeights()[i])*(oldW[i]-map.get(bottomNode).getWeights()[i]);
            }
            //System.out.println("After adapting: "+Math.sqrt(sum));
        }
    }
}
