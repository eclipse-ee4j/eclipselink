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
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.inheritance.JavaProgrammer;

public class LeafQueryTest extends org.eclipse.persistence.testing.framework.TestCase {
    public java.util.Vector V;

    public LeafQueryTest() {
        setDescription("Tests query at bottom level of deep inheritance");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        ReadAllQuery q = new ReadAllQuery();
        q.setReferenceClass(JavaProgrammer.class);
        V = (java.util.Vector)getSession().executeQuery(q);

    }

    public void verify() {
        if (V.size() == 0) {
            throw new org.eclipse.persistence.testing.framework.TestException("The query returned 0 results");
        }
    }
}
