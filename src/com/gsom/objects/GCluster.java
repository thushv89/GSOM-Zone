/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gsom.objects;

import com.gsom.util.GSOMConstants;
import com.gsom.util.Utils;
import java.util.ArrayList;

/**
 *
 * @author Thush
 */
public class GCluster {

    private double[] centroidWeights;
    private ArrayList<GNode> cNodes;
    private int x;
    private int y;

    public GCluster(double[] centroidWeights) {
        cNodes = new ArrayList<GNode>();
        this.centroidWeights = centroidWeights;
    }

    public void addNeuron(GNode node) {
        cNodes.add(node);
    }

    //similar to avg distance between this cluster centroid and all the nodes in the cluster
    public double getSI() {
        double SI2 = 0;
        for (GNode node : cNodes) {
            SI2 += Math.pow(Utils.calcEucDist(node.getWeights(), centroidWeights, GSOMConstants.DIMENSIONS), 2);
        }

        return Math.sqrt(SI2 / cNodes.size());
    }

    /**
     * @return the centroidWeights
     */
    public double[] getCentroidWeights() {
        return centroidWeights;
    }

    /**
     * @param centroidWeights the centroidWeights to set
     */
    public void setCentroidWeights(double[] centroidWeights) {
        this.centroidWeights = centroidWeights;
    }

    /**
     * @return the cNodes
     */
    public ArrayList<GNode> getcNodes() {
        return cNodes;
    }

    /**
     * @param cNodes the cNodes to set
     */
    public void setcNodes(ArrayList<GNode> cNodes) {
        this.cNodes = cNodes;
    }

    public void CalculateAndAssignNewCentroid() {
        if (cNodes.size() > 0) {
            double[] tempCentroid = new double[GSOMConstants.DIMENSIONS];
            double tempX = 0;
            double tempY = 0;
            for (int i = 0; i < GSOMConstants.DIMENSIONS; i++) {
                double temp = 0;
                for (int j = 0; j < cNodes.size(); j++) {
                    //Calculate the sum of the wieghts for each dimension to determine the weights of the centroid
                    temp += cNodes.get(j).getWeights()[i];
                }
                tempCentroid[i] = (temp / cNodes.size());
            }
            centroidWeights = tempCentroid;


            for (int j = 0; j < cNodes.size(); j++) {
                //Calcuate the sum of the X and Y position for each dimension to determine the position of the centroid
                tempX += cNodes.get(j).getX();
                tempY += cNodes.get(j).getY();
            }

            x = (int) tempX / cNodes.size();
            y = (int) tempY / cNodes.size();
        }
    }

    public void SaveNeuronList() {
        cNodes = new ArrayList<GNode>();
    }


    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

}
