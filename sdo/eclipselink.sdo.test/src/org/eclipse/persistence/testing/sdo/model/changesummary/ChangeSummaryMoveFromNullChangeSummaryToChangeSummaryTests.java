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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;

public class ChangeSummaryMoveFromNullChangeSummaryToChangeSummaryTests extends ChangeSummaryCreatedModifiedDeletedTestCase {
    public ChangeSummaryMoveFromNullChangeSummaryToChangeSummaryTests(String name) {
        super(name);
    }

    public void testMoveObjectFromNullChangeSummaryToChangeSummaryDetach() {
        buildTreeWithoutChangeSummary();

        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);

        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();

        buildTreeWithoutChildChangeSummaries();

        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        assertEquals(0, changeSummaryA.getChangedDataObjects().size());

        assertNull(changeSummaryA.getOldContainer(dataObjectF));

        dataObjectF.detach();

        assertEquals(0, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        // move D as child of C
        dataObjectC.set(propertyC, dataObjectF);

        // the 3 normally modified-c, modifed-b, deleted-d/created-d flags are reduced to 
        // 2 modified flags on c and b because created-d cancels deleted-d
        assertEquals(2, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertModified(dataObjectC, changeSummaryA);
        assertCreated(dataObjectF, changeSummaryA);

    }

    public void testMoveObjectFromNullChangeSummaryToChangeSummaryUnset() {
        buildTreeWithoutChangeSummary();

        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);

        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();

        buildTreeWithoutChildChangeSummaries();

        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        assertEquals(0, changeSummaryA.getChangedDataObjects().size());

        assertNull(changeSummaryA.getOldContainer(dataObjectF));

        root1.unset(p_root1);

        assertEquals(0, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        // move D as child of C
        dataObjectC.set(propertyC, dataObjectF);

        // the 3 normally modified-c, modifed-b, deleted-d/created-d flags are reduced to 
        // 2 modified flags on c and b because created-d cancels deleted-d
        assertEquals(2, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertModified(dataObjectC, changeSummaryA);
        assertCreated(dataObjectF, changeSummaryA);

    }

    public void testMoveObjectFromNullChangeSummaryToChangeSummaryDelete() {
        buildTreeWithoutChangeSummary();

        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);

        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();

        buildTreeWithoutChildChangeSummaries();

        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        assertEquals(0, changeSummaryA.getChangedDataObjects().size());

        assertNull(changeSummaryA.getOldContainer(dataObjectF));

        dataObjectF.delete();

        assertEquals(0, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        // move D as child of C
        dataObjectC.set(propertyC, dataObjectF);

        // the 3 normally modified-c, modifed-b, deleted-d/created-d flags are reduced to 
        // 2 modified flags on c and b because created-d cancels deleted-d
        assertEquals(2, changeSummaryA.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertModified(dataObjectC, changeSummaryA);
        assertCreated(dataObjectF, changeSummaryA);

    }
}
