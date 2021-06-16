/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.UnmarshalException;
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
