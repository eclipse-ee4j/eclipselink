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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class ChoiceCollectionTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String JSON = "org/eclipse/persistence/testing/jaxb/json/wrapper/ChoiceCollection.json";

    public ChoiceCollectionTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {ChoiceCollectionRoot.class});
        setControlJSON(JSON);
    }

    @Override
    protected JAXBElement<ChoiceCollectionRoot> getControlObject() {
        ChoiceCollectionRoot root = new ChoiceCollectionRoot();
        root.getWrapperItems().add("Hello");
        root.getWrapperItems().add("World");
        root.getWrapperItems().add(123);
        root.getWrapperItems().add(456);
        return new JAXBElement<ChoiceCollectionRoot>(new QName(""), ChoiceCollectionRoot.class, root);
    }

    @Override
    public Class getUnmarshalClass() {
        return ChoiceCollectionRoot.class;
    }

    @Override
    public Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>(3);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
        properties.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        return properties;
    }

}
