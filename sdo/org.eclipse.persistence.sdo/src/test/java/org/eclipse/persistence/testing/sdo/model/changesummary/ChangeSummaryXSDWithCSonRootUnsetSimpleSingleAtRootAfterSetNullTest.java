/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.textui.TestRunner;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterSetNullTest extends ChangeSummaryOnRootTestCases {
    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PurchaseOrderDeepWithCSonRootUnsetSimpleSingleAtRootAfterSetNull.xml");
    }

    @Override
    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterSetNullTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterSetNullTest" };
        TestRunner.main(arguments);
    }

    @Override
    public void setUp() {
        super.setUp();// watch setup redundancy
    }

    @Override
    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        // replace global object with one from xml file (with cs pre-populated)
        rootObject = document.getRootObject();
        cs = rootObject.getChangeSummary();

        assertCSonRootUnsetSimpleSingleAtRootAfterSetNull(true);
    }

    public void testUnsetSimpleSingleAtRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        rootObject.set("poId", null);
        cs.beginLogging();
        rootObject.unset("poId");

        assertCSonRootUnsetSimpleSingleAtRootAfterSetNull(false);
    }
}
