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
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.typeleveloverride.OverrideRoot;

/**
 * Test demonstrates that XmlNullPolicy annotation declared on class level overrides XmlNullPolicy annotation declare in package-info.java
 */
public class XmlNullPolicyTypeLevelOverrideTestCases extends JAXBTestCases {

    private final static String XML_CONTROL_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlnullpolicy/typeleveloverride/root_override.xml";

    public XmlNullPolicyTypeLevelOverrideTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_CONTROL_RESOURCE);
        setClasses(new Class[] { OverrideRoot.class });
    }

    @Override
    protected OverrideRoot getControlObject() {
        OverrideRoot controlObject = new OverrideRoot();
        controlObject.setB("B");

        return controlObject;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
