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
/*
   DESCRIPTION
    Move a deep tree between change summaries and back - observe original state

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

   MODIFIED    (MM/DD/YY)
    dmahar      04/23/07 -
    mfobrien    12/22/06 -
    dmahar      11/23/06 -
    mfobrien    07/09/06 - Creation
 */
/**
 *  @version $Header: ChangeSummaryVolumetricsTest.java 23-apr-2007.14:43:19 dmahar Exp $
 *  @author  mfobrien
 *  @since   release specific (what release of product did this appear in)
 */
package org.eclipse.persistence.testing.sdo.model.changesummary;

import commonj.sdo.DataObject;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class ChangeSummaryVolumetricsTest extends ChangeSummaryTestCases {
    public ChangeSummaryVolumetricsTest(String name) {
        super(name);
    }

    protected static final String CONTAINED_TYPENAME = "ContainedTypeName";
    protected static final String CONTAINED_PROPERTYNAME = "ContainedPName";
    protected static final int NUM_B_LEVELS = 100;
    protected SDODataObject root;
    protected SDOType rootType;
    protected SDOChangeSummary changeSummaryA;
    protected SDOProperty rootContainingPropertyB;
    protected SDOProperty rootContainingPropertyC;
    protected SDOProperty rootChangeSummaryProperty;
    protected SDODataObject dataObjectB;
    protected SDOType type_B;
    protected SDOProperty propertyB;
    protected SDOChangeSummary changeSummaryB;
    protected SDOProperty changeSummaryPropertyB;
    protected SDODataObject dataObjectC;
    protected SDOType type_C;
    protected SDOProperty propertyC;
    protected SDOChangeSummary changeSummaryC;
    protected SDOProperty changeSummaryPropertyC;
    protected SDODataObject[] dataObjectB_children;
    protected SDOType[] typeB_children;
    protected SDOProperty[] propertyB_children;

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryVolumetricsTest" };
        TestRunner.main(arguments);
    }

    // perform a move of a cs root (delete CCB) to another cs as child  and observe the old* instance variables
    public void testOldSettingsAfterMoveCSDeepDataObjectFromOneOwnerToAnotherDiffChangeSummary() {
        buildDeepTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        DataObject dataObjectD = dataObjectB.getDataObject("propertyB");

        // move
        for (int x = 0; x < 1000; x++) {
            dataObjectD.detach();
            dataObjectC.set(propertyC, dataObjectD);
            dataObjectD.detach();
            // verify that containers are set after (re)set
            int depth = depth(dataObjectB_children[99]);
            assertEquals(NUM_B_LEVELS - 1, depth);
        }
    }

    public void setUp() {
        super.setUp();
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        DataObject rootTypeDO = defineType(URINAME, TYPENAME);
        rootType = (SDOType)typeHelper.define(rootTypeDO);

        rootContainingPropertyB = setUpProperty("propertyA-B", true, dataObjectType, rootType);
        rootContainingPropertyC = setUpProperty("propertyA-C", true, dataObjectType, rootType);
        rootChangeSummaryProperty = setUpProperty("changeSummaryA", false, changeSummaryType, null);
        root = (SDODataObject)dataFactory.create(rootType);

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

    /*
     * root
     *   -> B
     *        -> CS-B
     *        -> D (String)
     *   -> C
     *        -> CS-C
     */
    protected void buildDeepTree() {
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        DataObject type_BDO = defineType("B_uri", "B");

        DataObject propertyBDO = addProperty(type_BDO, "propertyB", dataObjectType);
        propertyBDO.set("containment", true);
        type_B = (SDOType)typeHelper.define(type_BDO);
        propertyB = (SDOProperty)type_B.getDeclaredPropertiesMap().get("propertyB");
        changeSummaryPropertyB = setUpProperty("changeSummaryB", false, changeSummaryType, type_B);
        DataObject type_CDO = defineType("C_uri", "C");
        DataObject propertyCDO = addProperty(type_CDO, "propertyC", dataObjectType);
        propertyBDO.set("containment", true);
        type_C = (SDOType)typeHelper.define(type_CDO);
        propertyC = (SDOProperty)type_C.getDeclaredPropertiesMap().get("propertyC");
        changeSummaryPropertyC = setUpProperty("changeSummaryC", false, changeSummaryType, type_C);

        typeB_children = new SDOType[NUM_B_LEVELS];
        propertyB_children = new SDOProperty[NUM_B_LEVELS];
        dataObjectB_children = new SDODataObject[NUM_B_LEVELS];

        dataObjectB = (SDODataObject)root.createDataObject(rootContainingPropertyB, type_B);
        dataObjectC = (SDODataObject)root.createDataObject(rootContainingPropertyC, type_C);

        // setup object array
        for (int i = 0; i < NUM_B_LEVELS; i++) {
            DataObject typeB_childreniDO = defineType("D_uri" + String.valueOf(i), "D" + String.valueOf(i));

            DataObject propDo = addProperty(typeB_childreniDO, "propertyD" + String.valueOf(i), dataObjectType);
            propDo.set("containment", true);
            typeB_children[i] = (SDOType)typeHelper.define(typeB_childreniDO);
            propertyB_children[i] = (SDOProperty)typeB_children[i].getDeclaredPropertiesMap().get("propertyD" + String.valueOf(i));
        }

        dataObjectB_children[0] = (SDODataObject)dataObjectB.createDataObject(propertyB, typeB_children[0]);

        for (int i = 1; i < NUM_B_LEVELS; i++) {
            dataObjectB_children[i] = (SDODataObject)dataObjectB_children[i - 1].createDataObject(propertyB_children[i - 1], typeB_children[i]);
        }

        //dataObjectD = (SDODataObject)dataObjectB.createDataObject(propertyB, type_D);
    }
}
