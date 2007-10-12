/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;

import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionWithoutGroupingWithXMLRootTestCases extends XMLMappingTestCases {
    public AnyCollectionWithoutGroupingWithXMLRootTestCases(String name) throws Exception {
        super(name);
        Project project = new AnyCollectionWithGroupingElementProject();
        
        ((XMLAnyCollectionMapping)((XMLDescriptor)project.getDescriptor(Root.class)).getMappingForAttributeName("any")).setUseXMLRoot(true);
        
        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
        schemaRef.setSchemaContext("/childType");
        schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        ((XMLDescriptor)project.getDescriptor(Child.class)).setSchemaReference(schemaRef);
        
        NamespaceResolver nsr = new NamespaceResolver();
        ((XMLDescriptor)project.getDescriptor(Root.class)).setNamespaceResolver(nsr);
        ((XMLDescriptor)project.getDescriptor(Child.class)).setNamespaceResolver(nsr);
        setProject(project);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/complex_children_xmlroot.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        any.add("myTextNode");
        Child child = new Child();
        child.setContent("Child1");
        any.addElement(child);
        child = new Child();
        child.setContent("Child2");
        any.addElement(child);

        XMLRoot xmlroot1 = new XMLRoot();
        xmlroot1.setObject("theStringValue");
        xmlroot1.setLocalName("theString");        
        any.addElement(xmlroot1);

        XMLRoot xmlroot2 = new XMLRoot();

        //xmlroot2.setObject(new Integer(15));
        xmlroot2.setObject("15");
        xmlroot2.setLocalName("theInteger");        
        any.addElement(xmlroot2);

        XMLRoot xmlroot3 = new XMLRoot();
        child = new Child();
        child.setContent("Child3");
        xmlroot3.setObject(child);
        xmlroot3.setLocalName("someChild");                
        any.addElement(xmlroot3);
        
        XMLRoot xmlroot4 = new XMLRoot();
        child = new Child();
        child.setContent("Child4");
        xmlroot4.setObject(child);
        xmlroot4.setLocalName("blah");        
        any.addElement(xmlroot4);


        XMLRoot xmlroot5 = new XMLRoot();
        child = new Child();        
        xmlroot5.setObject(child);
        xmlroot5.setLocalName("someChild"); 
        xmlroot5.setNamespaceURI("my.uri1");
        any.addElement(xmlroot5);
        
        root.setAny(any);
        return root;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionWithoutGroupingWithXMLRootTestCases" };
        TestRunner.main(arguments);
    }
}