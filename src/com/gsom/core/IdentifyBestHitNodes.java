/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gsom.core;

import com.gsom.objects.GNode;
import com.gsom.util.Utils;
import java.util.*;

/**
 *
 * @author Thushan Ganegedara
 */
public class IdentifyBestHitNodes {

    public ArrayList<String> getHitNodeIDs(Map<String, GNode> map, int hitThresh, double neighRad) {

        Map<String, Double> hitVals = new HashMap<String, Double>();
        ArrayList<String> selectedNodeIDs = new ArrayList<String>();
        
        double threshold = hitThresh * neighRad;

        ArrayList<GNode> nodes = new ArrayList<GNode>(map.values());
        Collections.sort(nodes, new Comparator<GNode>() {

            @Override
            public int compare(GNode o1, GNode o2) {
                if (o1.getHitValue() > o2.getHitValue()) {
                    return -1;
                } else if (o1.getHitValue() < o2.getHitValue()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        for (int i = 0; i < nodes.size(); i++) {
            GNode n = nodes.get(i);
            String fulGNodeID = Utils.generateIndexString(n.getX(), n.getY());

            if (i == 0) {
                hitVals.put(fulGNodeID, Double.MAX_VALUE);
            } else {
                double minDist = Double.MAX_VALUE;
                for (int j = 0; j < i; j++) {
                    double distance = (nodes.get(i).getX() - nodes.get(j).getX()) * (nodes.get(i).getX() - nodes.get(j).getX())
                            + (nodes.get(i).getY() - nodes.get(j).getY()) * (nodes.get(i).getY() - nodes.get(j).getY());
                    distance = Math.sqrt(distance);
                    if (distance < minDist) {
                        minDist = distance;
                    }
                }

                double hitValue = nodes.get(i).getHitValue() * minDist;
                hitVals.put(fulGNodeID, hitValue);
            }
        }

        ValueComparator bvc = new ValueComparator(hitVals);
        TreeMap<String, Double> sortedHitVals = new TreeMap<String, Double>(bvc);
        sortedHitVals.putAll(hitVals);
        
        for (Map.Entry<String, Double> e : sortedHitVals.entrySet()) {
            if (e.getValue() > threshold) {
                selectedNodeIDs.add(e.getKey());
            } else {
                break;
            }
        }

        return selectedNodeIDs;
    }

    class ValueComparator implements Comparator<String> {

        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.    
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
}
