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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class PolymorphicPropertiesJira32TestCases extends SDOTestCase {
    public PolymorphicPropertiesJira32TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.PolymorphicPropertiesJira32TestCases" };
        TestRunner.main(arguments);
    }

    public void testPolymorphicProperty() {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        DataObject addressTypeDO = dataFactory.create(typeType);
        addressTypeDO.set("name", "addressType");
        addProperty(addressTypeDO, "street", SDOConstants.SDO_STRING, false, false, true);
        addProperty(addressTypeDO, "city", SDOConstants.SDO_STRING, false, false, true);
        Type addressType = typeHelper.define(addressTypeDO);

        DataObject cdnAddressTypeDO = dataFactory.create(typeType);
        cdnAddressTypeDO.set("name", "cdnAddressType");
        List baseTypes = new ArrayList();
        baseTypes.add(addressType);
        cdnAddressTypeDO.set("baseType", baseTypes);
        addProperty(cdnAddressTypeDO, "postalCode", SDOConstants.SDO_STRING, false, false, true);
        Type cdnAddressType = typeHelper.define(cdnAddressTypeDO);

        DataObject personTypeDO = dataFactory.create(typeType);
        personTypeDO.set("name", "personType");
        addProperty(personTypeDO, "name", SDOConstants.SDO_STRING, false, false, true);
        addProperty(personTypeDO, "address", addressType, true, false, true);
        Type personType = typeHelper.define(personTypeDO);

        DataObject addressDO = dataFactory.create(addressType);
        addressDO.set("street", "theStreet");
        addressDO.set("city", "theCity");

        DataObject personDO = dataFactory.create(personType);
        personDO.set("name", "theName");
        personDO.set("address", addressDO);

        DataObject value = personDO.getDataObject("address");
        assertEquals("addressType", value.getType().getName());

        DataObject cdnAddressDO = dataFactory.create(cdnAddressType);
        cdnAddressDO.set("street", "theStreet2");
        cdnAddressDO.set("city", "theCity2");
        cdnAddressDO.set("postalCode", "thePostalCode");
        personDO.set("address", cdnAddressDO);

        value = personDO.getDataObject("address");
        assertEquals("cdnAddressType", value.getType().getName());

    }
}
