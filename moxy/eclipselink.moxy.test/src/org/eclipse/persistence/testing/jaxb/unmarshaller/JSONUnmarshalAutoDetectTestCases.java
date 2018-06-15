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
//     Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JSONUnmarshalAutoDetectTestCases extends JSONUnmarshalTestCases{
    private static final String XML_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/validMarshal.xml";
    private static final String JSON_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/validMarshal.json";


    public JSONUnmarshalAutoDetectTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE_VALID);
        setControlDocument(XML_RESOURCE_VALID);
        setClasses(new Class[]{TestObject.class});
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);
    }

}
