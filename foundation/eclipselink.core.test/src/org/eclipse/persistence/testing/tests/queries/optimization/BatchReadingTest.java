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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.tools.schemaframework.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.collections.Restaurant;

public class BatchReadingTest extends TestCase {
    BatchFetchType batchType;

    public Vector result;

    public BatchReadingTest(BatchFetchType batchType) {
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void setup() {
        //getAbstractSession().beginTransaction();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Restaurant.class);
        query.addBatchReadAttribute("menus");
        result = (Vector)getSession().executeQuery(query);

    }

    public void verify() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Vector v = manager.getAllObjectsForClass(Restaurant.class);
        for (Enumeration enumtr = result.elements(); enumtr.hasMoreElements(); ) {
            Restaurant resDatabase = (Restaurant)enumtr.nextElement();
            for (Enumeration enum1 = v.elements(); enum1.hasMoreElements(); ) {
                Restaurant resPop = (Restaurant)enum1.nextElement();
                if (resDatabase.getName().equals(resPop.getName())) {
                    if (!((AbstractSession)getSession()).compareObjects(resDatabase, resPop)) {
                        throw new TestErrorException("Batchreading - one To Many Relationship : Object from database (" +
                                                                                          resDatabase +
                                                                                          ")is not equal to Object from PopulationManager(" +
                                                                                          resPop + ")");
                    }
                }

            }
        }
    }
}
