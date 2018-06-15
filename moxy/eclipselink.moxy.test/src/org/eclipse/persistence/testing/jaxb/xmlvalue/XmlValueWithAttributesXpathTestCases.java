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
//     Denise Smith, August 2013
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlValueWithAttributesXpathTestCases extends JAXBWithJSONTestCases {

    private final static String BINDINGS_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/phone_number-oxm.xml";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/phone_number_xpath_atts.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/phone_number_xpath_atts.json";
    private final static String CONTROL_NUMBER = "123-4567";

    public XmlValueWithAttributesXpathTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = PhoneNumberWithAtts.class;
        setClasses(classes);
    }

    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_VALUE_WRAPPER, "value");

        InputStream is = getClass().getClassLoader().getResourceAsStream(BINDINGS_RESOURCE);
        props.put(JAXBContextProperties.OXM_METADATA_SOURCE, is);
        return props;
    }

    protected Object getControlObject() {
        PhoneNumberWithAtts pn = new PhoneNumberWithAtts();
        pn.number = CONTROL_NUMBER;
        pn.areaCode = "613";
        return pn;
    }

    /*
    public void testSchemaGen() throws Exception{
        InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlvalue/phone_number_attr_xpath.xsd");
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(controlInputStream);
        this.testSchemaGen(controlSchemas);
    }*/
}
