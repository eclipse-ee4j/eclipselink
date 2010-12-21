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

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class ChangeSummaryCreatedModifiedDeletedTestCase extends SDOTestCase {
    protected static final String URINAME = "root_uri";
    protected static final String TYPENAME = "rootTypeName";
    protected static final String PROPERTY_NAME = "PName";
    protected static final String CONTAINED_TYPENAME = "ContainedTypeName";
    protected static final String CONTAINED_PROPERTYNAME = "ContainedPName";
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
    protected SDODataObject dataObjectD;
    protected SDOType type_D;
    protected SDOProperty propertyD;
    protected SDODataObject dataObjectE;
    protected SDOType type_E;
    protected SDOProperty propertyE;
    protected SDODataObject root1;
    protected SDOType type_root1;
    protected SDOProperty p_root1;
    protected SDODataObject dataObjectF;
    protected SDOType type_F;
    protected SDOProperty p_F;

    public ChangeSummaryCreatedModifiedDeletedTestCase(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp(); // watch setup redundancy
        DataObject rootTypeDO = defineType(URINAME, TYPENAME);
        rootType = (SDOType)typeHelper.define(rootTypeDO);

        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        rootContainingPropertyB = setUpProperty("propertyA-B", true, dataObjectType, rootType);
        rootContainingPropertyC = setUpProperty("propertyA-C", true, dataObjectType, rootType);
        rootChangeSummaryProperty = setUpProperty("changeSummaryA", true, changeSummaryType, null);

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
    protected void buildTree() {
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        DataObject type_BDO = defineType("B_uri", "B");
        type_B = (SDOType)typeHelper.define(type_BDO);

        propertyB = setUpProperty("propertyB", true, dataObjectType, type_B);
        changeSummaryPropertyB = setUpProperty("changeSummaryB", false, changeSummaryType, type_B);

        DataObject type_CDO = defineType("C_uri", "C");
        type_C = (SDOType)typeHelper.define(type_CDO);

        propertyC = setUpProperty("propertyC", true, dataObjectType, type_C);
        changeSummaryPropertyC = setUpProperty("changeSummaryC", false, changeSummaryType, type_C);

        DataObject type_DDO = defineType("D_uri", "D");
        type_D = (SDOType)typeHelper.define(type_DDO);

        propertyD = setUpProperty("propertyD", false, SDOConstants.SDO_STRING, type_D);

        root = (SDODataObject)dataFactory.create(rootType);
        dataObjectB = (SDODataObject)root.createDataObject(rootContainingPropertyB, type_B);
        dataObjectC = (SDODataObject)root.createDataObject(rootContainingPropertyC, type_C);
        dataObjectD = (SDODataObject)dataObjectB.createDataObject(propertyB, type_D);
    }

    protected void checkOldSettingsSizeTree(String values, SDOChangeSummary aCS,// 
                                            SDODataObject aDO1, SDODataObject aDO2, SDODataObject aDO3, SDODataObject aDO4) {
        assertEquals(Integer.parseInt(values.substring(0, 1)), aCS.getOldValues(aDO1).size());
        assertEquals(Integer.parseInt(values.substring(1, 2)), aCS.getOldValues(aDO2).size());
        assertEquals(Integer.parseInt(values.substring(2, 3)), aCS.getOldValues(aDO3).size());
        assertEquals(Integer.parseInt(values.substring(3, 4)), aCS.getOldValues(aDO4).size());
    }

    protected void checkOldContainer(SDOChangeSummary aCS,//
                                     SDODataObject aDO1, Object anObject1,//
                                     SDODataObject aDO2, Object anObject2,//
                                     SDODataObject aDO3, Object anObject3,//
                                     SDODataObject aDO4, Object anObject4) {
        assertEquals(anObject1, (SDODataObject)aCS.getOldContainer(aDO1));
        assertEquals(anObject2, (SDODataObject)aCS.getOldContainer(aDO2));
        assertEquals(anObject3, (SDODataObject)aCS.getOldContainer(aDO3));
        assertEquals(anObject4, (SDODataObject)aCS.getOldContainer(aDO4));
    }

    /*
     * root
     *   -> B
     *        -> CS-B
     *        -> D
     *             -> E
     *                  -> string
     *   -> C
     *        -> CS-C
     */
    protected void buildTreeWith4LevelsOfProperties() {
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        DataObject type_BDO = defineType("B_uri", "B");
        type_B = (SDOType)typeHelper.define(type_BDO);

        propertyB = setUpProperty("propertyB", true, dataObjectType, type_B);
        changeSummaryPropertyB = setUpProperty("changeSummaryB", false, changeSummaryType, type_B);

        DataObject type_CDO = defineType("C_uri", "C");
        type_C = (SDOType)typeHelper.define(type_CDO);

        propertyC = setUpProperty("propertyC", true, dataObjectType, type_C);
        changeSummaryPropertyC = setUpProperty("changeSummaryC", false, changeSummaryType, type_C);

        DataObject type_DDO = defineType("D_uri", "D");
        type_D = (SDOType)typeHelper.define(type_DDO);

        propertyD = setUpProperty("propertyD", true, dataObjectType, type_D);

        DataObject type_EDO = defineType("E_uri", "E");
        type_E = (SDOType)typeHelper.define(type_EDO);
        propertyE = setUpProperty("propertyE", false, SDOConstants.SDO_STRING, type_E);

        root = (SDODataObject)dataFactory.create(rootType);
        dataObjectB = (SDODataObject)root.createDataObject(rootContainingPropertyB, type_B);
        dataObjectC = (SDODataObject)root.createDataObject(rootContainingPropertyC, type_C);
        dataObjectD = (SDODataObject)dataObjectB.createDataObject(propertyB, type_D);
        dataObjectE = (SDODataObject)dataObjectD.createDataObject(propertyD, type_E);

    }

    protected void checkOldSettingsSizeTree(String values, SDOChangeSummary aCS,// 
                                            SDODataObject aDO1, SDODataObject aDO2, SDODataObject aDO3, SDODataObject aDO4, SDODataObject aDO5) {
        checkOldSettingsSizeTree(values, aCS, aDO1, aDO2, aDO3, aDO4);
        assertEquals(Integer.parseInt(values.substring(5, 6)), aCS.getOldValues(aDO5).size());
    }

    protected void checkOldContainer(SDOChangeSummary aCS,//
                                     SDODataObject aDO1, Object anObject1,//
                                     SDODataObject aDO2, Object anObject2,//
                                     SDODataObject aDO3, Object anObject3,//
                                     SDODataObject aDO4, Object anObject4,//
                                     SDODataObject aDO5, Object anObject5) {
        checkOldContainer(aCS,//
                          aDO1, anObject1,//
                          aDO2, anObject2,//
                          aDO3, anObject3,//
                          aDO4, anObject4);
        assertEquals(anObject5, (SDODataObject)aCS.getOldContainer(aDO5));
    }

    protected void buildTreeWithoutChildChangeSummaries() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        DataObject type_BDO = defineType("B_uri", "B");
        type_B = (SDOType)typeHelper.define(type_BDO);

        propertyB = setUpProperty("propertyB", true, dataObjectType, type_B);

        DataObject type_CDO = defineType("C_uri", "C");
        type_C = (SDOType)typeHelper.define(type_CDO);
        propertyC = setUpProperty("propertyC", true, dataObjectType, type_C);

        DataObject type_DDO = defineType("D_uri", "D");
        type_D = (SDOType)typeHelper.define(type_DDO);
        propertyD = setUpProperty("propertyD", false, SDOConstants.SDO_STRING, type_D);

        dataObjectB = (SDODataObject)root.createDataObject(rootContainingPropertyB, type_B);
        dataObjectC = (SDODataObject)root.createDataObject(rootContainingPropertyC, type_C);
        dataObjectD = (SDODataObject)dataObjectB.createDataObject(propertyB, type_D);
    }

    protected void buildTreeWithoutChangeSummary() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        DataObject type_root1DO = defineType("root1_uri", "root1");
        type_root1 = (SDOType)typeHelper.define(type_root1DO);

        p_root1 = setUpProperty("p_root1", true, dataObjectType, type_root1);
        root1 = (SDODataObject)dataFactory.create(type_root1);

        DataObject type_FDO = defineType("F_uri", "F");
        type_F = (SDOType)typeHelper.define(type_FDO);

        p_F = setUpProperty("p_F", false, SDOConstants.SDO_STRING, type_F);
        dataObjectF = (SDODataObject)root1.createDataObject(p_root1, type_F);
        dataObjectF.set(p_F, "test");
    }
}
