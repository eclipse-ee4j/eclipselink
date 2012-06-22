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
package org.eclipse.persistence.testing.tests.sessioncache;

import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.Shipment;
import org.eclipse.persistence.testing.framework.*;

public class ReadReferencedObjectNotInSessionCacheTest extends TestCase {
    private Employee objectInCache;
    private int originalSize;

    public ReadReferencedObjectNotInSessionCacheTest() {
        setDescription("This test ensures that referenced objects are put in the session cache");
    }

    protected void setup() {
        checkNoWaitSupported();

        // Flush the cache 								
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        //now read an Employee so it's in the session cache        
        objectInCache = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("lastName").equal("White"));

        originalSize = objectInCache.getShipments().size();
        getAbstractSession().beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadObjectQuery query = new ReadObjectQuery(objectInCache);
        Employee emp = (Employee)uow.executeQuery(query);

        ReadObjectQuery query2 = new ReadObjectQuery(Shipment.class);
        query2.setLockMode(ObjectLevelReadQuery.LOCK_NOWAIT);
        Shipment comp = (Shipment)uow.executeQuery(query2);
        
        emp.getShipments().add(comp);        
        uow.commit();        
    }

    protected void verify() {
        Employee empVerify = (Employee)getSession().readObject(objectInCache);
        if (empVerify == null) {
            throw new TestErrorException("Employee should have been in session cache.");
        } else if (empVerify.getShipments() == null) {
            throw new TestErrorException("Employee's shipments should not be null.");
        } else if (empVerify.getShipments().size() != (originalSize + 1)) {
            throw new TestErrorException("Shipment was not added to Employee.");
        }

        //make sure emp.getShipmentsl.zie 1 bigger than originalSize 
        //ensure changes were merged into the session cache      				
        IdentityMap im = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(Shipment.class);
        if ((im == null) || (im.getSize() == 0)) {
            throw new TestErrorException("Shipment should have been put into session cache.");
        }
    }

    public void reset() throws Exception {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
