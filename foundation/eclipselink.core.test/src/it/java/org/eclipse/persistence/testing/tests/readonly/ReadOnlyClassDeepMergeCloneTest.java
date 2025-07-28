/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.readonly;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.readonly.Address;
import org.eclipse.persistence.testing.models.readonly.Country;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: Test merging a non-read-only object which has a reference to a read-only object.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Verify object identity of the read-only object.
 * </ul>
 */
public class ReadOnlyClassDeepMergeCloneTest extends AutoVerifyTestCase {
    public Address address;

    public ReadOnlyClassDeepMergeCloneTest() {
        super();
    }

    @Override
    public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new ArrayList<>());
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void test() {
        // Have the countries standing by, outside the unit of work.
        Vector countries = getSession().readAllObjects(Country.class);

        // Acquire a unit of work with a class read-only.
        Vector readOnlyClasses = new Vector();
        readOnlyClasses.add(Country.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.removeAllReadOnlyClasses();
        uow.addReadOnlyClasses(readOnlyClasses);

        // Read in an existing Address.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("country").get("name").equal("United Kingdom");
        address = (Address)getSession().readObject(Address.class, exp);
        Address addressClone = (Address)uow.registerObject(address);
        Address serializedAddress = null;
        try {
            ByteArrayOutputStream outArray = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outArray);
            out.writeObject(addressClone);
            ByteArrayInputStream inArray = new ByteArrayInputStream(outArray.toByteArray());
            ObjectInputStream in = new ObjectInputStream(inArray);
            serializedAddress = (Address)in.readObject();
        } catch (Exception ex) {
            throw new TestErrorException("Test not run failed to serialize objects:" + ex);
        }
        uow.deepMergeClone(serializedAddress);
        if (addressClone.getCountry() == serializedAddress.getCountry()) {
            throw new TestErrorException("Deepmerge clone merge lost object identity for readonly object");
        }
    }
}
