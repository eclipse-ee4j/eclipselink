/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - April 10, 2013
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import java.io.InputStream;
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
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholderid_schema.json";

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

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);
    }

}
