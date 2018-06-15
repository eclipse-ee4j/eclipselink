/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0
package org.eclipse.persistence.testing.jaxb.json.type;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.rootlevellist.WithXmlRootElementRoot;
import org.eclipse.persistence.testing.jaxb.json.type.model.NameValue;
import org.eclipse.persistence.testing.jaxb.json.type.model.Properties;

/**
 * Tests correct unmarshal of type property.
 *
 * @author Martin Vojtek
 *
 */
public class TypeNameValueTestCases extends JSONTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/type/type_name_value.json";

    public TypeNameValueTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Properties.class});
        setControlJSON(JSON_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonUnmarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");

        jsonMarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonMarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
    }

    public void testUnmarshalFronStreamSource() throws Exception {

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(JSON_RESOURCE)) {

            Properties  properties = (Properties) jsonUnmarshaller.unmarshal(new StreamSource(is), Properties.class).getValue();

            assertEquals(buildProperties(), properties);
        }
    }

    protected Object getJSONReadControlObject() {

        QName name = new QName("");

        JAXBElement<Object> jbe = new JAXBElement<Object>(name, Object.class, buildProperties() );
        return jbe;
    }

    protected Object getControlObject() {
        return buildProperties();
    }

    private Properties buildProperties() {
        Properties props = new Properties();

        NameValue pair1 = new NameValue();
        pair1.name = "name1";
        pair1.value = "value1";

        NameValue pair2 = new NameValue();
        pair2.name = "type";
        pair2.value = "tvalue";

        NameValue pair3 = new NameValue();
        pair3.name = "name2";
        pair3.value = "value2";

        props.properties.add(pair1);
        props.properties.add(pair2);
        props.properties.add(pair3);

        return props;
    }

}
