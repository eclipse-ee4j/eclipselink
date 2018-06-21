/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4 - October 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefMissingIdTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlidref/missing_id.xml";
    private static final String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlidref/missing_id.json";

    public XmlIdRefMissingIdTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Owner.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Owner owner = new Owner();
        owner.m_activityId = "1";
        Thing theThing = new Thing();
        theThing.m_calendarId = "2";
        owner.m_calendar = theThing;
        return owner;
    }

    @Override
    public Object getReadControlObject() {
        Owner owner = new Owner();
        owner.m_activityId = "1";
        owner.m_calendar = null;
        return owner;
    }

    //Not applicable
    public void testRoundTrip(){}
}
