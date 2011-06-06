/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.io.StringWriter;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Tests the use of Long (> 255) strings
 */
public class CLOBTester extends TypeTester {
    public String longString;
    protected int length;

    public CLOBTester() {
        this(0);
    }

    public CLOBTester(int numChars) {
        super("CLOB(" + numChars + ")");
        length = numChars;
        longString = null;
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(CLOBTester.class);
        descriptor.setTableName("CLOBS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("longString", "THECLOB");
        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(CLOBTester.class);
        descriptor.setTableName("CLOBS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("longString", "getLongString", "setLongString", "THECLOB");
        return descriptor;
    }

    public boolean equals(CLOBTester otherTester, Session session) throws TestWarningException {
        if (!getTestName().equals(otherTester.getTestName())) {
            return false;
        }
        if ((!getLongString().equals(otherTester.getLongString())) && !((getLongString().length() == 0) && (otherTester.getLongString() == null))) {
            return false;
        }
        if (otherTester.getLongString() == null) {
            throw new TestWarningException("Empty columns can be returned as NULL.");
        }
        return true;
    }

    public String getLongString() {
        return longString;
    }

    public void setLongString(String newString) {
        longString = newString;
    }

    public void setup(Session session) {
        // Access and DB2 do not support BLOBS or CLOBS
        if (session.getPlatform().isAccess()) {
            throw new TestWarningException("Access does not support BLOBS or CLOBS.");
        }

        if (longString == null) {
            String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`1234567890-=\\~!@#$%^&*()_+|[]{};':\",./<>?";

            // \ is always treated as escape and removed from String in MySQL.  Not sure if it is a bug
            if (session.getPlatform().isMySQL()) {
                chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`1234567890-=~!@#$%^&*()_+|[]{};':\",./<>?";
            }
            StringWriter writer = new StringWriter();
            for (int index = 0, charIndex = 0; index < length; index++, charIndex++) {
                if (charIndex == chars.length()) {
                    charIndex = 0;
                }
                writer.write(chars.charAt(charIndex));
            }
            longString = writer.toString();
        }
    }

    /**
     *Return a platform independent definition of the database table.
     */
    public static TableDefinition tableDefinition(Session session) {
        TableDefinition definition = TypeTester.tableDefinition();

        definition.setName("CLOBS");

        definition.addField("THECLOB", Character[].class, 25000);
        return definition;
    }

    public static Vector testInstances() {
        Vector tests = new Vector();

        tests.addElement(new CLOBTester(1000));
        tests.addElement(new CLOBTester(5000));
        tests.addElement(new CLOBTester(10000));
        tests.addElement(new CLOBTester(25000));
        return tests;
    }

    public String toString() {
        return getTestName();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify(WriteTypeObjectTest testCase) throws TestErrorException, TestWarningException {
        try {
            super.verify(testCase);
        } catch (TestErrorException verifyFailedException) {
            // Database bridges which do not support long literal values
            if ((caughtException != null) && (testCase.getSession().getPlatform().isOracle() && (((java.sql.SQLException)((DatabaseException)caughtException).getInternalException()).getErrorCode() == 1704))) {
                throw new TestWarningException("CLOB Write failed. " + caughtException.toString() + "\n Turn on String Binding and re-run test");
            }

            if (testCase.getObjectFromDatabase() == null) {
                throw new TestWarningException("CLOB Write failed.  Object returned as null");
            }
            if (equals((CLOBTester)testCase.getObjectFromDatabase(), testCase.getSession())) {
                return;
            }
            throw verifyFailedException;
        }
    }
}
