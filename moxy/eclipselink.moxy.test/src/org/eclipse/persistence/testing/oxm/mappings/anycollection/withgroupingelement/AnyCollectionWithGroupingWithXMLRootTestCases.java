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
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;

import java.util.Calendar;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyCollectionWithGroupingWithXMLRootTestCases extends XMLWithJSONMappingTestCases {
    public AnyCollectionWithGroupingWithXMLRootTestCases(String name) throws Exception {
        super(name);
        Project project = new AnyCollectionWithGroupingElementProjectNS();
        ((XMLAnyCollectionMapping)((XMLDescriptor)project.getDescriptor(Root.class)).getMappingForAttributeName("any")).setUseXMLRoot(true);
        
        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
        schemaRef.setSchemaContext("/myns:childType");
        schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        ((XMLDescriptor)project.getDescriptor(Child.class)).setSchemaReference(schemaRef);
        setProject(project);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/complex_children_ns_xmlroot.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/complex_children_ns_xmlroot.json");
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

        /*XMLRoot xmlroot0 = new XMLRoot();       
        //xmlroot0.setObject((String)null);
        xmlroot0.setObject("");
        xmlroot0.setObject("  ");
        xmlroot0.setRootElementName("myns:emptyValueElement");
        xmlroot0.setRootElementURI("www.example.com/some-dir/some.xsd");
        any.addElement(xmlroot0);*/

       

        XMLRoot xmlroot2 = new XMLRoot();

        xmlroot2.setObject(new Integer(15));
        //xmlroot2.setObject("15");
        xmlroot2.setLocalName("myns:theInteger");
        xmlroot2.setNamespaceURI("www.example.com/some-dir/some.xsd");
        xmlroot2.setSchemaType(XMLConstants.INT_QNAME);
        any.addElement(xmlroot2);
        
         XMLRoot xmlroot1 = new XMLRoot();
        xmlroot1.setObject("15");
        xmlroot1.setLocalName("myns:theString");
        xmlroot1.setNamespaceURI("www.example.com/some-dir/some.xsd");
        any.addElement(xmlroot1);
        
        XMLRoot next = new XMLRoot();
    
        Calendar theCalendar  =Calendar.getInstance();
        theCalendar.clear();
        theCalendar.set(Calendar.HOUR, 9);
        theCalendar.set(Calendar.HOUR_OF_DAY, 9);
        theCalendar.set(Calendar.AM_PM, Calendar.AM);
        theCalendar.set(Calendar.MINUTE, 30);
        theCalendar.set(Calendar.SECOND, 45);
        theCalendar.set(Calendar.MILLISECOND, 0);
        next.setSchemaType(XMLConstants.TIME_QNAME);
        
        next.setObject(theCalendar);
        next.setNamespaceURI("www.example.com/some-dir/some.xsd");
        next.setLocalName("theTime");        
        any.addElement(next);

        XMLRoot xmlroot3 = new XMLRoot();
        child = new Child();
        child.setContent("Child3");
        xmlroot3.setObject(child);
        xmlroot3.setLocalName("myns:someChild");
        xmlroot3.setNamespaceURI("www.example.com/some-dir/some.xsd");
        any.addElement(xmlroot3);
        
        XMLRoot xmlroot4 = new XMLRoot();
        child = new Child();
        child.setContent("Child4");
        xmlroot4.setObject(child);
        xmlroot4.setLocalName("myns:blah");
        xmlroot4.setNamespaceURI("www.example.com/some-dir/some.xsd");
        any.addElement(xmlroot4);

        XMLRoot xmlroot5 = new XMLRoot();
        child = new Child();        
        xmlroot5.setObject(child);
        xmlroot5.setLocalName("myns:someChild");
        xmlroot5.setNamespaceURI("www.example.com/some-dir/some.xsd");
        any.addElement(xmlroot5);

        root.setAny(any);
        return root;
    }

    public Object getJSONReadControlObject(){
    	//different order - mixed always last and same qnames grouped together, no namespaces
    
    	   Root root = new Root();
           Vector any = new Vector();
           
           Child child = new Child();
           child.setContent("Child1");
           any.addElement(child);
           child = new Child();
           child.setContent("Child2");
           any.addElement(child);
         

           XMLRoot xmlroot2 = new XMLRoot();

           xmlroot2.setObject(new Integer(15));
           //xmlroot2.setObject("15");
           xmlroot2.setLocalName("theInteger");
           xmlroot2.setSchemaType(XMLConstants.INT_QNAME);
           any.addElement(xmlroot2);
           
            XMLRoot xmlroot1 = new XMLRoot();
           xmlroot1.setObject("15");
           xmlroot1.setLocalName("theString");
           any.addElement(xmlroot1);
           
           XMLRoot next = new XMLRoot();
       
           Calendar theCalendar  =Calendar.getInstance();
           theCalendar.clear();
           theCalendar.set(Calendar.HOUR, 9);
           theCalendar.set(Calendar.HOUR_OF_DAY, 9);
           theCalendar.set(Calendar.AM_PM, Calendar.AM);
           theCalendar.set(Calendar.MINUTE, 30);
           theCalendar.set(Calendar.SECOND, 45);
           theCalendar.set(Calendar.MILLISECOND, 0);
           next.setSchemaType(XMLConstants.TIME_QNAME);
           
           next.setObject(theCalendar);
           next.setLocalName("theTime");        
           any.addElement(next);
           
           XMLRoot xmlroot3 = new XMLRoot();
           child = new Child();
           child.setContent("Child3");
           xmlroot3.setObject(child);
           xmlroot3.setLocalName("someChild");
           any.addElement(xmlroot3);
           
           XMLRoot xmlroot5 = new XMLRoot();
           child = new Child();        
           xmlroot5.setObject(child);
           xmlroot5.setLocalName("someChild");
           any.addElement(xmlroot5);

           XMLRoot xmlroot4 = new XMLRoot();
           child = new Child();
           child.setContent("Child4");
           xmlroot4.setObject(child);
           xmlroot4.setLocalName("blah");
           any.addElement(xmlroot4);
           
           any.add("myTextNode");
           
           
           root.setAny(any);
           return root;
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionWithGroupingWithXMLRootTestCases" };
        TestRunner.main(arguments);
    }
}
