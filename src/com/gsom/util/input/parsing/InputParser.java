package com.gsom.util.input.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.gsom.listeners.InputParsedListener;
import com.gsom.util.ArrayHelper;
import com.gsom.util.GSOMConstants;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;

public abstract class InputParser {

    ArrayList<String> strForWeights;
    ArrayList<double[]> weights;

    public InputParser() {

        strForWeights = new ArrayList<String>();
        weights = new ArrayList<double[]>();
    }

    abstract public void parseInput(InputParsedListener iListener, String fileName);

    protected void normalizeData(ArrayList<double[]> inputs, int dimensions) {
        normalizeVertical(dimensions, inputs);
    }
    protected void normalizeDataWithStdDev(ArrayList<double[]> inputs, int dimensions) {
        normalizeVerticalWithStdDev(dimensions, inputs);
    }

    private void normalizeVerticalWithStdDev(int dimensions, ArrayList<double[]> inputs) {
        ArrayList<Double> stdDevArr = new ArrayList<Double>();
        ArrayList<Double> meanArr = new ArrayList<Double>();

        for (int i = 0; i < dimensions; i++) {
            double[] dimArr = new double[inputs.size()];

            double meanForDim = 0.0;
            for (int j = 0; j < inputs.size(); j++) {
                dimArr[j] = inputs.get(j)[i];
                meanForDim += dimArr[j];
            }

            meanForDim = meanForDim / inputs.size();
            meanArr.add(meanForDim);

            double stdDev = 0.0;
            for (int j = 0; j < inputs.size(); j++) {
                stdDev += (inputs.get(j)[i] - meanForDim) * (inputs.get(j)[i] - meanForDim);
            }
            stdDev = Math.sqrt(stdDev/inputs.size());
            stdDevArr.add(stdDev);

        }

        for (int i = 0; i < dimensions; i++) {

            double minStd = meanArr.get(i) - (2 * stdDevArr.get(i));
            double maxStd = meanArr.get(i) + (2 * stdDevArr.get(i));

            for (int j = 0; j < inputs.size(); j++) {
                double[] inArr = inputs.get(j);

                //do this if there's some value other than 0 is in column
                if (maxStd - minStd > 0) {
                    inArr[i] = (inArr[i] - minStd) / (maxStd - minStd);
                }
                if(inArr[i]<0){
                    inArr[i]=0;
                }else if(inArr[i]>1){
                    inArr[i]=1;
                }
                inputs.set(j, inArr);
            }
        }
    }

    private void normalizeVertical(int dimensions, ArrayList<double[]> inputs) {
        ArrayList<Double> maxDimArr = new ArrayList<Double>();
        ArrayList<Double> minDimArr = new ArrayList<Double>();

        for (int i = 0; i < dimensions; i++) {
            double[] dimArr = new double[inputs.size()];
            for (int j = 0; j < inputs.size(); j++) {
                dimArr[j] = inputs.get(j)[i];
            }
            maxDimArr.add(ArrayHelper.getMax(dimArr));
            minDimArr.add(ArrayHelper.getMin(dimArr));
        }

        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < inputs.size(); j++) {
                double[] inArr = inputs.get(j);

                //do this if there's some value other than 0 is in column
                if (maxDimArr.get(i) - minDimArr.get(i) > 0) {
                    inArr[i] = (inArr[i] - minDimArr.get(i)) / (maxDimArr.get(i) - minDimArr.get(i));
                    inputs.set(j, inArr);
                }
            }
        }
    }

    protected boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    protected Color getColor(String color) {
        Color clr = null;

        if (color.equalsIgnoreCase("red")) {
            clr = Color.red;
        } else if (color.equalsIgnoreCase("green")) {
            clr = Color.green;
        } else if (color.equalsIgnoreCase("blue")) {
            clr = Color.blue;
        } else if (color.equalsIgnoreCase("black")) {
            clr = Color.black;
        } else if (color.equalsIgnoreCase("white")) {
            clr = Color.white;
        } else if (color.equalsIgnoreCase("orange")) {
            clr = Color.orange;
        } else if (color.equalsIgnoreCase("gold")) {
            clr = new Color(255, 215, 0);
        } else if (color.equalsIgnoreCase("brown")) {
            clr = new Color(165, 42, 42);
        }

        return clr;
    }

    public ArrayList<String> getStrForWeights() {
        return strForWeights;
    }

    public ArrayList<double[]> getWeights() {
        return weights;
    }

    public void printInput() {
        for (int i = 0; i < strForWeights.size(); i++) {
            System.out.print(strForWeights.get(i) + " ");
            for (int j = 0; j < GSOMConstants.DIMENSIONS; j++) {
                System.out.print(weights.get(i)[j] + ",");

            }
            System.out.println("");
        }

    }
}
