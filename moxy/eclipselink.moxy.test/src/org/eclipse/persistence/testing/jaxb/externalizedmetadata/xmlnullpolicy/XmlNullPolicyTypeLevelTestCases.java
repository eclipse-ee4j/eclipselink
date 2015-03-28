/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Martin Vojtek - July 8/2014
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.typelevel.TypeRoot;

/**
 * Test demonstrates the usage of class level xml-null-policy element declared in oxm.xml file.
 */
public class XmlNullPolicyTypeLevelTestCases extends JAXBTestCases {

    private final static String XML_CONTROL_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnullpolicy/typelevel/type_root_control.xml";

    private final static String BINDINGS_DOC = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnullpolicy/typelevel/eclipselink-oxm.xml";

    public XmlNullPolicyTypeLevelTestCases(String name) throws Exception {
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
