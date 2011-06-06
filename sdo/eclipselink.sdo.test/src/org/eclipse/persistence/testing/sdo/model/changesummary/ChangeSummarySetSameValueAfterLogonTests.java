/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

public class ChangeSummarySetSameValueAfterLogonTests extends ChangeSummaryCreatedModifiedDeletedTestCase {
    public ChangeSummarySetSameValueAfterLogonTests(String name) {
        super(name);
    }

    public void testSetNullBeforeLoginSetNullAfterLogin() {
        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);

        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();

        buildTreeWithoutChildChangeSummaries();

        dataObjectD.set(propertyD, null);

        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        checkOldSettingsSizeTree("0000", changeSummaryA, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> CS-A
        //   -> B
        //   -> C
        //        -> D
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryA,//
                          root, null,// root is above csA 
                          dataObjectB, root,// B is a child of root at the level of csA 
                          dataObjectC, root,// C is a child of root at the level of csA
                          dataObjectD, dataObjectB);// D is a child of B inside csA scope

        dataObjectD.set(propertyD, null);

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        checkOldSettingsSizeTree("0000", changeSummaryA, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> CS-A
        //   -> B
        //   -> C
        //        -> D
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryA,//
                          root, null,// root is above csA 
                          dataObjectB, root,// B is a child of root at the level of csA 
                          dataObjectC, root,// C is a child of root at the level of csA
                          dataObjectD, dataObjectB);// D is a child of B inside csA scope
    }

    public void testSetSameValueBeforeAfterLogin() {
        rootType.addDeclaredProperty(rootChangeSummaryProperty);
        root = (SDODataObject)dataFactory.create(rootType);

        changeSummaryA = (SDOChangeSummary)root.getChangeSummary();

        buildTreeWithoutChildChangeSummaries();

        dataObjectD.set(propertyD, "test");

        changeSummaryA.beginLogging();

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        checkOldSettingsSizeTree("0000", changeSummaryA, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> CS-A
        //   -> B
        //   -> C
        //        -> D
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryA,//
                          root, null,// root is above csA 
                          dataObjectB, root,// B is a child of root at the level of csA 
                          dataObjectC, root,// C is a child of root at the level of csA
                          dataObjectD, dataObjectB);// D is a child of B inside csA scope

        dataObjectD.set(propertyD, "test");

        assertUnchanged(root, changeSummaryA);
        assertUnchanged(dataObjectB, changeSummaryA);
        assertUnchanged(dataObjectC, changeSummaryA);
        assertUnchanged(dataObjectD, changeSummaryA);

        checkOldSettingsSizeTree("0000", changeSummaryA, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> CS-A
        //   -> B
        //   -> C
        //        -> D
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryA,//
                          root, null,// root is above csA 
                          dataObjectB, root,// B is a child of root at the level of csA 
                          dataObjectC, root,// C is a child of root at the level of csA
                          dataObjectD, dataObjectB);// D is a child of B inside csA scope
    }
}
