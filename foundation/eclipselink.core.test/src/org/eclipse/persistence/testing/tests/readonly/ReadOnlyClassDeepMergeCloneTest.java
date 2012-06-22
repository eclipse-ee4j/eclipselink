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
package org.eclipse.persistence.testing.tests.readonly;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.readonly.Country;
import org.eclipse.persistence.testing.models.readonly.Address;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

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

    public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new Vector());
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void test() {
        // Have the countries standing by, outside the unit of work.
        Vector countries = getSession().readAllObjects(Country.class);

        // Acquire a unit of work with a class read-only.
        Vector readOnlyClasses = new Vector();
        readOnlyClasses.addElement(Country.class);
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
            throw new TestErrorException("Test not run failed to serialize objects:" + ex.toString());
        }
        uow.deepMergeClone(serializedAddress);
        if (addressClone.getCountry() == serializedAddress.getCountry()) {
            throw new TestErrorException("Deepmerge clone merge lost object identity for readonly object");
        }
    }
}
