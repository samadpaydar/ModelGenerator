/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.uwe2rdf;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.awt.*;

/**
 *
 * @author Home
 */
public class UWE2RDFConverter {

    public UWE2RDFConverter() {
    }

    public File run() {
        //String initialPath = "G:\\Projects & Courses\\My Thesis\\Use Case Dataset";
        String initialPath = "G:\\Projects & Courses\\My Thesis\\UWE2RDF\\UWE Samples";
        JFileChooser fileChooser = new JFileChooser(initialPath);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File directory = fileChooser.getSelectedFile();
            processDirectory(directory);
            FileMerger merger = new FileMerger(directory.getPath());
            return merger.mergeFiles();
        }
        return null;
    }

    private void processFile(File file) {
        if (file.getName().endsWith(".mdxml")) {
            String fileName = file.getName();
            System.out.println("Converting file '" + file.getName() + "'...");
            long start = System.currentTimeMillis();
            fileName = prepareFileName(file);
            Converter converter = new Converter(fileName);
            converter.convert(file);
            long end = System.currentTimeMillis();
            System.out.println("Conversion finished in " + (end - start) + " ms");
        }
    }

    private void processDirectory(File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                processFile(file);
            } else {
                processDirectory(file);
            }
        }
    }

    private String prepareFileName(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, index);
        fileName = fileName.replace('.', '_');
        return fileName;
    }
}
