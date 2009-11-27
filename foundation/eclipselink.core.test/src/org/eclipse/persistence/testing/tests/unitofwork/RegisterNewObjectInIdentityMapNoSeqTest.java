/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * This test is in responce to a support question.  There was a problem with
 * The stability of the cache when an object was registered with the Unit Of
 * Work as a new object.
 */
public class RegisterNewObjectInIdentityMapNoSeqTest extends AutoVerifyTestCase {
    public String sequenceNumberName;
    public DatabaseField sequenceNumberField;

    public RegisterNewObjectInIdentityMapNoSeqTest() {
        setDescription("This test verifies the the UOW cache when registering a new object with a primitive primary key");
    }

    public void reset() {
        ClassDescriptor descriptor = getSession().getClassDescriptor(Weather.class);
        descriptor.setIdValidation(IdValidation.ZERO);
        descriptor.setSequenceNumberField(this.sequenceNumberField);
        descriptor.setSequenceNumberName(this.sequenceNumberName);
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
        ClassDescriptor descriptor = getSession().getClassDescriptor(Weather.class);
        // Allow test to use 0 primary key (now not supported by default).
        descriptor.setIdValidation(IdValidation.NULL);
        sequenceNumberField = descriptor.getSequenceNumberField();
        descriptor.setSequenceNumberField(null);
        sequenceNumberName = descriptor.getSequenceNumberName();
        descriptor.setSequenceNumberName(null);
    }

    public void test() {
        Session session = getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.setShouldNewObjectsBeCached(true);
        Weather weather = new Weather();
        weather.setStormPattern("Something really bad");
        weather.id = 0;
        ReadObjectQuery query = new ReadObjectQuery(weather);
        query.checkCacheOnly();
        Weather weatherClone = (Weather)uow.registerObject(weather);
        weather = (Weather)uow.executeQuery(query);
        if (weather == null) {
            throw new TestErrorException("Failed to store the new object with the 0 primary key in the cache");
        }
    }
}
