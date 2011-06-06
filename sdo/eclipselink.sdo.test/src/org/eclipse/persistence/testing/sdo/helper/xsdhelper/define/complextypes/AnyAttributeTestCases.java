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
*     bdoughan - February 3/2010 - 2.0.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.complextypes;

import commonj.sdo.Type;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

public class AnyAttributeTestCases extends XSDHelperTestCases {

    public AnyAttributeTestCases(String name) {
        super(name);
    }

    public void testChildIsOpen() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/AnyAttribute.xsd");
        xsdHelper.define(xsdSchema);

        Type baseType = typeHelper.getType("urn:any-attribute", "base");
        assertTrue(baseType.isOpen());

        Type extendedChildType = typeHelper.getType("urn:any-attribute", "extended-child");
        assertTrue(extendedChildType.isOpen());

        Type restrictedChildType = typeHelper.getType("urn:any-attribute", "restricted-child");
        assertTrue(restrictedChildType.isOpen());
    }

}