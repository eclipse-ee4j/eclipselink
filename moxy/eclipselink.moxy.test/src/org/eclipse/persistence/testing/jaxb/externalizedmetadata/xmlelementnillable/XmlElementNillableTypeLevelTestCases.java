/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Martin Vojtek - July 8/2014
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.typelevel.TypeRoot;

/**
 * Test demonstrates the usage of class level xml-element-nillable element declared in oxm.xml file.
 */
public class XmlElementNillableTypeLevelTestCases extends JAXBTestCases {

    private final static String XML_CONTROL_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementnillable/typelevel/type_root_control.xml";

    private final static String BINDINGS_DOC = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementnillable/typelevel/eclipselink-oxm.xml";

    public XmlElementNillableTypeLevelTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_CONTROL_RESOURCE);
        setClasses(new Class[] { TypeRoot.class });
    }

    public Map getProperties() {

        InputStream iStream = ClassLoader.getSystemResourceAsStream(BINDINGS_DOC);

        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);

        return properties;
    }

    @Override
    protected TypeRoot getControlObject() {
        TypeRoot controlObject = new TypeRoot();
        controlObject.setB("B");

        return controlObject;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
