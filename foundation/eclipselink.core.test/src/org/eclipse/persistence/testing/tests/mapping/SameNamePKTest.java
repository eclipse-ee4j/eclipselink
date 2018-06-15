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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.*;

/**
 * Bug 5031472
 * Test a query on an Object model that has an object with a OneToOneMapping that
 * has the same attribute name on the source of the OneToOneMapping as an attribute on the
 * destination.
 *
 * In this bug, if the FK of the OneToOneMapping was a component of the PK of the source
 * object, an exception would be thrown.
 *
 * @author tware
 */
public class SameNamePKTest extends TestCase {

    protected Long systemId = null;
    protected SecureSystem system = null;
    protected Exception exception = null;

    public void setup() {
        beginTransaction();
        // Insert an example Object
        SecureSystem system = new SecureSystem();
        system.setManufacturer("Secure Systems Inc.");
        Identification identification = new Identification();
        identification.setId(new Long(1));
        system.setId(identification);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(system);
        uow.commit();
        systemId = identification.getId();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery(SecureSystem.class);
        ExpressionBuilder aBuilder = new ExpressionBuilder();
        // query on both "id" attributes.
        Expression exp = aBuilder.get("id").get("id").equal(systemId);
        query.setSelectionCriteria(exp);
        try {
            system = (SecureSystem)getSession().executeQuery(query);
        } catch (DescriptorException e) {
            exception = e;
        }
    }

    public void verify() {
        if (exception != null) {
            throw new TestErrorException("An exception was thrown while trying to query across two attributes of the same name.",
                                         exception);
        }
        if (system == null) {
            throw new TestErrorException("No result retrieved when trying to query across two attributes of the same name.");
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        exception = null;
        system = null;
    }

}
