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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.any;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class EmptyAnyCollectionWithRootTestCases extends JSONMarshalUnmarshalTestCases {

    private String JSON = "org/eclipse/persistence/testing/jaxb/json/any/NoAnyCollectionWithRoot.json";

    public EmptyAnyCollectionWithRootTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class});
        setControlJSON(JSON);
    }

    @Override
    protected Root getControlObject() {
        return new Root();
    }

}
