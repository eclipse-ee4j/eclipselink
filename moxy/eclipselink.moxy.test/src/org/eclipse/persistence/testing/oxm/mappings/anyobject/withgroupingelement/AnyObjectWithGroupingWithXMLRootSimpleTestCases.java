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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement;

import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyObjectWithGroupingWithXMLRootSimpleTestCases extends XMLMappingTestCases {
    public AnyObjectWithGroupingWithXMLRootSimpleTestCases(String name) throws Exception {
        super(name);
        Project project = new AnyObjectWithGroupingElementProjectNS();
        ((XMLAnyObjectMapping)((XMLDescriptor)project.getDescriptor(Root.class)).getMappingForAttributeName("any")).setUseXMLRoot(true);

        setProject(project);

        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/simple_ns_xmlroot.xml");
    }

    public Object getControlObject() {
        Root root = new Root();

        XMLRoot xmlroot = new XMLRoot();
        xmlroot.setObject("child's text");
        xmlroot.setLocalName("myns:theXMLRoot");
        xmlroot.setNamespaceURI("www.example.com/some-dir/some.xsd");

        root.setAny(xmlroot);
        return root;

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectWithGroupingWithXMLRootSimpleTestCases" };
        TestRunner.main(arguments);
    }
}
