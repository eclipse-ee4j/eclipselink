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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.interfaces.Actor;
import org.eclipse.persistence.testing.models.interfaces.Documentary;

import java.util.List;

public class OneToManyVariableBackBatchReadingTest extends AutoVerifyTestCase {
    public List result;

    public OneToManyVariableBackBatchReadingTest() {
        setDescription("Tests batch reading with a 1-m with a variable 1-1 back.");
    }

    @Override
    public void test() {
        // First instantiate programs.
        List actors = getSession().readAllObjects(Actor.class);
        for (Object actor : actors) {
            ((Actor) actor).program.getName();
        }
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Documentary.class);
        query.refreshIdentityMapResult();
        query.addBatchReadAttribute("actors");
        result = (List)getSession().executeQuery(query);
    }

    @Override
    public void verify() {
        Documentary documentary = (Documentary)result.get(0);
        strongAssert((!documentary.actors.isEmpty()), "Test failed. Batched objects were not read.");
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
