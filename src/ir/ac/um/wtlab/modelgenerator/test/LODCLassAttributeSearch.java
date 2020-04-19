/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import ir.ac.um.wtlab.modelgenerator.Constants;
import ir.ac.um.wtlab.modelgenerator.UMLClass;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.Repository;
import java.awt.Color;
import java.awt.Font;
import ir.ac.um.wtlab.modelgenerator.repositorymanager.*;
import org.openrdf.rio.RDFFormat;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.swing.text.TableView.TableRow;
import ir.ac.um.wtlab.modelgenerator.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.net.*;
import javax.swing.JOptionPane;
import ir.ac.um.wtlab.modelgenerator.similarity.*;
import javax.swing.text.*;
import ir.ac.um.wtlab.modelgenerator.test.BehaviorDetectionTest;
import ir.ac.um.wtlab.modelgenerator.test.OldConceptDetectionTest;
import ir.ac.um.wtlab.modelgenerator.geneticalgorithm.*;
import ir.ac.um.wtlab.modelgenerator.uwe2rdf.UWE2RDFConverter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import ir.ac.um.wtlab.modelgenerator.lod.LODInterface;

/**
 *
 * @author Home
 */
public class LODCLassAttributeSearch {

    public static void main(String[] args) {
        String repositoryName1 = Constants.MODEL_REPOSITORY_NAME;
        String repositoryName2 = Constants.ONTOLOGY_REPOSITORY_NAME;

        try {
            System.setOut(new PrintStream("LODSearchResults.log"));
            Repository repository1 = RepositoryManager.connectToRepository(repositoryName1);
            Repository repository2 = RepositoryManager.connectToRepository(repositoryName2);
            ModelGenerator modelGenerator = new ModelGenerator(repository1.getConnection(), repository2.getConnection(), null);
            String[] classNames = {
                "Acknowledgement",
                "Address",
                "AddressBook",
                "Album",
                "Article",
                "Artist",
                "Cast",
                "Contact",
                "Date",
                "DomainObject",
                "EMail",
                "Example",
                "ExternalReview",
                "Information",
                "Institution",
                "Journal",
                "MemorableQuote",
                "Movie",
                "News",
                "Organization",
                "Patient",
                "Performance",
                "Person",
                "Phone",
                "Picture",
                "Presentation",
                "Project",
                "Publication",
                "Publications",
                "Publisher",
                "RegisteredUser",
                "Room",
                "School",
                "Section",
                "Song",
                "Soundtrack",
                "TaggedEntry",
                "TeachingMaterial",
                "TextFile",
                "Theater",
                "Ticket",
                "Time",
                "Tool",
                "Trailer",
                "Tutorial",
                "URL",
                "UWE",
                "UserComment",
                "UserData",
                "UserRating",
                "WebEngineeringGroup"
            };
            for (String className : classNames) {
                modelGenerator.getClassAttributesFromLOD(className);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
