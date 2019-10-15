/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class XmlListTestCases extends OXTestCase {

    private static String XML = "<root xmlns:ns0=\"http://jaxb.dev.java.net/array\">1 2</root>";
    private static String JSON = "\"root\":[\"1\",\"2\"]";
    private static String JSON_INT = "\"root\":[1,2]";
    public XmlListTestCases(String name) {
        super(name);
    }

    public void testStringArray() throws Exception{
        _testArray(new String[] {"1", "2"});
    }

    public void testIntArray() throws Exception{
        _testArray(new int[] {1, 2});
    }

    public void _testArray(Object controlArray) throws Exception{
        XmlList xmlList = new XmlList() {
            public Class<? extends Annotation> annotationType() {
                return XmlList.class;
            }
        };

        Annotation[] a = { xmlList };
        TypeMappingInfo t = new TypeMappingInfo();
        t.setType(controlArray.getClass());
        t.setXmlTagName(new QName("root"));
        t.setAnnotations(a);
        TypeMappingInfo[] types = { t };
        JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory .createContext(types, null, Thread.currentThread().getContextClassLoader());

        StringReader stringReader = new StringReader(XML);
        StreamSource streamSource = new StreamSource(stringReader);
        JAXBUnmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement o = unmarshaller.unmarshal(streamSource, t);
        Object testArray = o.getValue();
        assertEquals(Array.get(controlArray, 0), Array.get(testArray, 0));
        assertEquals(Array.get(controlArray, 1), Array.get(testArray, 1));

        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        JAXBMarshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(o, streamResult, t);
        assertEquals(XML, stringWriter.toString());
    }

    public void testJSONStringArray() throws Exception{
        _testJSONArray(new String[] {"1", "2"}, JSON, "application/json");
    }

    public void testJSONIntArray() throws Exception{
        _testJSONArray(new int[] {1, 2}, JSON_INT, "application/json");
    }



    public void _testJSONArray(Object controlArray, String controlString, String mediaType) throws Exception{
        XmlList xmlList = new XmlList() {
            public Class<? extends Annotation> annotationType() {
                return XmlList.class;
            }
        };

        Annotation[] a = { xmlList };
        TypeMappingInfo t = new TypeMappingInfo();
        t.setType(controlArray.getClass());
        t.setXmlTagName(new QName("root"));
        t.setAnnotations(a);
        TypeMappingInfo[] types = { t };

        Map props = new HashMap();
        props.put(JAXBContextProperties.MEDIA_TYPE, mediaType);

        JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory .createContext(types, props, Thread.currentThread().getContextClassLoader());

        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);

        JAXBMarshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(controlArray, streamResult, t);

        assertEquals(removeWhiteSpaceFromString(controlString), removeWhiteSpaceFromString(stringWriter.toString()));
    }
}
