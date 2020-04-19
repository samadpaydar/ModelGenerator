/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.lod;

import com.sindice.Sindice;
import java.net.*;
import java.util.*;
import com.sindice.query.CacheQuery;
import com.sindice.query.FieldRegexFilter;
import com.sindice.result.CacheResult;
import com.sindice.result.SearchResults;
import com.sindice.result.SearchResult;
import ir.ac.um.wtlab.modelgenerator.*;

/**
 *
 * @author Home
 */
public class LODInterface {

    private URLReader urlReader;
    public final static String QUERY_PREFIX = "http://lod.openlinksw.com/sparql?default-graph-uri=&query=";
    private Sindice sindice;

    public LODInterface() {
        urlReader = new URLReader();
        sindice = new Sindice();
    }

    public ArrayList<String> getClassURIs(String className) {
        ArrayList<String> uris = new ArrayList<String>();
        try {
            /*            String query = "SELECT DISTINCT ?class WHERE {"
            + " {"
            + " ?class rdf:type owl:Class ."
            + " FILTER(regex(str(?class), \"#" + className + "$\", \"i\")) "
            + " } "
            + "UNION "
            + "{"
            + "?class rdf:type owl:Class ."
            + "?class rdfs:label ?label ."
            + " FILTER(regex(str(?label), \"" + className + "$\", \"i\")) "
            + "}"
            + "}"
            + "limit 200";*/
            String query = "SELECT DISTINCT ?class WHERE {"
                    + " {"
                    + " ?class rdf:type rdfs:Class ."
                    + " FILTER(regex(str(?class), \"#" + className + "$\", \"i\")) "
                    + " } "
                    + "UNION "
                    + "{"
                    + "?class rdf:type rdfs:Class ."
                    + "?class rdfs:label ?label ."
                    + " FILTER(regex(str(?label), \"^" + className + "$\", \"i\")) "
                    + "}"
                    + "UNION"
                    + " {"
                    + " ?class rdf:type owl:Class ."
                    + " FILTER(regex(str(?class), \"#" + className + "$\", \"i\")) "
                    + " } "
                    + "UNION "
                    + "{"
                    + "?class rdf:type owl:Class ."
                    + "?class rdfs:label ?label ."
                    + " FILTER(regex(str(?label), \"^" + className + "$\", \"i\")) "
                    + "}"
                    + "}"
                    + "limit 200";
            //System.out.println(query);
            query = URLEncoder.encode(query, "UTF-8");
            URL url = new URL(QUERY_PREFIX + query);
            String response = urlReader.fetchURL(url);
            String part1 = "<td><a href=\"";
            String part2 = "\">";
            int index1 = 0;
            while (true) {
                index1 = response.indexOf(part1, index1);
                if (index1 == -1) {
                    break;
                }
                int index2 = response.indexOf(part2, index1);
                String uri = response.substring(index1 + part1.length(), index2);
                uris.add(uri);
                index1 = index2;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return uris;
    }

    public String getLabelByURI(String uri) {
        try {
            String query = "SELECT DISTINCT ?label WHERE { "
                    + "<" + uri + "> rdfs:label ?label "
                    + "}";

            //System.out.println(query);
            query = URLEncoder.encode(query, "UTF-8");
            URL url = new URL(QUERY_PREFIX + query);
            String response = urlReader.fetchURL(url);
            String part1 = "<td>";
            String part2 = "</td>";
            int index1 = 0;
            while (true) {
                index1 = response.indexOf(part1, index1);
                if (index1 == -1) {
                    break;
                }
                int index2 = response.indexOf(part2, index1);
                return response.substring(index1 + part1.length(), index2);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UMLAttribute> getClassAttributesURIs(String classURI) {
        ArrayList<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
        try {
            String query = "SELECT DISTINCT ?attribute ?name ?range WHERE { "
                    + "{"
                    + "?attribute rdfs:domain <" + classURI + "> ."
                    + "OPTIONAL {?attribute rdfs:label ?name} ."
                    + "OPTIONAL {?attribute rdfs:range ?range} ."
                    + "} UNION {"
                    + "<" + classURI + "> rdfs:subClassOf ?super . "
                    + "?attribute rdfs:domain ?super ."
                    + "OPTIONAL {?attribute rdfs:label ?name} ."
                    + "OPTIONAL {?attribute rdfs:range ?range} ."
                    + "} "
                    + "}";

            query = URLEncoder.encode(query, "UTF-8");
            URL url = new URL(QUERY_PREFIX + query);
            String response = urlReader.fetchURL(url);
            String part1 = "<td>";
            String part2 = "</td>";
            int index1 = 0;
            while (true) {
                index1 = response.indexOf(part1, index1);
                if (index1 == -1) {
                    break;
                }
                int index2 = response.indexOf(part2, index1);
                String attributeURI = response.substring(index1 + part1.length(), index2);
                index1 = index2;
                index1 = response.indexOf(part1, index1);
                if (index1 == -1) {
                    break;
                }
                index2 = response.indexOf(part2, index1);
                String attributeName = response.substring(index1 + part1.length(), index2);
                index1 = index2;
                index1 = response.indexOf(part1, index1);
                if (index1 == -1) {
                    break;
                }
                index2 = response.indexOf(part2, index1);
                String attributeType = response.substring(index1 + part1.length(), index2);

                if (attributeType.startsWith("<a")) {
                    int tempIndex1 = attributeType.indexOf("\">");
                    if (tempIndex1 > 0) {
                        int tempIndex2 = attributeType.indexOf("</a>", tempIndex1);
                        attributeType = attributeType.substring(tempIndex1 + "\">".length(), tempIndex2);
                    }
                }

                index1 = index2;
                UMLAttribute attribute = new UMLAttribute(attributeName, attributeURI);
                attribute.setType(attributeType);
                attributes.add(attribute);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return attributes;
    }

    public ArrayList<String> getClassURIFromSindice(String className) {
        ArrayList<String> uris = new ArrayList<String>();
        try {
            SearchResults searchResults = sindice.advancedSearch(
                    "rdfs:label",
                    className);

            int count = searchResults.getTotalResults();
            for (int i = 0; i < count; i++) {
                SearchResult result = searchResults.get(i);
                System.out.println(result.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return uris;
    }
}
