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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlanyattribute;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAnyAttributeTest extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlanyattribute/xmlanyattribute.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlanyattribute/xmlanyattribute.json";

    public XmlAnyAttributeTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = XmlAnyAttributeModel.class;
        setClasses(classes);

        jaxbUnmarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("www.example.com","ns0");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    public JAXBMarshaller getJSONMarshaller() throws Exception{
        JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        jsonMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("www.example.com","ns0");

        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        return jsonMarshaller;
    }

    protected Object getControlObject() {

        XmlAnyAttributeModel model = new XmlAnyAttributeModel();
        model.setTitle("A mixture of elements");

        HashMap<QName,Object> amap = new HashMap<QName,Object>();
        amap.put(new QName("www.example.com", "any"), "blah");
        model.any = amap;
        return model;
    }


}
