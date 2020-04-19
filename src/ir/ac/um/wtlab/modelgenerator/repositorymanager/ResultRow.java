/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.repositorymanager;

import java.util.*;

/**
 *
 * @author Home
 */
public class ResultRow {

    private ArrayList<NameValuePair> nameValuePairs;

    public ResultRow() {
        nameValuePairs = new ArrayList<NameValuePair>();
    }

    public void add(NameValuePair pair) {
        nameValuePairs.add(pair);
    }

    public String getValueByName(String name) {
        for (NameValuePair pair : nameValuePairs) {
            if (pair.getName().equals(name)) {
                return pair.getValue();
            }
        }
        return null;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(NameValuePair pair: nameValuePairs) {
            builder.append(pair + "\t");
        }
        return builder.toString();
    }
}
