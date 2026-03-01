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
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.testing.models.ownership.ObjectA;
import org.eclipse.persistence.testing.models.ownership.ObjectB;
import org.eclipse.persistence.testing.models.ownership.ObjectC;
import org.eclipse.persistence.testing.models.ownership.ObjectD;
import org.eclipse.persistence.testing.models.ownership.ObjectE;

import java.util.Vector;

/**
 * Test changing private parts of an object.
 */
public class UpdateDeepOwnershipTest extends ComplexUpdateTest {
    public UpdateDeepOwnershipTest() {
        super();
    }

    public UpdateDeepOwnershipTest(ObjectA originalObject) {
        super(originalObject);
        String testName = "UpdateDeepOwnershipTest(";
        if (usesUnitOfWork) {
            testName = testName + "with UOW, ";
        }
        testName = testName + originalObject.getClass() + ")";

        setName(testName);
        setDescription("This tests changing deeply private parts of an object for update.");
    }

    @Override
    protected void changeObject() {
        ObjectA a = (ObjectA)this.workingCopy;
        ObjectB b = (ObjectB)a.oneToOne.getValue();
        Vector cs = (Vector)b.oneToMany.getValue();
        ObjectC c = (ObjectC)cs.get(0);
        ObjectD d = (ObjectD)c.oneToOne.getValue();
        Vector es = (Vector)d.oneToMany.getValue();
        ObjectE e = (ObjectE)es.get(0);
        e.setName("Foo");
        es.remove(es.lastElement());
        cs.remove(cs.lastElement());
        b.setName("lola");
    }
}
