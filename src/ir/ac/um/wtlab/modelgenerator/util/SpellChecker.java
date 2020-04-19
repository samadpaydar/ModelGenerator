/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.util;

import com.jspell.domain.*;

/**
 *
 * @author Home
 */
public class SpellChecker {

    private JSpellDictionaryLocal jdLocal;
    private JSpellParser parser;
    private JSpellDictionaryManager manager;
    private final static String LEXICON_DIRECTORY = "./lexicons/";

    public SpellChecker() {
        manager = JSpellDictionaryManager.getJSpellDictionaryManager();
        manager.setDictionaryDirectory(LEXICON_DIRECTORY);
        jdLocal = manager.getJSpellDictionaryLocal("enUS"); // specify the language here
        jdLocal.setForceUpperCase(false); // set options on the dictionary here
        jdLocal.setIgnoreUpper(true);
        jdLocal.setIgnoreIrregularCaps(false);
        jdLocal.setIgnoreFirstCaps(true);
        jdLocal.setIgnoreDoubleWords(false);
        jdLocal.setLearnWords(false);
    }

    public JSpellErrorInfo check(String textToCheck) {
        parser = new JSpellParser(jdLocal, textToCheck); // this is where you specify your text

        // iterate through errors by calling parser.getError() until null. If parser returns non-null value
        // then you will get a JSpellErrorInfo object containing the position, original word and suggestions
        try {
            JSpellErrorInfo error = parser.getError();
            return error;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void print(JSpellErrorInfo error) {
        while (error != null) {
            System.out.println("Error at position: " + error.getPosition() + " in word: " + error.getWord());
            System.out.print("      Suggestions: ");
            for (int i = 0; i < error.getSuggestions().length && error.getSuggestions()[i] != null; i++) {
                System.out.print(error.getSuggestions()[i] + " ");
            }
            System.out.println();
            error = parser.getError();
        }
    }
}
