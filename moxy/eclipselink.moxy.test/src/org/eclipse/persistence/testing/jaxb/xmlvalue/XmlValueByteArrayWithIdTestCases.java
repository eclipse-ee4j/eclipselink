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
 *     Denise Smith - April 10, 2013
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlValueByteArrayWithIdTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholderid.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholderid.json";

    public XmlValueByteArrayWithIdTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = BytesHolderWithXmlId.class;
        classes[1] = Root.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        BytesHolderWithXmlId holder = new BytesHolderWithXmlId();
        byte[] bytes = "ASTRING".getBytes();
        holder.theBytes = bytes;

        Root theRoot = new Root();
        theRoot.thing = holder;
        return theRoot;
    }           
}
