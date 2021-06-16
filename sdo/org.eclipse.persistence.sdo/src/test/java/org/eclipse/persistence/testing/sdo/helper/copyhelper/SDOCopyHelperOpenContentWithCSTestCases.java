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
package org.eclipse.persistence.testing.sdo.helper.copyhelper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDOCopyHelperOpenContentWithCSTestCases extends SDOCopyHelperOpenContentTestCases {
    private Property openPropString;
    private Property openPropStringMany;
    private DataObject customerObject;

    public SDOCopyHelperOpenContentWithCSTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        customerObject = rootObject.getDataObject("customer");
        DataObject newProperty = dataFactory.create(propertyType);
        SDOProperty nameProp = (SDOProperty)newProperty.getType().getProperty("name");
        newProperty.set(nameProp, "openPropString");
        newProperty.set("type", SDOConstants.SDO_STRING);
        openPropString = typeHelper.defineOpenContentProperty("my.uri", newProperty);

        DataObject newProperty2 = dataFactory.create(propertyType);

        newProperty2.set(nameProp, "openPropStringMany");

        newProperty2.set("type", SDOConstants.SDO_STRING);
        newProperty2.set("many", true);
        openPropStringMany = typeHelper.defineOpenContentProperty("my.uri", newProperty2);

    }

    public static void main(String[] args) {
        TestRunner.run(SDOCopyHelperOpenContentWithCSTestCases.class);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/CustomerOpenContentWithCS.xsd";
    }

    protected String getXMLFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementOpenContentWithCS.xml";
    }

    public void testCopyOpenContentInCS() throws Exception {
        rootObject.getChangeSummary().beginLogging();
        customerObject.set(openPropString, "theFirstString");
        customerObject.set(openPropString, "theSecondString");

        commonj.sdo.ChangeSummary.Setting setting = rootObject.getChangeSummary().getOldValue(customerObject, openPropString);
        assertNotNull(setting);
        assertFalse(setting.isSet());
        assertEquals(null, setting.getValue());

        //DEEP COPY TEST
        DataObject deepCopy = copyHelper.copy(rootObject);
        compareOpenContentProperties(deepCopy, false);
        assertTrue(equalityHelper.equal(rootObject, deepCopy));

        commonj.sdo.ChangeSummary.Setting deepCopySetting = deepCopy.getChangeSummary().getOldValue(deepCopy.getDataObject("customer"), openPropString);
        assertNotNull(deepCopySetting);
        assertFalse(deepCopySetting.isSet());
        assertEquals(null, deepCopySetting.getValue());

        //SHALLOW COPY TEST
        DataObject shallowCopy = copyHelper.copyShallow(rootObject);
        compareOpenContentProperties(shallowCopy, false);
        assertTrue(equalityHelper.equalShallow(rootObject, shallowCopy));

        assertEquals(0, shallowCopy.getChangeSummary().getChangedDataObjects().size());
        assertEquals(0, shallowCopy.getChangeSummary().getOldValues(shallowCopy.getDataObject("customer")).size());
    }

    public void testCopyOpenContentInCS2() throws Exception {
        customerObject.set(openPropString, "theFirstString");
        rootObject.getChangeSummary().beginLogging();
        customerObject.set(openPropString, "theSecondString");

        commonj.sdo.ChangeSummary.Setting setting = rootObject.getChangeSummary().getOldValue(customerObject, openPropString);
        assertNotNull(setting);
        assertTrue(setting.isSet());
        assertEquals("theFirstString", setting.getValue());

        //DEEP COPY TEST
        DataObject deepCopy = copyHelper.copy(rootObject);
        compareOpenContentProperties(deepCopy, false);
        assertTrue(equalityHelper.equal(rootObject, deepCopy));

        commonj.sdo.ChangeSummary.Setting deepCopySetting = deepCopy.getChangeSummary().getOldValue(deepCopy.getDataObject("customer"), openPropString);
        assertNotNull(deepCopySetting);
        assertTrue(deepCopySetting.isSet());
        assertEquals("theFirstString", deepCopySetting.getValue());

        //SHALLOW COPY TEST
        DataObject shallowCopy = copyHelper.copyShallow(rootObject);
        compareOpenContentProperties(shallowCopy, false);
        assertTrue(equalityHelper.equalShallow(rootObject, shallowCopy));

        assertEquals(0, shallowCopy.getChangeSummary().getChangedDataObjects().size());
        assertEquals(0, shallowCopy.getChangeSummary().getOldValues(shallowCopy.getDataObject("customer")).size());

    }

    public void testCopyOpenContentUnsetInCS() throws Exception {
        customerObject.set(openPropString, "theFirstString");
        rootObject.getChangeSummary().beginLogging();
        customerObject.unset(openPropString);

        commonj.sdo.ChangeSummary.Setting setting = rootObject.getChangeSummary().getOldValue(customerObject, openPropString);
        assertNotNull(setting);
        assertTrue(setting.isSet());
        assertEquals("theFirstString", setting.getValue());

        //DEEP COPY TEST
        DataObject deepCopy = copyHelper.copy(rootObject);
        compareOpenContentProperties(deepCopy, false);
        assertTrue(equalityHelper.equal(rootObject, deepCopy));
        assertFalse(deepCopy.isSet(openPropString));

        commonj.sdo.ChangeSummary.Setting deepCopySetting = deepCopy.getChangeSummary().getOldValue(deepCopy.getDataObject("customer"), openPropString);
        assertNotNull(deepCopySetting);
        assertTrue(deepCopySetting.isSet());
        assertEquals("theFirstString", deepCopySetting.getValue());

        //SHALLOW COPY TEST
        DataObject shallowCopy = copyHelper.copyShallow(rootObject);
        compareOpenContentProperties(shallowCopy, false);
        assertTrue(equalityHelper.equalShallow(rootObject, shallowCopy));
        assertFalse(shallowCopy.isSet(openPropString));

        assertEquals(0, shallowCopy.getChangeSummary().getChangedDataObjects().size());
        assertEquals(0, shallowCopy.getChangeSummary().getOldValues(shallowCopy.getDataObject("customer")).size());
    }
}
