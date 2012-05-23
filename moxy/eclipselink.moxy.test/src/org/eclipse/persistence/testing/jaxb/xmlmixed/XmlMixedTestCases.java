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
 * Matt MacIvor - July 4th 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmixed;

import java.util.ArrayList;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlMixedTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlmixed/root.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlmixed/root.json";

    public XmlMixedTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.setAttr("attribute value");
        root.setElem("element value");
        root.setObjects(new ArrayList<Object>());
        root.getObjects().add("Text Value");
        
        root.getObjects().add("Text Value2");
        return root;
    }
    
    public Object getReadControlObject() {
        Root root = new Root();
        root.setAttr("attribute value");
        root.setElem("element value");
        root.setObjects(new ArrayList<Object>());
        root.getObjects().add("Text ValueText Value2");        
        return root;
    }
    
    protected Object getJSONReadControlObject() {
       return getControlObject();
    }
    
    public void testObjectToXMLDocument() throws Exception {    	
    }
}
