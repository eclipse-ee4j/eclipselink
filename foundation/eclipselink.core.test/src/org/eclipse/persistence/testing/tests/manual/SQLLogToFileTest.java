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
package org.eclipse.persistence.testing.tests.manual;

import java.io.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

public class SQLLogToFileTest extends ManualVerifyTestCase {
    String logFileName;
    Writer oldLog;

    /**
     * SQLLoggingTest constructor comment.
     */
    public SQLLogToFileTest() {
        setDescription("This test verifies that SQL can be logged to a file, the sql output should logged.");
    }

    private String getLogFileName() {
        return logFileName;
    }

    public void reset() {
        rollbackTransaction();
        // Set SQL logging to previous values
        getSession().setLog(oldLog);
    }

    private void setLogFileName(String newFileName) {
        logFileName = newFileName;
    }

    protected void setLogging() {
        oldLog = getSession().getLog();
        setLogFileName("SQL.LOG");

        // Create a PrintStream on the given log file. Tell the session to log
        // SQL output to this file.
        try {
            FileWriter fileWriter = new FileWriter(getLogFileName());
            getSession().setLog(fileWriter);
        } catch (IOException exception) {
            TestException testException = new TestException("Error opening stream on file " + getLogFileName());
            testException.setInternalException(exception);
            throw testException;
        }
    }

    public void setup() {
        setLogging();
        beginTransaction();
    }

    public void test() {
        DatabaseSession session = getDatabaseSession();
        Employee anEmployee = (org.eclipse.persistence.testing.models.employee.domain.Employee)new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator().basicEmployeeExample1();

        // Insert an employee into the database
        session.insertObject(anEmployee);

        // Write an employee into the database
        session.writeObject(new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator().basicEmployeeExample2());

        // Read all employees from the database
        session.readAllObjects(Employee.class);

        // Delete objects from the database
        session.deleteObject(anEmployee);

        try {
            session.getLog().close();
        } catch (IOException e) {
        }

        // Set SQL logging to previous values
        session.setLog(oldLog);

        // Dump the contents of the log file to the screen.
        getSession().logMessage("================================================================");
        getSession().logMessage("Dumping contents of log file (" + getLogFileName() + ") to screen.");

        try {
            BufferedReader input = new BufferedReader(new FileReader(getLogFileName()));

            for (String nextLine = ""; nextLine != null; nextLine = input.readLine()) {
                getSession().logMessage(nextLine);
            }
        } catch (FileNotFoundException exception) {
            TestException testException = new TestException("The log file was not found :" + getLogFileName());
            testException.setInternalException(exception);
            throw testException;
        } catch (IOException exception) {
            TestException testException = new TestException("Could not read from log file :" + getLogFileName());
            testException.setInternalException(exception);
            throw testException;
        }

        getSession().logMessage("================== DONE (Dumping contents of log file.) ====================");
    }
}
