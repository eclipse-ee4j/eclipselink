/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import org.eclipse.persistence.jaxb.MOXySystemProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests verify, that marshalling/unmarshalling works for field with @XmlID annotation if type is different, than String and system property org.eclipse.persistence.moxy.annotation.xml-id-extension is set.
 */
public class XmlIdSystemPropertyNonDefaultTestCase extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlidref/missing_id.xml";
    private static final String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlidref/missing_id_integer_id.json";

    static {
        System.setProperty(MOXySystemProperties.XML_ID_EXTENSION, "true");
    }

    public XmlIdSystemPropertyNonDefaultTestCase(String name) throws Exception {
        super(name);
        setClasses(new Class[]{OwnerIntegerId.class, ThingIntegerId.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        OwnerIntegerId owner = new OwnerIntegerId();
        owner.m_activityId = "1";
        ThingIntegerId theThing = new ThingIntegerId();
        theThing.m_calendarId = 2;
        owner.m_calendar = theThing;
        return owner;
    }

    @Override
    public Object getReadControlObject() {
        OwnerIntegerId owner = new OwnerIntegerId();
        owner.m_activityId = "1";
        owner.m_calendar = null;
        return owner;
    }

    //Not applicable
    public void testRoundTrip() {    }
}
