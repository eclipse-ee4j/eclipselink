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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.transparentindirection.Order;
import org.eclipse.persistence.testing.models.transparentindirection.OrderLine;


/**
 * This verifies that mergeClone works with transparent indirection.
 * There was a bug that the indirection was not instantiated causing insertion of the related objects.
 */
public class MergeCloneWithReferencesTransparentIndirectionTest extends AutoVerifyTestCase {
    Order order;

    public MergeCloneWithReferencesTransparentIndirectionTest() {
        setDescription("This verifies that mergeClone works with transparent indirection.");
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        order = new Order();
        order.addLine(new OrderLine());
        order.addLine(new OrderLine());
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(order);
        uow.commit();
    }

    public void reset() {
        if (order != null) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.deleteObject(order);
            uow.commit();
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        Order order = (Order)getSession().readObject(Order.class);
        order.getLineVector().size();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.readObject(order);
        uow.mergeCloneWithReferences(order);
        uow.commit();
    }
}
