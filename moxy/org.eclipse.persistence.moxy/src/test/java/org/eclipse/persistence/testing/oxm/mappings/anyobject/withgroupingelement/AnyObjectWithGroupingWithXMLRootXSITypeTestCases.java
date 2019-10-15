/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

public class AnyObjectWithGroupingWithXMLRootXSITypeTestCases extends XMLMappingTestCases
{

   public AnyObjectWithGroupingWithXMLRootXSITypeTestCases(String name) throws Exception {
        super(name);
        Project project = new AnyObjectWithGroupingElementProjectNS();
        ((XMLAnyObjectMapping)((XMLDescriptor)project.getDescriptor(Root.class)).getMappingForAttributeName("any")).setUseXMLRoot(true);

        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
        schemaRef.setSchemaContext("/myns:childType");
        schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        ((XMLDescriptor)project.getDescriptor(Child.class)).setSchemaReference(schemaRef);
        setProject(project);

        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/complex_child_ns_xmlroot_xsitype.xml");
    }

    public Object getControlObject() {
        Root root = new Root();

        XMLRoot xmlroot = new XMLRoot();
        Child child = new Child();
        child.setContent("child's text");
        xmlroot.setObject(child);
        xmlroot.setLocalName("myns:blah");
        xmlroot.setNamespaceURI("www.example.com/some-dir/some.xsd");

        root.setAny(xmlroot);
        return root;

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectWithGroupingWithXMLRootXSITypeTestCases" };
        TestRunner.main(arguments);
    }
}
