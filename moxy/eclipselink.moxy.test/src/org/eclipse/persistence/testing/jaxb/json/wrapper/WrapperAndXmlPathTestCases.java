/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WrapperAndXmlPathTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String JSON = "org/eclipse/persistence/testing/jaxb/json/wrapper/WrapperAndXmlPath.json";

    public WrapperAndXmlPathTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON);
        setClasses(new Class[] {WrapperAndXmlPathRoot.class});
    }

    @Override
    protected Object getControlObject() {
        WrapperAndXmlPathRoot fooRoot = new WrapperAndXmlPathRoot();
        fooRoot.name = "FOO";
        fooRoot.foos.add(new WrapperAndXmlPathRoot());
        fooRoot.foos.add(new WrapperAndXmlPathRoot());
        return fooRoot;
    }

    @Override
    public Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>(3);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        properties.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        return properties;
    }

}
