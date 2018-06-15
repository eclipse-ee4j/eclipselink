/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.qname;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementDeclQNameTestCases extends JAXBWithJSONTestCases{
     private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/qname.xml";
     private static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/qname_write.xml";
     private static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/qname.json";
     private static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/qname_write.json";

    public XmlElementDeclQNameTestCases(String name) throws Exception {
        super(name);
         setControlDocument(XML_RESOURCE);
         setControlJSON(JSON_RESOURCE);
         setWriteControlDocument(XML_WRITE_RESOURCE);
         setWriteControlJSON(JSON_WRITE_RESOURCE);
         setClasses(new Class[] {ObjectFactory.class});

    }

    @Override
    protected Object getControlObject() {
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<QName> jbe=objectFactory.createTheTest(Constants.STRING_QNAME);
        return jbe;
    }

    @Override
    protected Object getJSONReadControlObject() {
        ObjectFactory objectFactory = new ObjectFactory();
        QName qName = new QName(null, "string", "xsd");
        JAXBElement<QName> jbe=objectFactory.createTheTest(qName);
        return jbe;
    }

}
