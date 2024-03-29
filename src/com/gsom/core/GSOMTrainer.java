package com.gsom.core;

import com.gsom.enums.InitType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gsom.objects.GNode;
import com.gsom.util.ArrayHelper;
import com.gsom.util.GSOMConstants;
import com.gsom.util.Utils;
import java.util.LinkedHashMap;

public class GSOMTrainer {

    private Map<String, GNode> nodeMap;
    private NodeGrowthHandler growthHandler;
    private InitType initType;

    public GSOMTrainer(InitType initType) {
        this.initType = initType;
        nodeMap = new LinkedHashMap<String, GNode>();
    }

    public Map<String, GNode> trainNetwork(ArrayList<String> iStrings, ArrayList<double[]> iWeights) {
        initFourNodes(initType);	//init the map with four nodes
        for (int i = 0; i < GSOMConstants.MAX_ITERATIONS; i++) {
            int k = 0;
            double learningRate = Utils.getLearningRate(i, nodeMap.size());
            double radius = Utils.getRadius(i, Utils.getTimeConst());
            for (double[] input : iWeights) {
                trainForSingleIterAndSingleInput(i, input, iStrings.get(k), learningRate, radius);
                k++;
            }
        }
        return nodeMap;
    }

    private void trainForSingleIterAndSingleInput(int iter, double[] input, String str, double learningRate, double radius) {

        GNode winner = Utils.selectWinner(nodeMap, input);

        for (String key : nodeMap.keySet()) {
            Utils.adjustNeighbourWeight(nodeMap.get(key), winner, input, radius, learningRate);
        }

        winner.calcAndUpdateErr(input);

        if (winner.getErrorValue() > GSOMConstants.getGT()) {
            //System.out.println("Winner "+winner.getX()+","+winner.getY()+" GT exceeded");
            adjustWinnerError(winner);
        }
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

        //on x-axis
        String nodeLeftStr = Utils.generateIndexString(winner.getX() - 1, winner.getY());
        String nodeRightStr = Utils.generateIndexString(winner.getX() + 1, winner.getY());

        //on y-axis
        String nodeTopStr = Utils.generateIndexString(winner.getX(), winner.getY() + 1);
        String nodeBottomStr = Utils.generateIndexString(winner.getX(), winner.getY());

        if (nodeMap.containsKey(nodeLeftStr)
                && nodeMap.containsKey(nodeRightStr)
                && nodeMap.containsKey(nodeTopStr)
                && nodeMap.containsKey(nodeBottomStr)) {
            distrErrToNeighbors(winner, nodeLeftStr, nodeRightStr, nodeTopStr, nodeBottomStr);
        } else {
            growNodes(winner); //NodeGrowthHandler takes over
        }
    }

    //distributing error to the neighbors of thw winning node
    private void distrErrToNeighbors(GNode winner, String leftK, String rightK, String topK, String bottomK) {
        winner.setErrorValue(GSOMConstants.getGT() / 2);

        nodeMap.get(leftK).setErrorValue(calcErrForNeighbour(nodeMap.get(leftK)));
        nodeMap.get(rightK).setErrorValue(calcErrForNeighbour(nodeMap.get(rightK)));
        nodeMap.get(topK).setErrorValue(calcErrForNeighbour(nodeMap.get(topK)));
        nodeMap.get(bottomK).setErrorValue(calcErrForNeighbour(nodeMap.get(bottomK)));
    }

    //error calculating equation for neighbours of a winner
    private double calcErrForNeighbour(GNode node) {
        return node.getErrorValue() + (GSOMConstants.FD * node.getErrorValue());
    }

    public void growNodes(GNode winner) {

        int X = winner.getX();
        int Y = winner.getY();

        for (int i = X - 1; i <= X + 1; i = i + 2) {
            String nodeStr = Utils.generateIndexString(i, Y);
            if (!nodeMap.containsKey(nodeStr)) {
                
                //grow new node
                GNode newNode = new GNode(i, Y, getNewNodeWeights(winner, i, Y));
                //System.out.println("Node "+X+","+Y+" grown from Node "+i+","+Y);
                nodeMap.put(Utils.generateIndexString(i, Y), newNode);
            }
        }
        for (int i = Y - 1; i <= Y + 1; i = i + 2) {
            String nodeStr = Utils.generateIndexString(X, i);
                
            if (!nodeMap.containsKey(nodeStr)) {
                //grow new node
                GNode newNode = new GNode(X, i, getNewNodeWeights(winner, X, i));
                //System.out.println("Node "+X+","+Y+" grown from Node "+X+","+i);
                nodeMap.put(Utils.generateIndexString(X, i), newNode);
            }
        }
        return;
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
