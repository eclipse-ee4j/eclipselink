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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement;

import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyObjectXMLRootSimpleNonStringTestCases extends XMLMappingTestCases {
    public AnyObjectXMLRootSimpleNonStringTestCases(String name) throws Exception {
        super(name);
        Project project = new AnyObjectWithGroupingElementProjectNS();
        ((XMLAnyObjectMapping)((XMLDescriptor)project.getDescriptor(Root.class)).getMappingForAttributeName("any")).setUseXMLRoot(true);

        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
        schemaRef.setSchemaContext("/myns:childType");
        schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        ((XMLDescriptor)project.getDescriptor(Child.class)).setSchemaReference(schemaRef);
        setProject(project);

        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/simple_non_string_xmlroot.xml");
    }

    public Object getControlObject() {
        Root root = new Root();

        XMLRoot xmlroot = new XMLRoot();
        Child child = new Child();
        //child.setContent("22");
        float theFloat = 22;
        //child.setContent(theFloat);        
        xmlroot.setSchemaType(XMLConstants.FLOAT_QNAME);
        xmlroot.setObject(theFloat);
        xmlroot.setLocalName("myns:theFloat");
        xmlroot.setNamespaceURI("www.example.com/some-dir/some.xsd");

        root.setAny(xmlroot);
        return root;

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectXMLRootSimpleNonStringTestCases" };
        TestRunner.main(arguments);
    }
}
