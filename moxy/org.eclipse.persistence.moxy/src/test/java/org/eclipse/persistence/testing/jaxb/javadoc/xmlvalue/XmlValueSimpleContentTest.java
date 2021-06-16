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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlvalue;

//Example 2

import java.io.StringWriter;

import jakarta.xml.bind.Marshaller;


import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlValueSimpleContentTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlvalue/xmlvaluesimplecontent.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlvalue/xmlvaluesimplecontent.json";
    private final static String JSON_RESOURCE_DEFAULT = "org/eclipse/persistence/testing/jaxb/javadoc/xmlvalue/xmlvaluesimplecontentdefault.json";

    public XmlValueSimpleContentTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = InternationalPrice.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "valuewrapper");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, "valuewrapper");
    }

    protected Object getControlObject() {
        InternationalPrice p = new InternationalPrice();
        p.setPrice(123.99);
        p.currency = "CANADIAN DOLLAR";
        return p;
    }

    public void testJSONNoValuePropException() throws Exception{
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(getControlObject(), sw);

        compareStringToControlFile("**testJSONMarshalToStringWriter**", sw.toString(), JSON_RESOURCE_DEFAULT);

    }
}
