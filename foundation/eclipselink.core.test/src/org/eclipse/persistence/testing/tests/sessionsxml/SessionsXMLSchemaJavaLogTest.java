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
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * This test will read a session xml with logging set to java log and test to ensure an 
 * expected logging message is generated in a log file.
 */
public class

SessionsXMLSchemaJavaLogTest extends AutoVerifyTestCase {
    DatabaseSession employeeSession;
    protected Exception generationException = null;
    protected Exception fileReadException = null;
    protected String testString = "EmployeeSession login successful";
    protected String fileName = "java.log";

    public SessionsXMLSchemaJavaLogTest() {
        setDescription("Test loading of a session xml with java logging against the XML Schema");
    }

    public void reset() {
        if (employeeSession != null && employeeSession.isConnected()) {
            employeeSession.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession);
            employeeSession = null;
        }
        File file = new File(fileName);
        file.delete();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        try {
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaJavaLog.xml");
            employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader(), false, true);

            Logger.getLogger("org.eclipse.persistence").addHandler(new FileHandler(fileName));
            employeeSession.login();
        } catch (Exception e) {
            generationException = e;
        }
    }

    protected void verify() {
        if (generationException != null) {
            throw new TestErrorException("Exception thrown during session configuration: " + generationException.toString());
        }
        if (!findStringInFile(testString, fileName)) {
            String exceptionString = "String: " + testString + " not found in " + fileName;
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
