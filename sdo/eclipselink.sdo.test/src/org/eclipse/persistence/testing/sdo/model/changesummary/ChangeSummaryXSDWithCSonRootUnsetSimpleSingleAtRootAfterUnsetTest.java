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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.textui.TestRunner;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterUnsetTest extends ChangeSummaryOnRootTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PurchaseOrderDeepWithCSonRootUnsetSimpleSingleAtRootAfterUnset.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterUnsetTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterUnsetTest" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        // replace global object with one from xml file (with cs pre-populated)
        rootObject = document.getRootObject();
        cs = rootObject.getChangeSummary();

        assertCSonRootUnsetSimpleSingleAtRootAfterUnset(true);
    }

    public void testUnsetSimpleSingleAtRoot() {
        defineTypes();  
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        rootObject.unset("poId");
        cs.beginLogging();
        rootObject.unset("poId");

        assertCSonRootUnsetSimpleSingleAtRootAfterUnset(false);
    }
}
