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

import commonj.sdo.DataObject;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class ChangeSummaryCopyTestCases extends SDOTestCase {
    public ChangeSummaryCopyTestCases(String name) {
        super(name);
    }

    protected static final String URINAME = "root_uri";
    protected static final String TYPENAME = "rootTypeName";
    private SDOType rootType;
    private SDOType type_B;
    private SDOType type_D;
    private SDOType type_E;
    private DataObject root;
    private DataObject dataObjectB;
    private DataObject dataObjectD;
    private DataObject dataObjectE;

    public void setUp() {
        super.setUp();
        DataObject rootTypeDO = defineType(URINAME, TYPENAME);
        rootType = (SDOType)typeHelper.define(rootTypeDO);
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        SDOProperty rootContainingPropertyB = setUpProperty("propertyA-B", true, dataObjectType, rootType);

        DataObject type_BDO = defineType("B_uri", "B");
        type_B = (SDOType)typeHelper.define(type_BDO);

        SDOProperty propertyB = setUpProperty("propertyB", true, dataObjectType, type_B);
        setUpProperty("changeSummaryB", false, changeSummaryType, type_B);

        DataObject type_DDO = defineType("D_uri", "D");
        type_D = (SDOType)typeHelper.define(type_DDO);

        SDOProperty propertyD = setUpProperty("propertyD", true, dataObjectType, type_D);

        //propertyD = setUpProperty("propertyD", false, SDOConstants.SDO_STRING, type_D);
        DataObject type_EDO = defineType("E_uri", "E");
        type_E = (SDOType)typeHelper.define(type_EDO);

        // String types are containment == false
        SDOProperty propertyE = setUpProperty("propertyE", false, SDOConstants.SDO_STRING, type_E);

        root = (SDODataObject)dataFactory.create(rootType);
        dataObjectB = (SDODataObject)root.createDataObject(rootContainingPropertyB, type_B);
        dataObjectD = (SDODataObject)dataObjectB.createDataObject(propertyB, type_D);
        dataObjectE = (SDODataObject)dataObjectD.createDataObject(propertyD, type_E);
        dataObjectB.getChangeSummary().beginLogging();

    }

    public void tearDown() {
        dataObjectB.getChangeSummary().beginLogging();
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryCopyTestCases" };
        TestRunner.main(arguments);
    }

    public void testCopyChildSecondLevelNoPropsSet() {
        copyHelper.copy(dataObjectE);
        int changedSize = dataObjectB.getChangeSummary().getChangedDataObjects().size();
        assertEquals(0, changedSize);
    }

    public void testCopyChildSecondLevel() {
        dataObjectB.getChangeSummary().endLogging();
        dataObjectE.set("propertyE", "propEStringValue");
        // #5878436 12-FEB-07 do not recurse into a non-containment relationships or simple containment ones
        dataObjectB.getChangeSummary().beginLogging();
        copyHelper.copy(dataObjectE);
        int changedSize = dataObjectB.getChangeSummary().getChangedDataObjects().size();
        assertEquals(0, changedSize);
    }

    public void testCopyChildFirstLevelNoPropsSet() {
        dataObjectB.getChangeSummary().endLogging();
        dataObjectD.unset("propertyD");
        dataObjectB.getChangeSummary().beginLogging();
        copyHelper.copy(dataObjectD);
        int changedSize = dataObjectB.getChangeSummary().getChangedDataObjects().size();
        assertEquals(0, changedSize);
    }

    public void testCopyChildFirstLevel() {
        copyHelper.copy(dataObjectD);
        int changedSize = dataObjectB.getChangeSummary().getChangedDataObjects().size();
        assertEquals(0, changedSize);
    }

    public void testCopyCSOwner() {
        copyHelper.copy(dataObjectB);
        int changedSize = dataObjectB.getChangeSummary().getChangedDataObjects().size();
        assertEquals(0, changedSize);
    }

    public void testCopyRoot() {
        copyHelper.copy(root);
        int changedSize = dataObjectB.getChangeSummary().getChangedDataObjects().size();
        assertEquals(0, changedSize);
    }

    protected SDOProperty setUpProperty(String name, boolean containment, SDOType propertyType, SDOType owner) {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(name);
        property.setContainment(containment);
        property.setType(propertyType);
        if (owner != null) {
            owner.addDeclaredProperty(property);
        }
        return property;
    }
}
