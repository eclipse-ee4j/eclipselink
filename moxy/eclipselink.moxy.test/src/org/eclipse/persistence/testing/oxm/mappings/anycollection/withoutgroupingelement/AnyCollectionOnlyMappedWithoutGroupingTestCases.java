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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;

import java.io.InputStream;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class AnyCollectionOnlyMappedWithoutGroupingTestCases extends XMLMappingTestCases {
    private Document writeControlDoc;

    public AnyCollectionOnlyMappedWithoutGroupingTestCases(String name) throws Exception {
        super(name);
        Project project = new AnyCollectionWithGroupingElementProject();

        XMLDirectMapping newMapping = new XMLDirectMapping();
        newMapping.setAttributeName("directMapping");
        newMapping.setXPath("theString/text()");
        newMapping.setDescriptor(project.getDescriptor(Root.class));
        project.getDescriptor(Root.class).getMappings().add(0, newMapping);

        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
        schemaRef.setSchemaContext("/childType");
        schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        ((XMLDescriptor)project.getDescriptor(Child.class)).setSchemaReference(schemaRef);

        ((XMLAnyCollectionMapping)((XMLDescriptor)project.getDescriptor(Root.class)).getMappingForAttributeName("any")).setUseXMLRoot(true);

        setProject(project);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/complex_children_mapped_only.xml");
        setWriteControlDoc("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/complex_children_mapped_only.xml");

    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        any.add("myTextNode");

        Child child1 = new Child();
        child1.setContent("Child1");
        any.add(child1);

        Child child2 = new Child();
        child2.setContent("Child2");
        any.add(child2);

        XMLRoot xmlroot2 = new XMLRoot();

        xmlroot2.setObject("15");
        xmlroot2.setLocalName("theInteger");

        any.addElement(xmlroot2);

        XMLRoot xmlroot3 = new XMLRoot();
        Child child = new Child();
        child.setContent("Child3");
        xmlroot3.setObject(child);

        xmlroot3.setLocalName("someChild");

        any.addElement(xmlroot3);

        XMLRoot xmlroot4 = new XMLRoot();
        Child child4 = new Child();
        child4.setContent("Child4");
        xmlroot4.setObject(child4);
        xmlroot4.setLocalName("blah");
        any.addElement(xmlroot4);

        root.setDirectMapping("theStringValue");

        root.setAny(any);

        return root;
    }

    public Document getWriteControlDocument() {
        return writeControlDoc;
    }

    public void setWriteControlDoc(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        writeControlDoc = parser.parse(inputStream);
        removeEmptyTextNodes(writeControlDoc);
        inputStream.close();
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionOnlyMappedWithoutGroupingTestCases" };
        TestRunner.main(arguments);
    }
}
