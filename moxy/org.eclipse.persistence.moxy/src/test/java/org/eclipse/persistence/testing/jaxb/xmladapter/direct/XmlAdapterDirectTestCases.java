/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterDirectTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct.json";
    private final static int DAY = 12;
    private final static int MONTH = 4;
    private final static int YEAR = 1997;

    public XmlAdapterDirectTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyCalendar.class;
        classes[1] = MyCalendarType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyCalendar myCal = new MyCalendar();
        MyCalendarType mcType = new MyCalendarType();
        mcType.day = DAY;
        mcType.month = MONTH;
        mcType.year = YEAR;
        myCal.date = mcType;
        return myCal;
    }
}
