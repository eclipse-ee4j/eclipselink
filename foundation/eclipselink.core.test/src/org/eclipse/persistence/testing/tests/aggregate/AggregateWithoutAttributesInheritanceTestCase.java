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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.Switch;
import org.eclipse.persistence.testing.models.aggregate.SwitchStateON;

/**
 * Test to make sure that the appropriate update is made when an aggregate using inheriance is
 * changed from one subclass to another in a special case when aggregates contain no mapped attributes
 */
public class AggregateWithoutAttributesInheritanceTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public boolean ok;

    public void reset() {
    }

    public void setup() {
    }

    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();

        // create a new switch - it's created with OFF state
        Switch sw = new Switch();

        UnitOfWork uow = session.acquireUnitOfWork();
        Switch swClone = (Switch)uow.registerObject(sw);
        uow.commit();

        // now change the state to ON
        uow = session.acquireUnitOfWork();
        swClone = (Switch)uow.registerObject(sw);
        swClone.state = new SwitchStateON();
        uow.commit();

        ok = sw.state.getClass() == SwitchStateON.class;
    }

    public void verify() {
        if (!ok) {
            throw new TestErrorException("The Aggregate was changed, but the cache was not merged.");
        }
    }
}
