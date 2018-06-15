/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmployee;


// Currently nested unit of work is not supported for attribute change tracking.
// This test case tests if the correct exception is thrown when a nested uow is registered.
public class AggregateAttributeChangeTrackingTest extends AutoVerifyTestCase {

    public AggregateAttributeChangeTrackingTest() {
        setDescription("Tests that the ALCT policy is copied to the aggregate object");
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ALCTEmployee emp = (ALCTEmployee)uow.readObject(ALCTEmployee.class);
        emp.getPeriod().setEndDate(new java.sql.Date(191099988948748948L));
        if (emp._persistence_getPropertyChangeListener() == null) {
            throw new TestErrorException("Attribute Level Change Tracking not active in aggregate");
        }
        if (!((AttributeChangeListener)emp._persistence_getPropertyChangeListener()).hasChanges()) {
            throw new TestErrorException("Attribute Level Change Tracking not active in aggregate");
        }


    }
}


