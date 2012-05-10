/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlanyelement;
// Example1
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlAnyElementCollectionModelTest extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlanyelement/xmlanyelementmodel.xml";
    private final static String XML_CHILD_ELEMENTS = "org/eclipse/persistence/testing/jaxb/javadoc/xmlanyelement/xmlanyelementmodel_child_elements_all.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlanyelement/xmlanyelementmodel.json";
    
    public XmlAnyElementCollectionModelTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = XmlAnyElementCollectionModel.class;
        setClasses(classes);        
    }
    
    public Map getProperties(){
    	Map props = new HashMap();
    	props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");
    	return props;
    }

    protected Object getControlObject() {
    	XmlAnyElementCollectionModel example = new XmlAnyElementCollectionModel();
        example.a = 1;
        example.b = 3;
  
        example.any = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(XML_CHILD_ELEMENTS));
            Element rootElem = doc.getDocumentElement();
            NodeList children = rootElem.getChildNodes();
            for(int i = 0; i < children.getLength(); i++) {
                if(children.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    example.any.add(children.item(i));
                }
            }
        } catch(Exception ex) {}


        return example;
    }


}