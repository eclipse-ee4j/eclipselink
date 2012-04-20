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
 * Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlAnyElementBaseTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-default-ns.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-default-ns.json";
	
	public XmlAnyElementBaseTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});
        
    }  
	
	  public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/eclipselink-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	        return properties;
		}
	  
	  protected Object getControlObject() {
		  Employee ctrlEmp = new Employee();
          ctrlEmp.a = 1;
          ctrlEmp.b = "3";
          Element elt = null;
          try {
              Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
              elt = doc.createElement(MyDomAdapter.STUFF_STR);
              elt.appendChild(doc.createTextNode("This is some stuff"));
          } catch (ParserConfigurationException e) {
              e.printStackTrace();
          }
          ctrlEmp.stuff = elt;
          return ctrlEmp;
	  }
	  
	  public void testLaxFalse() throws JAXBException {
	        // setup the control Employee
	        Element empElt = null;
	        try {
	            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	            Element aElt = doc.createElement("a");
	            aElt.appendChild(doc.createTextNode("666"));
	            Element bElt = doc.createElement("b");
	            bElt.appendChild(doc.createTextNode("999"));
	            empElt = doc.createElement("employee");
	            empElt.appendChild(aElt);
	            empElt.appendChild(bElt);
	        } catch (ParserConfigurationException e1) {
	            e1.printStackTrace();
	        }
	        Employee ctrlEmp = new Employee();
	        ctrlEmp.a = 1;
	        ctrlEmp.b = "3";
	        ctrlEmp.stuff = empElt;

	        // test unmarshal	        
	        try {
	            String src = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-with-employee.xml";
	            Employee emp = (Employee) jaxbUnmarshaller.unmarshal(getControlDocument(src));
	            assertNotNull("The Employee object is null after unmarshal.", emp);
	            assertEquals(ctrlEmp, emp);
	        } catch (Exception e) {
	            e.printStackTrace();
	            fail("An unexpected exception occurred unmarshalling the document.");
	        }
	    }
	  
	    protected Document getControlDocument(String xmlResource) throws Exception {
	        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
	        Document document = parser.parse(inputStream);
	        OXTestCase.removeEmptyTextNodes(document);
	        return document;
	    }
	
}
