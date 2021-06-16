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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.InputStream;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

public class ClashingNamespacesTestCases extends XSDHelperTestCases {
    public ClashingNamespacesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(ClashingNamespacesTestCases.class);
    }

    public void testDefineTwoXsdsWithPrefixClash() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/namespaces/clashingUri1.xsd");
        xsdHelper.define(is, null);

        InputStream is2 = getSchemaInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/namespaces/clashingUri2.xsd");
        xsdHelper.define(is2, null);

        SDOType empType = (SDOType)typeHelper.getType("uri1", "EmployeeType");
        assertNotNull(empType);
        assertEquals("ns0", empType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri1"));
        assertNull(empType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri2"));

        SDOType addressType = (SDOType)typeHelper.getType("uri2", "AddressType");
        assertEquals("ns1", addressType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri2"));
        assertNull(addressType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri1"));
        assertNotNull(addressType);
    }

    public void testDefineTwoXsdsWithSameUriDiffPrefix() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/namespaces/sameUriDiffPrefix1.xsd");
        xsdHelper.define(is, null);

        InputStream is2 = getSchemaInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/namespaces/sameUriDiffPrefix2.xsd");
        xsdHelper.define(is2, null);

        SDOType empType = (SDOType)typeHelper.getType("uri1", "EmployeeType");
        assertNotNull(empType);
        assertEquals("aaa", empType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri1"));
        assertNull(empType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri2"));
        assertEquals("uri1", empType.getXmlDescriptor().getNamespaceResolver().resolveNamespacePrefix("aaa"));

        SDOType addressType = (SDOType)typeHelper.getType("uri1", "AddressType");
        assertEquals("aaa", addressType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri1"));
        assertNull(addressType.getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI("uri2"));
        assertNull(addressType.getXmlDescriptor().getNamespaceResolver().resolveNamespacePrefix("ns0"));
        assertNotNull(addressType);
    }
}
