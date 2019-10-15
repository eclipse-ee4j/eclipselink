/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

/**
 * There are 2 types of tests model and XML
 * both use the same assertion sets and 2 different CS-on-root and CS-on-child XSD schemas.
 * We will not duplicate the full loadandsave tests here, just toString versions
 *
 *  Model:
 *  1. read in model from a populated XML file - via setup()
 *  2. rootObject will be in pre-operation state
 *  3. get references to the original object tree - before operation is performed
 *  4. [perform operation]
 *  -
 *  5. compare new modified model with original objects - in memory
 *  6.
 *
 *
 *  XML:
 *  1. read in modified model from a modified XML file with CS pre-populated - [perform operation]
 *  2. rootObject will be in post-operation state
 *  3. partially compare new modified model with ([original] objects by doing an effective undo or using oldValues) - in memory
 *  4. save model to XML
 *  5. compare generated XML with file system XML
 *
 *  Combined:
 *
 */
public class ChangeSummaryXSDWithCSonRootDetachComplexSingleBelowRootTest extends ChangeSummaryOnRootTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PurchaseOrderDeepWithCSonRootDetachComplexSingleBelowRoot.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootDetachComplexSingleBelowRootTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootDetachComplexSingleBelowRootTest" };
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
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = null;//shipToDO.getDataObject("yard");

        assertDeleteDetachUnsetComplexSingleBelowRoot(true,//
                                                      false,//
                                                      shipToDO,//
                                                      yardDO);
    }

    public void testDetachComplexManySingleBelowRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        cs.beginLogging();
        yardDO.detach();

        assertDeleteDetachUnsetComplexSingleBelowRoot(false,//
                                                      false,//
                                                      shipToDO,//
                                                      yardDO);
    }
}
