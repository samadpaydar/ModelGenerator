/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.util.Locale;

import jxl.Workbook;
import jxl.Cell;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 *
 * @author Home
 */
public class ExcelGenerator {

    private WritableWorkbook workbook;
    private WritableSheet excelSheet;
    private int row;
    private final String[] COLUMN_NAMES;

    public ExcelGenerator(File excel, String[] columnNames) {
        this.COLUMN_NAMES = columnNames;
        try {

            WorkbookSettings wbSettings = new WorkbookSettings();

            wbSettings.setLocale(new Locale("en", "EN"));
            wbSettings.setEncoding("UTF-8");

            workbook = Workbook.createWorkbook(excel, wbSettings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void append(String attributeName, String attributeValue) {
        try {
            int attributeIndex = getAttributeIndex(attributeName);
            if (attributeIndex == -1) {
                //System.out.println("AttributeName not valid '" + attributeName + "'");
            } else {
                write(attributeIndex, attributeValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(int columnIndex, String content) {
        try {
            if (content == null || content.trim().length() == 0) {
                return;
            }
            if(excelSheet == null) {
                goToNextSheet("Sheet 1");
            }
            Cell cell = excelSheet.getCell(columnIndex, row);
            if (cell != null) {
                String currentValue = cell.getContents().trim();
                if (currentValue != null && currentValue.trim().length() > 0) {
                    if (currentValue.toLowerCase().indexOf(content.toLowerCase()) == -1) {
                        content = currentValue + "; " + content;
                    }
                }
            }
            Label label;
            label = new Label(columnIndex, row, content);
            excelSheet.addCell(label);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToNextRow() {
        row++;
    }

    public void goToNextSheet(String sheetTitle) {
        int sheetCount = workbook.getSheets().length;
        workbook.createSheet(sheetTitle, sheetCount);
        excelSheet = workbook.getSheet(sheetCount);
        createHeader();
    }

    private void createHeader() {
        row = 0;
        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            write(i, COLUMN_NAMES[i]);
        }
        row = 1;
    }

    public void close() {
        try {
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getAttributeIndex(String attributeName) {
        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            if (COLUMN_NAMES[i].equalsIgnoreCase(attributeName)) {
                return i;
            }
        }
        return -1;
    }

}
