/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.util;

import java.io.*;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Home
 */
public class Logger {
    private final static String LOG_FOLDER_NAME = "logs";
    private static PrintWriter file;
    
    private Logger() {
    }
    
    public static void log(String str) {
        if(file == null) {
            createLogFile();
        }
        file.println(str);
    }
    
    private static void createLogFile() {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            String fileName = calendar.get(Calendar.YEAR) + "_" + 
                    calendar.get(Calendar.MONTH + 1) + "_" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "_" +
                    calendar.get(Calendar.HOUR_OF_DAY) + "_" +
                    calendar.get(Calendar.MINUTE) + "_" +
                    calendar.get(Calendar.SECOND);
            File logDirectory = new File(LOG_FOLDER_NAME);
            if(!logDirectory.exists()) {
                logDirectory.mkdir();
            }
            file = new PrintWriter(new FileWriter(LOG_FOLDER_NAME + File.separator + fileName), true);
        } catch (Exception e) {
            e.printStackTrace();
        }     
    }
    
    public static void main(String[] args) {
        Logger.log("hello");
        Logger.log("good morning");
    }
}
