/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Martin Vojtek - July 7/2014
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementnillable;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementnillable.typeleveloverride.OverrideRoot;

/**
 * Test demonstrates that XmlElement annotation declared on class overrides XmlElementNillable annotation declared on package level.
 */
public class XmlElementNillableTypeLevelOverrideTestCases extends JAXBTestCases {

    private final static String XML_CONTROL_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementnillable/typeleveloverride/root_override.xml";

    public XmlElementNillableTypeLevelOverrideTestCases(String name) throws Exception {
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
