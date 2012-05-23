/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.types;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * Tests strings.
 */
public class StringTester extends TypeTester {
    public String varString;
    public String fixedString;

    public StringTester() {
        super("NULL");
        varString = null;
        fixedString = null;
    }

    public StringTester(String nameOfTest, String example) {
        super(nameOfTest);
        varString = example;
        fixedString = example;
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(StringTester.class);
        descriptor.setTableName("STRINGS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("varString", "VAR");
        descriptor.addDirectMapping("fixedString", "FIXED_STRING");
        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(StringTester.class);
        descriptor.setTableName("STRINGS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("varString", "getVarString", "setVarString", "VAR");
        descriptor.addDirectMapping("fixedString", "getFixedString", "setFixedString", "FIXED_STRING");
        return descriptor;
    }

    public boolean equals(StringTester otherTester, Session session) throws TestWarningException {
        if (!getTestName().equals(otherTester.getTestName())) {
            return false;
        }
        if ((!Helper.rightTrimString(getVarString(stringLength())).equals(otherTester.getVarString())) && !((getVarString().length() == 0) && (otherTester.getVarString() == null))) {
            return false;
        }
        if ((!Helper.rightTrimString(getFixedString(stringLength())).equals(otherTester.getFixedString())) && !((getFixedString().length() == 0) && (otherTester.getFixedString() == null))) {
            return false;
        }
        if ((otherTester.getFixedString() == null) || (otherTester.getVarString() == null)) {
            throw new TestWarningException("Empty columns can be returned as NULL.");
        }
        return true;
    }

    public String getFixedString() {
        return fixedString;
    }

    public String getFixedString(int fieldLength) {
        if (fixedString.length() > stringLength()) {
            return fixedString.substring(0, stringLength());
        }
        return fixedString;
    }

    public String getVarString() {
        return varString;
    }

    public String getVarString(int fieldLength) {
        if (varString.length() > stringLength()) {
            return varString.substring(0, stringLength());
        }
        return varString;
    }

    public void setFixedString(String aString) {
        fixedString = aString;
    }

    public void setVarString(String aString) {
        varString = aString;
    }

    public static int stringLength() {
        return 30;
    }

    /**
     *Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition(Session session) {
        TableDefinition definition = TypeTester.tableDefinition();

        definition.setName("STRINGS");

        definition.addField("VAR", String.class, StringTester.stringLength());
        definition.addField("FIXED_STRING", Character.class, StringTester.stringLength());
        return definition;
    }

    public static Vector testInstances() {
        Vector tests = new Vector();

        tests.addElement(new StringTester());
        tests.addElement(new StringTester("Empty", ""));
        tests.addElement(new StringTester("Date", new java.util.Date().toString()));
        tests.addElement(new StringTester("Too Long", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        tests.addElement(new StringTester("Leading Blanks", "     5 blanks"));
        tests.addElement(new StringTester("Trailing Blanks", "5 blanks     "));
        tests.addElement(new StringTester("Quotes", "\"\'`"));
        return tests;
    }

    public String toString() {
        return "StringTester(" + getTestName() + ")";
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify(WriteTypeObjectTest testCase) throws TestErrorException, TestWarningException {
        if ((caughtException != null) && (getVarString().length() > 30)) {
            throw new TestWarningException("Too long exception: " + caughtException.toString());
        }
        try {
            super.verify(testCase);
        } catch (TestErrorException verifyFailedException) {
            // Char fields always are trimmed (by us) this is an error if they are not
            if (((StringTester)testCase.getObjectFromDatabase()) != null) {
                String fixed = ((StringTester)testCase.getObjectFromDatabase()).getFixedString();
                if ((fixed != null) && (fixed.length() > 0) && (fixed.charAt(fixed.length() - 1) == ' ')) {
                    if (fixed.equals(" ")) {// Stupid drivers make "" -> " "
                        throw new TestWarningException("This driver converts '''' to '' ''.");
                    }
                    throw new TestErrorException("Trailing blanks were not removed from char field.");
                }
            }

            // Databases which return empty strings as null
            if ((getVarString().length() == 0) && (((StringTester)testCase.getObjectFromDatabase()).getVarString() == null)) {
                throw new TestWarningException("A null was returned for the empty string");
            }

            // Database returned a " " when an empty string was stored.
            if ((getVarString().length() == 0) && ((StringTester)testCase.getObjectFromDatabase()).getVarString().equals(" ")) {
                throw new TestWarningException("A single space string was returned instead of an empty string");
            }

            // Databases which remove trailing blanks
            if ((getVarString().length() > 0) && (getVarString().charAt(getVarString().length() - 1) == ' ') && getVarString().trim().equals(((StringTester)testCase.getObjectFromDatabase()).getVarString())) {
                throw new TestWarningException("Trailing blanks were removed");
            }

            // Databases which remove leading blanks			
            if ((getVarString().length() > 0) && (getVarString().charAt(0) == ' ') && getVarString().trim().equals(((StringTester)testCase.getObjectFromDatabase()).getVarString())) {
                throw new TestWarningException("Leading blanks were removed");
            }

            // Database truncates strings which are too long
            if ((getVarString().length() > 30) && (((StringTester)testCase.getObjectFromDatabase()).getVarString().length() == 30) && getVarString().startsWith(((StringTester)testCase.getObjectFromDatabase()).getVarString())) {
                throw new TestWarningException("The string was truncated.");
            }

            // Char fields always are trimmed (by us)		
            if ((getFixedString().length() > 0) && (getFixedString().charAt(getFixedString().length() - 1) == ' ') && getFixedString().trim().equals(((StringTester)testCase.getObjectFromDatabase()).getFixedString())) {
                throw new TestWarningException("Trailing blanks were removed from char field.");
            }

            if (caughtException != null) {
                throw caughtException;
            }
            throw verifyFailedException;
        }
    }
}
