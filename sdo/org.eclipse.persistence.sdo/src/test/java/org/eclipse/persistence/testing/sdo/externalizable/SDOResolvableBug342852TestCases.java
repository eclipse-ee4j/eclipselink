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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.sdo.externalizable;

import java.util.List;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class SDOResolvableBug342852TestCases extends SDOResolvableTestCases {

    public final String SERIALIZATION_FILE_NAME = tempFileDir + "/serialization.bin";

    public SDOResolvableBug342852TestCases(String name) {
        super(name);
    }

    public void testSerializeWithChangeSummary(){
         String xsd = "org/eclipse/persistence/testing/sdo/externalizable/POrder.xsd";

         // Define Types so that processing attributes completes
         List types = xsdHelper.define(getSchema(xsd));
         serialize(getControlObject(), SERIALIZATION_FILE_NAME);
    }

     private DataObject getControlObject(){

         Type addressType = aHelperContext.getTypeHelper().getType("http://www.example.org", "AddressType");

         DataObject addrDataObject = aHelperContext.getDataFactory().create(addressType);
         addrDataObject.set("street", "myStreet2");

         Type porderType = aHelperContext.getTypeHelper().getType("http://www.example.org", "PurchaseOrderType");
         DataObject theDataObject = aHelperContext.getDataFactory().create(porderType);
         theDataObject.set("comment", "That is all.");

         theDataObject.set("billTo", addrDataObject);

         return theDataObject;
     }

}
