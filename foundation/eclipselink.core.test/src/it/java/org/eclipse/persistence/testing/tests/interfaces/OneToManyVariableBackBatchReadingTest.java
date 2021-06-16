/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.interfaces;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class OneToManyVariableBackBatchReadingTest extends AutoVerifyTestCase {
    public List result;

    public OneToManyVariableBackBatchReadingTest() {
        setDescription("Tests batch reading with a 1-m with a variable 1-1 back.");
    }

    public void test() {
        // First instantiate programs.
        List actors = getSession().readAllObjects(Actor.class);
        for (Iterator iterator = actors.iterator(); iterator.hasNext(); ) {
            ((Actor)iterator.next()).program.getName();
        }
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Documentary.class);
        query.refreshIdentityMapResult();
        query.addBatchReadAttribute("actors");
        result = (List)getSession().executeQuery(query);
    }

    public void verify() {
        Documentary documentary = (Documentary)result.get(0);
        strongAssert((documentary.actors.size() > 0), "Test failed. Batched objects were not read.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
