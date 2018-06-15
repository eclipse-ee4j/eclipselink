/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.prefixmapper;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class DefaultNSPrefixMapperSimpleTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/prefixmapper/simple.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/prefixmapper/simple.json";
    public DefaultNSPrefixMapperSimpleTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Simple.class});
        Map<String, String> map = new HashMap<String, String>();

        map.put("namespace1","ns1");
        map.put("namespace2","");
        map.put("namespace3","ns3");

        jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, map);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, map);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Simple simple = new Simple();
        simple.thing1 =1;
        simple.thing2 =2;
        return simple;
    }

}
