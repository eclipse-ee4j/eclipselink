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
package org.eclipse.persistence.testing.tests.writing;

import java.util.*;
import org.eclipse.persistence.testing.models.ownership.*;

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

    protected void changeObject() {
        ObjectA a = (ObjectA)this.workingCopy;
        ObjectB b = (ObjectB)a.oneToOne.getValue();
        Vector cs = (Vector)b.oneToMany.getValue();
        ObjectC c = (ObjectC)cs.firstElement();
        ObjectD d = (ObjectD)c.oneToOne.getValue();
        Vector es = (Vector)d.oneToMany.getValue();
        ObjectE e = (ObjectE)es.firstElement();
        e.setName("Foo");
        es.removeElement(es.lastElement());
        cs.removeElement(cs.lastElement());
        b.setName("lola");
    }
}
