/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Matt MacIvor - August 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarType;

public class XmlAdapterDirectCollectionArrayTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositedirectcollection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositedirectcollection.json";
    private final static int DAY_1 = 12;
    private final static int MONTH_1 = 4;
    private final static int YEAR_1 = 1997;
    private final static int DAY_2 = 11;
    private final static int MONTH_2 = 6;
    private final static int YEAR_2 = 2006;

    public XmlAdapterDirectCollectionArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyCalendarWithArray.class;
        classes[1] = MyCalendarType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyCalendarType mcType = new MyCalendarType();
        mcType.day = DAY_1;
        mcType.month = MONTH_1;
        mcType.year = YEAR_1;

        MyCalendarType mcType2 = new MyCalendarType();
        mcType2.day = DAY_2;
        mcType2.month = MONTH_2;
        mcType2.year = YEAR_2;

        MyCalendarWithArray myCal = new MyCalendarWithArray();
        myCal.date = new MyCalendarType[]{mcType, mcType2};
        return myCal;
    }
}

