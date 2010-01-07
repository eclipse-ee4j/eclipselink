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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Collections;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Bug# 219097
 * This test would normally pass, but we purposely invoke an SQLException on the firstName field
 * so that we can test that an UnsupportedOperationException is not thrown as part of the 
 * roll-back exception handling code for an SQLException.
 * @author mobrien
 */
public class CommitUnitOfWorkForcingSQLExceptionTest extends UnitOfWorkEventTest {
	
	/** The field length for the firstname */
	public static final int MAX_FIRST_NAME_FIELD_LENGTH = 255;
	
    public void setup() {
        super.setup();
        setDescription("Test force of SQLException to exercise the rollback exception handling code");
    }

    public void test() {
    	boolean exceptionThrown = false;
    	// Set an immutable properties Map on the UOW session to test addition of properties to this map in the roll-back exception handler
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ((UnitOfWorkImpl)uow).setProperties(Collections.emptyMap());
        Employee anEmployee = (Employee)uow.readObject(Employee.class);
        
        /*
         * Provoke an SQL exception by setting a field with a value greater than field length.
         * 1 - This test will not throw an exception without the Collections$emptyMap() set on the EntityhManager
         *       or the exceeded field length set on firstName.
         * 2 - This test will throw an UnsupportedOperationException if the map on AbstractSession is not cloned when immutable - bug fix
         * 3 - This test will throw an SQLException when operating normally due to the field length exception
         */
        StringBuffer firstName = new StringBuffer("firstName_maxfieldLength_");
        for(int i=0; i<MAX_FIRST_NAME_FIELD_LENGTH + 100; i++) {
        	firstName.append("0");
        }
        anEmployee.setFirstName(firstName.toString());        

        try {
            uow.commit();

        } catch (Exception e) {
        	Throwable cause = e.getCause();
        	if(cause instanceof UnsupportedOperationException) {
        		exceptionThrown = true;
        		fail(cause.getClass() + " Exception was thrown in error instead of expected SQLException.");
        	} else {
        		exceptionThrown = true;
        	}
        } finally {
            // Release transaction mutex
            //((AbstractSession)uow).rollbackTransaction();
        }
        if(!exceptionThrown) {
        	fail("An expected SQLException was not thrown.");
        }
    }
}
