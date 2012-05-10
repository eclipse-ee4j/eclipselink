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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.relational.*;
import org.eclipse.persistence.mappings.*;

/**
 * To test the functionality of ExceptionHandler.
 * ExceptionHandler can catch errors that occur on queries or during database access.
 * The exception handler has the option of re-throwing the exception, throwing a different
 * exception or re-trying the query or database operation.
 * This tests that when TopLink throws an Integrity exception at boot time,
 * it is logged through our logging mechanism CR#4011
 */
public class ExceptionHandlerLoginIntegrityTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public org.eclipse.persistence.sessions.Project project;
    public org.eclipse.persistence.sessions.DatabaseSession session;
    public org.eclipse.persistence.sessions.DatabaseLogin login;
    protected Exception caughtException = null;

    public ExceptionHandlerLoginIntegrityTest() {
        setDescription("To test if login IntegrityExceptions are passed to the ExceptionHandler");
    }

    public void test() {
        try {
            session = project.createDatabaseSession();
            session.dontLogMessages();
            Handler handler = new Handler();
            session.setExceptionHandler(handler);
            session.login();
            session.logout();
        } catch (org.eclipse.persistence.exceptions.IntegrityException ie) {
            caughtException = ie;
        }
    }

    public void verify() throws Exception {
        if (caughtException != null) {
            throw new TestErrorException("Test to see if IntegrityExceptions caused at boot time are passed " + "to the exceptionHandler failed.\n " + "----- ExceptionHandlerTest3 -----\n" + caughtException.getMessage());
        }
    }

    public void setup() {
        login = getSession().getLogin();
        project = new EmployeeProject();
        project.setLogin(login);
        java.util.Map descriptors = project.getDescriptors();

        //java.util.Enumeration e =ht.elements();(Descriptor)e.nextElement();
        ClassDescriptor descriptor = (ClassDescriptor)descriptors.get(org.eclipse.persistence.testing.models.employee.domain.Employee.class);

        //add a non existent mapping so loggin will throw an integrity exception
        DirectToFieldMapping NonExistentFieldMapping = new DirectToFieldMapping();
        NonExistentFieldMapping.setAttributeName("nonExistent");
        descriptor.addMapping(NonExistentFieldMapping);

    }
}
