/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Martin Vojtek - July 7/2014
package org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.nillabletype.NillableRoot;

/**
 * Test demonstrates XmlNullPolicy annotation declared on class level.
 */
public class XmlNullPolicyTypeLevelTestCases extends JAXBTestCases {

    private final static String XML_CONTROL_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlnullpolicy/nillabletype/nillable_type.xml";

    public XmlNullPolicyTypeLevelTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_CONTROL_RESOURCE);
        setClasses(new Class[] { NillableRoot.class });
    }

    @Override
    protected NillableRoot getControlObject() {
        NillableRoot controlObject = new NillableRoot();
        controlObject.setB("B");

        return controlObject;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
