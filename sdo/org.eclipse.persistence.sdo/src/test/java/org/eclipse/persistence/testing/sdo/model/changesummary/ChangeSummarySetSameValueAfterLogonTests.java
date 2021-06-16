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
