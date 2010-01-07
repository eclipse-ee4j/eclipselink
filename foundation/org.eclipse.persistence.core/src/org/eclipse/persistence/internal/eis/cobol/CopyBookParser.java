/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.eis.cobol;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.internal.eis.cobol.helper.*;

/**
*   <p>
*   <b>Purpose</b>:  This class is a parser for Cobol Copy books. It will take a stream as
*   an argument in its constructor and parse the stream when <code>parse()</code> is called.
*   The <code>parse()</code> method returns a <code>Vector</code> of <code>RecordMetaData</code>
*   one for each "01" level record description in the stream.
*/
public class CopyBookParser {

    /** the maximum nested levels allowed, or in other words, the highest level number allowed */
    private static final int maximumNestingLevels = 50;

    /** the line the parser is currently on */
    private String currentLine;

    /** the current line number */
    private int currentLineNumber;

    /**
    * Default constructor
    */
    public CopyBookParser() {
        currentLine = new String();
        currentLineNumber = 0;
    }

    /**
    * This method is the primary public api for this class, it takes an <code>InputStream</code>
    * as an argument then parses this stream looking for "01" level record entries.  It returns
    * a <code>Vector</code> containing <code>RecordMetaData</code> for each "01" record defintion
    * encountered in the stream.
    */
    public Vector parse(InputStream stream) throws Exception {
        Vector records;
        currentLineNumber = 0;
        //read file and prepare for parsing
        try {
            //FileInputStream fileInput = new FileInputStream(myParseFile);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            String copyBookString = new String(bytes);

            //parse through string and determine hierarchy and field sizes
            records = buildStructure(copyBookString);

            //calculate the field offsets from the records
            for (int i = 0; i < records.size(); i++) {
                setOffsetsForComposite((CompositeObject)records.elementAt(i), 0);
            }
        } catch (IOException exception) {
            throw CopyBookParseException.ioException(exception);
        }
        return records;
    }

    /**
    * This is a private method that handles the actual parsing of the string tokens, it scans
    * each line looking for a "01" level record definition, when it is encountered, it builds the
    * hierarchical structure for the <code>RecordMetaData</code>
    */
    private Vector buildStructure(String fileString) throws Exception {
        Vector records = new Vector();
        StringTokenizer lineTokenizer = new StringTokenizer(fileString, System.getProperty("line.separator"), false);
        RecordMetaData record = new RecordMetaData();
        Vector recordLines = new Vector();
        Vector lineNums = new Vector();

        //first pass removes all non-record data and brings all lines together
        while (lineTokenizer.hasMoreTokens() && !currentLine.equalsIgnoreCase("procedure division.")) {
            currentLine = lineTokenizer.nextToken();
            currentLineNumber++;
            if (!currentLine.trim().startsWith("*") && (currentLine.trim().length() > 0)) {
                StringTokenizer lineTokens = new StringTokenizer(currentLine);
                String firstToken = lineTokens.nextToken();
                if (firstToken.endsWith(".")) {
                    firstToken = firstToken.substring(0, currentLine.lastIndexOf("."));
                }
                Integer levelNumber = Helper.integerFromString(firstToken);
                if ((levelNumber != null) && (levelNumber.intValue() < 50)) {
                    //assure we've gotten entire line
                    while (!currentLine.trim().endsWith(".")) {
                        currentLine += lineTokenizer.nextToken();
                        currentLineNumber++;
                    }
                    currentLine = currentLine.substring(0, currentLine.lastIndexOf("."));
                    recordLines.addElement(currentLine);
                    lineNums.addElement(new Integer(currentLineNumber));
                }
            }
        }

        //second pass will build the structure
        int nestingLevel = maximumNestingLevels;
        Stack parents = new Stack();
        Hashtable parentsToLevels = new Hashtable();
        Enumeration recordsEnum = recordLines.elements();
        Enumeration recordLineNums = lineNums.elements();
        while (recordsEnum.hasMoreElements()) {
            currentLine = (String)recordsEnum.nextElement();
            currentLineNumber = ((Integer)recordLineNums.nextElement()).intValue();
            StringTokenizer lineTokens = new StringTokenizer(currentLine);
            if (lineTokens.hasMoreTokens()) {
                String firstToken = lineTokens.nextToken();
                Integer levelNumber = Helper.integerFromString(firstToken);
                Object component;

                //process record
                if (levelNumber.intValue() == 1) {
                    nestingLevel = maximumNestingLevels;
                    parents = new Stack();
                    parentsToLevels = new Hashtable();
                    component = buildRecord(lineTokens);
                    record = (RecordMetaData)component;
                    records.addElement(component);
                }
                //process subordinate field
                else if (levelNumber.intValue() >= nestingLevel) {
                    component = buildField(lineTokens);
                    ((CompositeObject)parents.peek()).addField((FieldMetaData)component);
                }
                //field is no longer subordinate skip back to original level
                else {
                    while (((Integer)parentsToLevels.get(parents.peek())).intValue() >= levelNumber.intValue()) {
                        parents.pop();
                    }
                    component = buildField(lineTokens);
                    ((CompositeObject)parents.peek()).addField((FieldMetaData)component);
                }
                nestingLevel = levelNumber.intValue();
                if (component instanceof FieldMetaData) {
                    ((FieldMetaData)component).setRecord(record);
                }
                if (component instanceof CompositeObject) {
                    parents.push(component);
                    parentsToLevels.put(component, levelNumber);
                }
            }
        }
        return records;
    }

    /**
    * This method cascades down through the records built in the <code>buildStructure</code>
    * method setting the  offsets for the fields, this must be done now, because the sizes
    * for the fields must allready be determined.
    */
    private void setOffsetsForComposite(CompositeObject object, int offset) {
        int currentOffset = offset;
        int previousFieldSize = 0;
        Vector fields = object.getFields();
        Enumeration fieldEnum = fields.elements();
        FieldMetaData previousField = null;

        //loop through fields setting their offsets and redefines if it applies
        while (fieldEnum.hasMoreElements()) {
            FieldMetaData field = (FieldMetaData)fieldEnum.nextElement();

            //if its a redefine, must first see if it larger and reset offset accordingly
            if (field.isFieldRedefine()) {
                field.setFieldRedefined(previousField);
                if (field instanceof CompositeObject) {
                    setOffsetsForComposite((CompositeObject)field, previousField.getOffset());
                }
                field.setOffset(previousField.getOffset());
                if (previousFieldSize < field.getSize()) {
                    currentOffset += (field.getSize() - previousFieldSize);
                    previousFieldSize = field.getSize();
                }
            } else {
                if (field instanceof CompositeObject) {
                    setOffsetsForComposite((CompositeObject)field, currentOffset);
                }
                field.setOffset(currentOffset);
                currentOffset += field.getSize();
                previousField = field;
                previousFieldSize = field.getSize();
            }
        }
    }

    /**
    * This method processes "01" level lines building a <code>RecordMetaData</code> and
    * returning it.
    */
    private RecordMetaData buildRecord(StringTokenizer lineTokens) throws Exception {
        RecordMetaData record;
        if (lineTokens.hasMoreTokens()) {
            String recordName = lineTokens.nextToken();
            record = new RecordMetaData(recordName);
            return record;
        } else {
            throw invalidCopyBookException("The record has no name.");
        }
    }

    /**
    * This method handles all "02" through "49" level lines building  a field from the line.
    * it returns <code>FieldMetaData</code> for either composite or elementary fields
    * appropriately.
    */
    private FieldMetaData buildField(StringTokenizer lineTokens) throws Exception {
        FieldMetaData field;
        String fieldName;
        boolean redefine = false;
        int arraySize = -1;
        String[] tokens = new String[lineTokens.countTokens()];
        int index = 0;
        String dependentField = "";

        //build token array first so that backtracking can be done if necessary
        while (lineTokens.hasMoreTokens()) {
            tokens[index++] = lineTokens.nextToken();
        }
        index = 0;
        if (tokens.length > 0) {
            if (isKeyWord(tokens[index])) {
                fieldName = "filler";
            } else {
                fieldName = tokens[index];
            }
        }
        //composite without name
        else {
            field = new CompositeFieldMetaData();
            field.setName("filler");
            return field;
        }

        //find the pic statement   
        for (int i = 0;
                 (i < tokens.length) && !tokens[index].equalsIgnoreCase("pic") && !tokens[index].equalsIgnoreCase("picture");
                 i++) {
            if (tokens[index].equalsIgnoreCase("redefines")) {
                redefine = true;
            }
            if (tokens[index].equalsIgnoreCase("occurs")) {
                arraySize = handleOccursStatement(tokens, ++index);
                index--;
            }
            if (tokens[index].equalsIgnoreCase("depending")) {
                dependentField = handleDependeningStatement(tokens, ++index);
                index--;
            }
            index++;

        }

        //elementary field
        if ((index < tokens.length) && (tokens[index].equalsIgnoreCase("pic") || tokens[index].equalsIgnoreCase("picture"))) {
            field = buildElementaryField(fieldName, tokens, index);
        }
        //composite field    
        else {
            field = new CompositeFieldMetaData();
            field.setName(fieldName);
        }
        field.setIsFieldRedefine(redefine);
        field.setArraySize(arraySize);
        field.setDependentFieldName(dependentField);
        return field;
    }

    /**
    * This method handles the "depending" statement returning the name of the field that
    * this field depends on.
    */
    private String handleDependeningStatement(String[] tokens, int index) throws Exception {
        String fieldName = null;
        try {
            fieldName = tokens[index];
            if (index < tokens.length) {
                if (fieldName.equalsIgnoreCase("on")) {
                    fieldName = tokens[++index];
                }
            }

            //there was no name following the depending clause or on clause
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw invalidCopyBookException("There was no field name following the depending clause.", exception);
        }
        return fieldName;
    }

    /**
    * This method handles the "occurs" statment, returning the maximum number of times this field
    * should occur.
    */
    private int handleOccursStatement(String[] tokens, int index) throws Exception {
        try {
            Integer size = Helper.integerFromString(tokens[index]);
            if (index < tokens.length) {
                if (tokens[++index].equalsIgnoreCase("to")) {
                    Integer newSize = Helper.integerFromString(tokens[++index]);
                    if (size.intValue() > 0) {
                        newSize = new Integer(newSize.intValue() - size.intValue());
                    }
                    size = newSize;
                }
            }
            if (size.intValue() < 1) {
                throw invalidCopyBookException("Must occur at least once.");
            }
            return size.intValue();
            //there was no integer following the occurs statment or one after the to statement
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw invalidCopyBookException("Occurs clause must be folowed by and integer.", exception);
        }
    }

    /**
    * This method build elementary fields getting the pertinent size
    * information from the pic statement
    */
    private FieldMetaData buildElementaryField(String fieldName, String[] tokens, int index) throws Exception {
        FieldMetaData field = new ElementaryFieldMetaData();
        String picStatment;
        int size = 0;
        try {
            field.setName(fieldName);
            picStatment = tokens[++index];

            if (picStatment.equalsIgnoreCase("is")) {
                picStatment = tokens[++index];
            }

            //either the pic statement didn't follow the pic clause or didn't follow the is clause
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw invalidCopyBookException("Picture clause must be followed by a pic statement.", exception);
        }

        //set type and calculate size
        if (picStatment.toUpperCase().startsWith("A")) {
            field.setType(FieldMetaData.ALPHABETIC);
            size = calculateSizeOfAlphaNumeric(picStatment, field);
        } else if (picStatment.toUpperCase().startsWith("X")) {
            field.setType(FieldMetaData.ALPHA_NUMERIC);
            size = calculateSizeOfAlphaNumeric(picStatment, field);
        } else if (picStatment.toUpperCase().startsWith("9") || picStatment.toUpperCase().startsWith("V") || picStatment.toUpperCase().startsWith("Z") || picStatment.startsWith("+") || picStatment.startsWith("-") || picStatment.toUpperCase().startsWith("S")) {
            field.setType(FieldMetaData.NUMERIC);
            size = calculateSizeOfNumeric(picStatment, tokens, index, field);
        }

        field.setSize(size);
        return field;

    }

    /**
    * This method calculates the size of alphanumeric pic statments that begin with "A" or "X"
    */
    private int calculateSizeOfAlphaNumeric(String picStatement, FieldMetaData field) throws Exception {
        //parse through statement and determine size
        char[] picChars = picStatement.toCharArray();
        int length = picChars.length;
        int index = 0;
        int size = 0;

        while (index < length) {
            char currentChar = picChars[index];
            switch (currentChar) {
            case '(':
                String number = new String();
                index++;
                size--;
                currentChar = picChars[index];
                while ((index < length) && (currentChar != ')')) {
                    number += currentChar;
                    index++;
                    currentChar = picChars[index];
                }
                try {
                    Integer value = new Integer(number);
                    size += value.intValue();
                } catch (NumberFormatException exception) {
                    throw invalidCopyBookException("In pic statement a valid integer must be enclosed by the parenthesis.", exception);
                }
                if (currentChar == ')') {
                    index++;
                } else {
                    throw invalidCopyBookException("An open parenthesis must be followed by a close parenthesis.");
                }
                break;
            case 'p':
            case 'P':
            case 'Z':
            case 'z':
            case '+':
            case '-':
            case 'x':
            case 'X':
            case 'a':
            case 'A':
            case '9':
                size++;
                index++;
                break;
            case 'V':
            case 'v':
                field.setDecimalPosition(size);
                index++;
                break;
            case 'S':
            case 's':
                index++;
                break;
            case '.':
                if (index == (length - 1)) {
                    return size;
                } else {
                    index++;
                }
                size++;
                break;
            default:
                throw invalidCopyBookException("Invalid character: " + currentChar + " in pic statement.");
            }
        }
        return size;
    }

    /**
    * This method calculates the size of numeric fields in which the pic statement begins with "9"
    */
    private int calculateSizeOfNumeric(String picStatement, String[] tokens, int index, FieldMetaData field) throws Exception {
        int size = 0;
        int digits = 0;

        if (picStatement.toUpperCase().startsWith("S")) {
            field.setIsSigned(true);
        }

        for (int i = index; i < tokens.length; i++, index++) {
            String nextStatement = tokens[i];

            //determine if there is usage other than display default
            if (nextStatement.equalsIgnoreCase("comp") || nextStatement.equalsIgnoreCase("computational")) {
                digits = calculateSizeOfAlphaNumeric(picStatement, field);
                field.setType(FieldMetaData.BINARY);
                if (digits < 3) {
                    size = 1;
                } else if (digits < 5) {
                    size = 2;
                } else if (digits < 8) {
                    size = 3;
                } else if (digits < 10) {
                    size = 4;
                } else if (digits < 13) {
                    size = 5;
                } else if (digits < 15) {
                    size = 6;
                } else if (digits < 17) {
                    size = 7;
                } else if (digits < 20) {
                    size = 8;
                }
                if (field.isSigned() && ((digits == 7) || (digits == 12) || (digits == 19))) {
                    size++;
                }
            } else if (nextStatement.equalsIgnoreCase("comp-2") || nextStatement.equalsIgnoreCase("computational-2")) {
                //size of float, should not be encountered
                field.setType(FieldMetaData.MANTISSA);
                return 4;
            } else if (nextStatement.equalsIgnoreCase("comp-3") || nextStatement.equalsIgnoreCase("computational-3") || nextStatement.equalsIgnoreCase("packed-decimal")) {
                int tempSize = calculateSizeOfAlphaNumeric(picStatement, field);
                field.setType(FieldMetaData.PACKED_DECIMAL);
                size = (tempSize + 1) / 2;
                if (((tempSize + 1) % 2) > 0) {
                    size++;
                }
            } else if (nextStatement.equalsIgnoreCase("seperate")) {
                size = calculateSizeOfAlphaNumeric(picStatement, field);
                size++;
            } else {
                field.setType(FieldMetaData.ALPHA_NUMERIC);
                size = calculateSizeOfAlphaNumeric(picStatement, field);
            }
        }
        return size;
    }

    /**
    * This method returns true if the string word equals one of a list of keywords.
    */
    private boolean isKeyWord(String word) {
        String[] keyWords = { "pic", "picture", "redefines", "blank", "external", "global", "justified", "just", "occurs" };

        for (int i = 0; i < keyWords.length; i++) {
            if (word.equalsIgnoreCase(keyWords[i])) {
                return true;
            }
        }
        return false;
    }

    /**
    * This method will create a invalid copybook exception and add the line number and line
    * at which the exception occurred
    */
    private CopyBookParseException invalidCopyBookException(String message) {
        return CopyBookParseException.invalidCopyBookException(message + " Error occrured on line " + currentLineNumber + ":" + currentLine);
    }

    /**
    * This method adds the internal exception if it is provided
    */
    private CopyBookParseException invalidCopyBookException(String message, Exception internalException) {
        CopyBookParseException exception = invalidCopyBookException(message);
        exception.setInternalException(internalException);
        return exception;
    }
}
