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
//     bdoughan - February 3/2010 - 2.0.1 - Initial implementation
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
