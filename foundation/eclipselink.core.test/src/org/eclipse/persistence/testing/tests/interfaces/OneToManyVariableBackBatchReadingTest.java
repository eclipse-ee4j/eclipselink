/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
