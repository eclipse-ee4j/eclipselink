/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.inheritance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBInheritanceTestCases extends JAXBWithJSONTestCases {
	public JAXBInheritanceTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] { A.class, B.class, C.class, D.class, E.class });
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/inheritance.xml");
		setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/inheritance.json");
	}

	public Object getControlObject() {
		// reads a document that also contains a value for "ddd" and makes sure
		// we ignore it
		E object = new E();
		object.setAaa(1);
		object.setBbb(2);
		object.setCcc(3);
		object.setDdd(4);
		object.setEee(5);
		QName qname = new QName("", "a-element");
		JAXBElement jaxbElement = new JAXBElement(qname, A.class, object);

		return jaxbElement;
	}

	public void testSchemaGen() throws Exception {
		testSchemaGen(getControlSchemaFiles());
	}
		
    public  List<InputStream> getControlSchemaFiles(){
		List<InputStream> controlSchema = new ArrayList<InputStream>();

    	InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/inheritance/schema0.xsd");
    	 		
 		controlSchema.add(instream1);
 		return controlSchema;
    }    
    
    public void testUnmarshalToSubClass() throws Exception{
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            StreamSource ss = new StreamSource(instream);
            JAXBElement testObject = getJAXBUnmarshaller().unmarshal(ss, A.class);
            Object value = testObject.getValue();
            instream.close();
            assertTrue(value instanceof E);
            assertEquals(5, ((E)value).getEee());
            
            log("\n**xmlToObjectTest**");
            log("Expected:");
            log(getReadControlObject().toString());
            log("Actual:");
            log(testObject.toString());

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj);           
        }
    }
	
}
