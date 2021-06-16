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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement;

import java.util.HashMap;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;

public class AnyAttributeMultipleAttributesNSTestCases extends XMLMappingTestCases {
    public AnyAttributeMultipleAttributesNSTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeWithGroupingElementNSProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withgroupingelement/multiple_attributes_ns.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/some-dir/some.xsd", "first-name");
        any.put(name, "Matt");
        name = new QName("www.example.com/some-dir/some.xsd", "last-name");
        any.put(name, "MacIvor");

        name = new QName(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "ns0");
        any.put(name, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

        name = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
        any.put(name, "blah");

        root.setAny(any);
        return root;
    }

    public Object getWriteControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/some-dir/some.xsd", "first-name");
        any.put(name, "Matt");
        name = new QName("www.example.com/some-dir/some.xsd", "last-name");
        any.put(name, "MacIvor");

        name = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
        any.put(name, "blah");

        root.setAny(any);
        return root;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeMultipleAttributesNSTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
