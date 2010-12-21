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
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.io.FileReader;
import java.io.LineNumberReader;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


//Bug#4157545  Added logging options back.  Test if sessions.xml is read correctly and messages are logged correctly
//when logging options tags are missing, or logging options don't have a value (default), or logging options
//are set to true, or false.
public class SessionsXMLSchemaLoggingOptionsTest extends AutoVerifyTestCase {

    DatabaseSession employeeSession;
    DatabaseSession employeeSession2;
    DatabaseSession employeeSession3;
    DatabaseSession employeeSession4;
    //Please do not change these file names as they are set in the XMLSchemaSessionLoggingOptions.xml
    protected String nullOptionsFileName = "logging-options-null.log";
    protected String defaultFileName = "logging-options-default.log";
    protected String trueFileName = "logging-options-true.log";
    protected String falseFileName = "logging-options-false.log";

    protected String sessionString = "DatabaseSessionImpl";
    protected String connectionString = "Connection";
    protected String threadString = "Thread";
    protected Exception fileReadException = null;

    public SessionsXMLSchemaLoggingOptionsTest() {
        setDescription("Test loading of a session xml with various logging options against the XML Schema");
    }

    public void reset() {
        if (employeeSession != null && employeeSession.isConnected()) {
            employeeSession.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession);
            employeeSession = null;
        }
        if (employeeSession2 != null && employeeSession2.isConnected()) {
            employeeSession2.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession2);
            employeeSession2 = null;
        }
        if (employeeSession3 != null && employeeSession3.isConnected()) {
            employeeSession3.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession3);
            employeeSession3 = null;
        }
        if (employeeSession4 != null && employeeSession4.isConnected()) {
            employeeSession4.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession4);
            employeeSession4 = null;
        }
    }

    public void test() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionLoggingOptions.xml");

        // log in the session
            employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "NullOptionsSession", getClass().getClassLoader(), true, false); // refresh the session  

        // log in the session
            employeeSession2 = (DatabaseSession)SessionManager.getManager().getSession(loader, "DefaultSession", getClass().getClassLoader(), true, false); // refresh the session  

        // log in the session
            employeeSession3 = (DatabaseSession)SessionManager.getManager().getSession(loader, "TrueSession", getClass().getClassLoader(), true, false); // refresh the session  

        // log in the session
            employeeSession4 = (DatabaseSession)SessionManager.getManager().getSession(loader, "FalseSession", getClass().getClassLoader(), true, false); // refresh the session  
    }

    protected void verify() {
        verifyFindStrings(sessionString, nullOptionsFileName);
        verifyFindStrings(connectionString, nullOptionsFileName);
        verifyFindStrings(threadString, nullOptionsFileName);
        verifyFindStrings(sessionString, defaultFileName);
        verifyFindStrings(connectionString, defaultFileName);
        verifyNotFindStrings(threadString, defaultFileName);
        verifyFindStrings(sessionString, trueFileName);
        verifyFindStrings(connectionString, trueFileName);
        verifyFindStrings(threadString, trueFileName);
        verifyNotFindStrings(sessionString, falseFileName);
        verifyNotFindStrings(connectionString, falseFileName);
        verifyNotFindStrings(threadString, falseFileName);
    }

    protected void verifyFindStrings(String testString, String fileName) {
        if (!findStringInFile(testString, fileName)) {
            String exceptionString = "String: " + testString + " not found in " + fileName;
            if (fileReadException != null) {
                exceptionString = exceptionString + " Exception thrown while reading file. - " + fileReadException.toString();
            }
            throw new TestErrorException(exceptionString);
        }
    }

    protected void verifyNotFindStrings(String testString, String fileName) {
        if (findStringInFile(testString, fileName)) {
            String exceptionString = "String: " + testString + " found in " + fileName;
            if (fileReadException != null) {
                exceptionString = exceptionString + " Exception thrown while reading file. - " + fileReadException.toString();
            }
            throw new TestErrorException(exceptionString);
        }
    }

    public boolean findStringInFile(String string, String fileName) {
        try {
            FileReader reader = new FileReader(fileName);
            LineNumberReader lnr = new LineNumberReader(reader);
            String line = lnr.readLine();
            while (line != null) {
                if (line.indexOf(string) > -1) {
                    return true;
                }
                line = lnr.readLine();
            }
        } catch (Exception exception) {
            fileReadException = exception;
        }
        ;
        return false;
    }
}
