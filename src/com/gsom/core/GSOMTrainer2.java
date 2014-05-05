package com.gsom.core;

import com.gsom.enums.InitType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gsom.objects.GNode;
import com.gsom.util.ArrayHelper;
import com.gsom.util.GSOMConstants;
import com.gsom.util.Utils;

public class GSOMTrainer2 {

    private Map<String, GNode> nodeMap;
    private InitType initType;

    public GSOMTrainer2(InitType initType) {

    }

    public Map<String, GNode> trainNetwork(ArrayList<String> iStrings, ArrayList<double[]> iWeights) {
        return null;
    }

    private void trainForSingleIterAndSingleInput(int iter, double[] input, String str, double learningRate, double radius) {

    }

    //Initialization of the map. 
    //this will create 4 nodes with random weights
    private void initFourNodes(InitType type) {
        if (type == InitType.RANDOM) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    GNode initNode = new GNode(i, j, Utils.generateRandomArray(GSOMConstants.DIMENSIONS));
                    nodeMap.put(Utils.generateIndexString(i, j), initNode);
                }
            }
        } else if (type == InitType.LINEAR) {
            double initVal = 0.1;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    GNode initNode = new GNode(i, j, Utils.generateLinearArray(GSOMConstants.DIMENSIONS, initVal));
                    nodeMap.put(Utils.generateIndexString(i, j), initNode);
                    initVal += 0.1;
                }
            }
        }
    }

    //when a neuron wins its error value needs to be adjusted
    private void adjustWinnerError(GNode winner) {

    }

    //distributing error to the neighbors of thw winning node
    private void distrErrToNeighbors(GNode winner, String leftK, String rightK, String topK, String bottomK) {

    }

    //error calculating equation for neighbours of a winner
    private double calcErrForNeighbour(GNode node) {
        return 0.0;
    }

    public void growNodes(GNode winner) {

    }

    //calc and get weights for the new node
    private double[] getNewNodeWeights(GNode winner, int X, int Y) {

        double[] newWeights = new double[GSOMConstants.DIMENSIONS];

        if (winner.getY() == Y) {
			//two consecutive nodes 

            //winnerX,otherX
            if (X == winner.getX() + 1) {
                String nextNodeStr = Utils.generateIndexString(X + 1, Y); //nX
                String othrSideNodeStr = Utils.generateIndexString(X - 2, Y); //oX
                String topNodeStr = Utils.generateIndexString(winner.getX(), Y + 1);  //tX
                String botNodeStr = Utils.generateIndexString(winner.getX(), Y - 1);  //bX

				//new node has one direct neighbor, 
                //but neighbor has a neighbor in the opposing directly
                //wX,X,nX
                if (nodeMap.containsKey(nextNodeStr)) {
                    newWeights = newWeightsForNewNodeInMiddle(winner, nextNodeStr);
                } //oX,wX,X
                else if (nodeMap.containsKey(othrSideNodeStr)) {
                    //2 consecutive nodes on right
                    newWeights = newWeightsForNewNodeOnOneSide(winner, othrSideNodeStr);
                } //   tX
                //wX, X
                else if (nodeMap.containsKey(topNodeStr)) {
                    //right and top nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, topNodeStr);
                } //wX, X
                //   bX
                else if (nodeMap.containsKey(botNodeStr)) {
                    //right and bottom nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, botNodeStr);
                } else {
                    newWeights = newWeightsForNewNodeOneOlderNeighbor(winner);
                }
            } //otherX,winnerX
            else if (X == winner.getX() - 1) {
                String nextNodeStr = Utils.generateIndexString(X - 1, Y);
                String othrSideNodeStr = Utils.generateIndexString(X + 2, Y);
                String topNodeStr = Utils.generateIndexString(winner.getX(), Y + 1);
                String botNodeStr = Utils.generateIndexString(winner.getX(), Y - 1);

				//new node has one direct neighbor, 
                //but neighbor has a neighbor in the opposing directly
                //nX,X,wX
                if (nodeMap.containsKey(nextNodeStr)) {
                    newWeights = newWeightsForNewNodeInMiddle(winner, nextNodeStr);
                } //X,wX,oX
                else if (nodeMap.containsKey(othrSideNodeStr)) {
                    //2 consecutive nodes on left
                    newWeights = newWeightsForNewNodeOnOneSide(winner, othrSideNodeStr);
                } else if (nodeMap.containsKey(topNodeStr)) {
                    //left and top nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, topNodeStr);
                } else if (nodeMap.containsKey(botNodeStr)) {
                    //left and bottom nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, botNodeStr);
                } else {
                    newWeights = newWeightsForNewNodeOneOlderNeighbor(winner);
                }
            }

            //new node is in the middle of two older nodes
        } else if (winner.getX() == X) {

			//otherY
            //winnerY
            if (Y == winner.getY() + 1) {
                String nextNodeStr = Utils.generateIndexString(X, Y + 1);
                String othrSideNodeStr = Utils.generateIndexString(X, Y - 2);
                String leftNodeStr = Utils.generateIndexString(X - 1, winner.getY());
                String rightNodeStr = Utils.generateIndexString(X + 1, winner.getY());

				//new node has one direct neighbor, 
                //but neighbor has a neighbor in the opposing directly
                //nY
                // Y
                //wY
                if (nodeMap.containsKey(nextNodeStr)) {
                    newWeights = newWeightsForNewNodeInMiddle(winner, nextNodeStr);
                } else if (nodeMap.containsKey(othrSideNodeStr)) {
                    //2 consecutive nodes upwards
                    newWeights = newWeightsForNewNodeOnOneSide(winner, othrSideNodeStr);
                } else if (nodeMap.containsKey(leftNodeStr)) {
                    //left and top nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, leftNodeStr);
                } else if (nodeMap.containsKey(rightNodeStr)) {
                    //right and top nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, rightNodeStr);
                } else {
                    newWeights = newWeightsForNewNodeOneOlderNeighbor(winner);
                }
            } //winnerY
            //otherY
            else if (Y == winner.getY() - 1) {
                String nextNodeStr = Utils.generateIndexString(X, Y - 1);
                String othrSideNodeStr = Utils.generateIndexString(X, Y + 2);
                String leftNodeStr = Utils.generateIndexString(X - 1, winner.getY());
                String rightNodeStr = Utils.generateIndexString(X + 1, winner.getY());

				//new node has one direct neighbor, 
                //but neighbor has a neighbor in the opposing directly
                                //wY
                // Y
                //nY
                if (nodeMap.containsKey(nextNodeStr)) {
                    newWeights = newWeightsForNewNodeInMiddle(winner, nextNodeStr);
                } else if (nodeMap.containsKey(othrSideNodeStr)) {
                    //2 consecutive nodes on left
                    newWeights = newWeightsForNewNodeOnOneSide(winner, othrSideNodeStr);
                } else if (nodeMap.containsKey(leftNodeStr)) {
                    //left and top nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, leftNodeStr);
                } else if (nodeMap.containsKey(rightNodeStr)) {
                    //left and bottom nodes
                    newWeights = newWeightsForNewNodeOnOneSide(winner, rightNodeStr);
                } else {
                    newWeights = newWeightsForNewNodeOneOlderNeighbor(winner);
                }
            }
        }

        for (int i = 0; i < GSOMConstants.DIMENSIONS; i++) {
            if (newWeights[i] < 0) {
                newWeights[i] = 0;
            }
            if (newWeights[i] > 1) {
                newWeights[i] = 1;
            }
        }
        return newWeights;
    }

    //node1,new_node,node2
    private double[] newWeightsForNewNodeInMiddle(GNode winner, String otherNodeIdx) {
        double[] newWeights;
        GNode otherNode = nodeMap.get(otherNodeIdx);
        newWeights = ArrayHelper.add(winner.getWeights(), otherNode.getWeights(), GSOMConstants.DIMENSIONS);
        newWeights = ArrayHelper.multiplyArrayByConst(newWeights, 0.5);
        return newWeights;
    }

    //node1,node2,new_node or new_node,node1,node2
    private double[] newWeightsForNewNodeOnOneSide(GNode winner, String otherNodeIdx) {
        double[] newWeights;
        GNode otherNode = nodeMap.get(otherNodeIdx);
        newWeights = ArrayHelper.multiplyArrayByConst(winner.getWeights(), 2);
        //System.out.println(newWeights.length+" "+winner.getWeights().length+" "+otherNode.getWeights().length);
        newWeights = ArrayHelper.substract(newWeights, otherNode.getWeights(), GSOMConstants.DIMENSIONS);
        return newWeights;
    }

    //winner,new node
    private double[] newWeightsForNewNodeOneOlderNeighbor(GNode winner) {
        double[] newWeights = new double[GSOMConstants.DIMENSIONS];
        double min = ArrayHelper.getMin(winner.getWeights());
        double max = ArrayHelper.getMax(winner.getWeights());
        for (int i = 0; i < GSOMConstants.DIMENSIONS; i++) {
            newWeights[i] = (min + max) / 2;
        }
        return newWeights;
    }
}
