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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


/**
 * Test for bug 3815959: SETSHOULDPERFORMDELETESFIRST(TRUE) PERFORMS REDUNDANT UPDATE STATEMENTS
 */
public class PerformDeletesFirstIgnoreUpdateTest extends TransactionalTestCase {

    static class UpdateListener extends DescriptorEventAdapter {
        boolean updated;

        public void postUpdate(DescriptorEvent event) {
            updated = true;
        }
    }

    UpdateListener phoneUpdateListener;
    DatabaseException dbException;

    public PerformDeletesFirstIgnoreUpdateTest() {
        setDescription("Verifies that TopLink doesn't issue UPDATE phone number after deleting it");
    }

    public void setup() {
        super.setup();
        phoneUpdateListener = new UpdateListener();
        getSession().getDescriptor(PhoneNumber.class).getEventManager().addListener(phoneUpdateListener);
    }

    public void test() {
        PhoneNumber phone = (PhoneNumber)getSession().readObject(PhoneNumber.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.setShouldPerformDeletesFirst(true);
        PhoneNumber phoneClone = (PhoneNumber)uow.registerObject(phone);

        // The new phone number is too long (it should be no more than 7 characters)
        // therefore is update goes through and not preceded with DELETE DatabaseException will result
        phoneClone.setNumber("0123456789");
        uow.deleteObject(phoneClone);
        try {
            uow.commit();
        } catch (DatabaseException ex) {
            dbException = ex;
        }
    }

    public void verify() {
        if (dbException != null) {
            throw new TestErrorException("Attempted to update Phone number before delete", dbException);
        }
        if (phoneUpdateListener.updated) {
            throw new TestErrorException("Phone number was updated after delete");
        }
    }

    public void reset() {
        if (phoneUpdateListener != null) {
            super.reset();
            getSession().getDescriptor(PhoneNumber.class).getEventManager().removeListener(phoneUpdateListener);
            phoneUpdateListener = null;
            dbException = null;
        }
    }
}
