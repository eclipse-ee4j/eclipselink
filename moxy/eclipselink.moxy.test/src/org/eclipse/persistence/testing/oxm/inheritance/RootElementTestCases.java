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
//     bdoughan - January 5/2010 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.inheritance;

import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

/**
 * This test case is to verify the fix for the following bug:
 * Bug 298664 - XMLHelper.INSTANCE.load deserializes xml instance to a wrong
 *              data object type
 */
public class RootElementTestCases extends XMLWithJSONMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/inheritance/rootelement.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/inheritance/rootelement.json";

    public RootElementTestCases(String name) throws Exception {
        super(name);
        setProject(new RootElementProject());
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName("car");
        xmlRoot.setSchemaType(new QName(null, "car"));
        xmlRoot.setObject(new Car());
        return xmlRoot;
    }

    @Override
    public Object getReadControlObject() {
        return new Car();
    }

}
