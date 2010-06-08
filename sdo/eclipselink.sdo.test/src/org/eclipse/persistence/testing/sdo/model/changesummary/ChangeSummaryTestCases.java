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

import java.util.Iterator;
import java.util.List;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class ChangeSummaryTestCases extends SDOTestCase {
    protected static final String URINAME = "rooturi";
    protected static final String TYPENAME = "rootTypeName";
    protected static final String PROPERTY_NAME = "PName";
    protected SDODataObject root;
    protected SDOType rootType;
    protected SDOProperty rootProperty;
    protected SDOProperty rootProperty1;
    protected SDODataObject containedDataObject;
    protected SDOType contained_type;
    protected SDOChangeSummary changeSummary;

    public ChangeSummaryTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();

        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);

        DataObject rootPropertyTypeDO = defineType("rootPropertyTypeUri", "rootPropertyType");
        SDOType rootPropertyType = (SDOType)typeHelper.define(rootPropertyTypeDO);

        DataObject rootTypeDO = defineType(URINAME, TYPENAME);
        DataObject changeSumPropertyDO = addProperty(rootTypeDO, "csmProp", changeSummaryType);
        changeSumPropertyDO.set("containment", true);
        DataObject rootPropertyDO = addProperty(rootTypeDO, "property-Containment", rootPropertyType);
        rootPropertyDO.set("containment", true);

        DataObject rootProperty1DO = addProperty(rootTypeDO, "property1-Containment", SDOConstants.SDO_STRING);
        rootType = (SDOType)typeHelper.define(rootTypeDO);

        rootProperty = (SDOProperty)rootType.getDeclaredPropertiesMap().get("property-Containment");
        rootProperty1 = (SDOProperty)rootType.getDeclaredPropertiesMap().get("property1-Containment");
        rootProperty1.setNullable(true);

        DataObject contained_typeDO = defineType("containedUri", "containedType");

        DataObject containedPropertyDO = addProperty(contained_typeDO, "containedProperty", SDOConstants.SDO_STRING);        

        contained_type = (SDOType)typeHelper.define(contained_typeDO);

        containedDataObject = (SDODataObject)dataFactory.create(contained_type);

        root = (SDODataObject)dataFactory.create(rootType);
        root.set(rootProperty, containedDataObject);
        changeSummary = (SDOChangeSummary)root.getChangeSummary();
    }

}
