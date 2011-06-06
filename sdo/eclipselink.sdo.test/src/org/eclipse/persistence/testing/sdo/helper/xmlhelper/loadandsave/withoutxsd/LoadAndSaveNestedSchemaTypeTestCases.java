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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;

public class LoadAndSaveNestedSchemaTypeTestCases extends LoadAndSaveUnknownTestCases {
    public LoadAndSaveNestedSchemaTypeTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd.LoadAndSaveNestedSchemaTypeTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/nestedSchemaTypes.xml");
    }
   
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/PurchaseOrder.xsd";
    }

    protected void verifyAfterLoad(XMLDocument document) {
        assertNull(document.getRootObject().getContainer());
        DataObject root = document.getRootObject();
        assertNotNull(root);

        List someUnknownThingDOList = root.getList("someUnknownThing");
        assertNotNull(someUnknownThingDOList);
        assertEquals(1, someUnknownThingDOList.size());

        DataObject someUnknownThingDO = (DataObject)someUnknownThingDOList.get(0);
        assertNotNull(someUnknownThingDO);

        List someOtherUnknownThingDOList = someUnknownThingDO.getList("someOtherUnknownThing");
        assertNotNull(someOtherUnknownThingDOList);
        assertEquals(1, someOtherUnknownThingDOList.size());

        Object value = someOtherUnknownThingDOList.get(0);
        Property prop = someUnknownThingDO.getInstanceProperty("someOtherUnknownThing");
        assertNotNull(prop);
        assertEquals(SDOConstants.SDO_INT, prop.getType());
        assertTrue(value instanceof Integer);
        assertEquals(new Integer(10), ((Integer)value));

    }
}
