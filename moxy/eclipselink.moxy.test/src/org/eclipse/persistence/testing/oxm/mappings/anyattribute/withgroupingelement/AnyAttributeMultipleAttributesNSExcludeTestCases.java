/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement;

import java.util.HashMap;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeWithoutGroupingElementNSProject;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;

public class AnyAttributeMultipleAttributesNSExcludeTestCases extends XMLMappingTestCases {
    public AnyAttributeMultipleAttributesNSExcludeTestCases(String name) throws Exception {
        super(name);
        Project p = new AnyAttributeWithGroupingElementNSProject();
        XMLAnyAttributeMapping mapping = (XMLAnyAttributeMapping)p.getDescriptor(Root.class).getMappingForAttributeName("any");
        mapping.setSchemaInstanceIncluded(false);
        mapping.setNamespaceDeclarationIncluded(false);
        setProject(p);

        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withgroupingelement/multiple_attributes_ns.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/some-dir/some.xsd", "first-name");
        any.put(name, "Matt");
        name = new QName("www.example.com/some-dir/some.xsd", "last-name");
        any.put(name, "MacIvor");
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
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeMultipleAttributesNSExcludeTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
