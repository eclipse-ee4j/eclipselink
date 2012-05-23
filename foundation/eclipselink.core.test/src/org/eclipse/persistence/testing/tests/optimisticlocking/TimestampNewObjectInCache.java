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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.optimisticlocking.LockInObject;
import org.eclipse.persistence.testing.models.optimisticlocking.TimestampInObject;

public class TimestampNewObjectInCache extends TestCase {
    protected Object objectToBeRead;
    protected Object lockingObject;
    public boolean isTio;

    public TimestampNewObjectInCache(Object o) {
        setName("TimestampNewObjectInCache(" + o.getClass().getName() + ")");
        lockingObject = o;
        this.setDescription("This tests whether a new object that uses Timestamp or Version locking is added to Cache.");
    }

    public void test() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();
        ExpressionBuilder bldr = new ExpressionBuilder();
        ReadObjectQuery queryObject = new ReadObjectQuery();
        queryObject.checkCacheOnly();

        if (lockingObject instanceof TimestampInObject) {
            isTio = true;
            TimestampInObject tio = (TimestampInObject)lockingObject;
            uow.registerObject(tio);
            uow.commit();
            Expression exp = bldr.get("id").equal(tio.id);
            queryObject.setSelectionCriteria(exp);
            queryObject.setReferenceClass(TimestampInObject.class);
            objectToBeRead = getSession().executeQuery(queryObject);

        } else if (lockingObject instanceof LockInObject) {
            isTio = false;
            LockInObject ov = (LockInObject)lockingObject;
            uow.registerObject(ov);
            uow.commit();
            Expression exp = bldr.get("id").equal(ov.id);
            queryObject.setSelectionCriteria(exp);
            queryObject.setReferenceClass(LockInObject.class);
            objectToBeRead = getSession().executeQuery(queryObject);
        }
    }

    public void verify() {
        if (objectToBeRead != lockingObject) {
            if (isTio) {
                throw new TestErrorException("New Objects using Timestamp locking are not put in Cache.");
            } else {
                throw new TestErrorException("New Objects using Version locking are not put in Cache.");
            }
        }
    }
}
