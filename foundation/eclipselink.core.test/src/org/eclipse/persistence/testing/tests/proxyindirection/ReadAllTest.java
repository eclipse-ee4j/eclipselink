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
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.Vector;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ReadAllTest extends AutoVerifyTestCase {
    Vector cubicles;

    public ReadAllTest() {
        setDescription("Tests ReadAllObjects using Proxy Indirection.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        ReadAllQuery q = new ReadAllQuery();
        q.setReferenceClass(Cubicle.class);
        cubicles = (Vector)getSession().executeQuery(q);
    }

    public void verify() {
        if (cubicles.size() != 3) {
            throw new TestErrorException("All Cubicles were not read in.  Expected 4, got " + cubicles.size() + ".");
        }

        // Test the indirection
        for (int i = 0; i < cubicles.size(); i++) {
            Cubicle c = (Cubicle)cubicles.elementAt(i);
            if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(EmployeeImpl.class).getSize() != i) {
                throw new TestErrorException("ProxyIndirection did not work - Employee was read in along with Cubicle.");
            }
            c.getEmployee().getFirstName();
            if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(EmployeeImpl.class).getSize() == i) {
                throw new TestErrorException("ProxyIndirection did not work - Employee was not read in when triggered from Cubicle.");

            }
        }
    }
}
