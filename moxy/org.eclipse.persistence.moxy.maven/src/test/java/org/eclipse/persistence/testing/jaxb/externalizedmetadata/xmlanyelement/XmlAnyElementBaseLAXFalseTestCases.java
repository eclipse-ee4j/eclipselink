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
// Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlAnyElementBaseLAXFalseTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-with-employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-with-employeedom.json";


    public XmlAnyElementBaseLAXFalseTestCases(String name) throws Exception{
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

    public Object getControlObject(){
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
            return ctrlEmp;
    }

    public Object getReadControlObject(){
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
            return ctrlEmp;
    }
}
