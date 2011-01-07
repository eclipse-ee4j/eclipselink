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

import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryXSDWithCSonRootDetachComplexSingleAtRootTest extends ChangeSummaryOnRootTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PurchaseOrderDeepWithCSonRootDetachComplexSingleAtRoot.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootDetachComplexSingleAtRootTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootDetachComplexSingleAtRootTest" };
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
        DataObject shipToDO = null;//rootObject.getDataObject("shipTo");
        DataObject yardDO = null;//shipToDO.getDataObject("yard");
        Property containmentProp = null;//shipToDO.getContainmentProperty();

        //List phoneList = shipToDO.getList("phone");
        DataObject phone1 = null;//(DataObject)phoneList.get(0);
        DataObject phone2 = null;//(DataObject)phoneList.get(1);

        verifyCSonRootDetachUnsetComplexSingleAtRoot(true, shipToDO, containmentProp, yardDO, phone1, phone2);
    }

    public void testDetachComplexSingleBelowRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        Property containmentProp = shipToDO.getContainmentProperty();
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        cs.beginLogging();
        shipToDO.detach();

        verifyCSonRootDetachUnsetComplexSingleAtRoot(false, shipToDO, containmentProp, yardDO, phone1, phone2);
    }
}
