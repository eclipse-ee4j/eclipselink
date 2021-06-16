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
package org.eclipse.persistence.testing.jaxb.xmladapter.compositecollection;

import java.util.ArrayList;
import java.util.Date;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterCompositeCollectionTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositecollection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositecollection.json";
    private final static int DAY_1 = 1;
    private final static int DAY_2 = 2;
    private final static int DAY_3 = 6;
    private final static int MONTH_1 = 1;
    private final static int MONTH_2 = 2;
    private final static int MONTH_3 = 6;
    private final static int YEAR_1 = 2001;
    private final static int YEAR_2 = 2002;
    private final static int YEAR_3 = 2006;

    public XmlAdapterCompositeCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyDates.class;
        classes[1] = MyDateType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyDates myDates = new MyDates();
        Date date1 = new Date(YEAR_1, MONTH_1, DAY_1);
        Date date2 = new Date(YEAR_2, MONTH_2, DAY_2);
        Date date3 = new Date(YEAR_3, MONTH_3, DAY_3);
        myDates.dateList = new ArrayList();
        myDates.dateList.add(date1);
        myDates.dateList.add(date2);
        myDates.dateList.add(date3);
        return myDates;
    }
}
