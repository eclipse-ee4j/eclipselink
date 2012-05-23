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
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Abstract class for all Type testing.
 */
public abstract class TypeTester {
    protected String testName;
    protected EclipseLinkException caughtException;

    /**
     * @param nameOfTest java.lang.String
     */
    public TypeTester(String nameOfTest) {
        setTestName(nameOfTest);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String nameOfTest) {
        testName = nameOfTest;
    }

    /**
    *    Foe testers with large data this method allows for lazy initialization.
    */
    protected void setup(Session session) {
    }

    /**
     *Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.addField("NAME", String.class, 25);
        return definition;
    }

    /**
    *    Double dispatch so that indivual types can check for error conditions during test
    */
    protected void test(WriteTypeObjectTest testCase) {
        try {
            testCase.superTest();
        } catch (EclipseLinkException e) {
            caughtException = e;
        }
    }

    public static Vector testInstances() {
        return new Vector();
    }

    protected void verify(WriteTypeObjectTest testCase) throws TestException {

        /*    if (caughtException != null) {
                throw caughtException;
            }*/
        testCase.superVerify();
    }
}
