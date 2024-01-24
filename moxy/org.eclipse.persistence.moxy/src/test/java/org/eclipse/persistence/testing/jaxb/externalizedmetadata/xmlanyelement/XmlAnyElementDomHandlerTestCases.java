/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlAnyElementDomHandlerTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee.json";

    public XmlAnyElementDomHandlerTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class<?>[]{Employee.class});
    }

    @Override
    public Map getProperties(){
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/eclipselink-oxm-dom-handler.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        return properties;
    }

     @Override
     protected Object getControlObject() {
          Employee ctrlEmp = new Employee();
         ctrlEmp.a = 1;
         ctrlEmp.b = "3";
         Element elt = null;
         try {
             Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
             elt = doc.createElementNS("extra", "e:stuff");
             elt.appendChild(doc.createTextNode("Some stuff"));
         } catch (ParserConfigurationException e) {
             e.printStackTrace();
         }
         ctrlEmp.stuff = elt;
         return ctrlEmp;
      }

     @Override
     public Object getReadControlObject() {
        Employee ctrlEmp = new Employee();
        ctrlEmp.a = 1;
        ctrlEmp.b = "3";

        ctrlEmp.stuff = "Giggity";
        return ctrlEmp;
      }

     @Override
     public void testRoundTrip(){
         //doesn't apply
     }

}
