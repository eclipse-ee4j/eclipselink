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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarAdapter;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarType;

@XmlRootElement(name="mycalendar")
public class MyCalendar {
    @XmlElement(name="date")
    @XmlJavaTypeAdapter(MyCalendarAdapter.class)
    public ArrayList<MyCalendarType> date;

    public boolean equals(Object obj) {
        if (!(obj instanceof MyCalendar)) {
            return false;
        }
        MyCalendar myCal = (MyCalendar) obj;
        for (Iterator<MyCalendarType> calIt = myCal.date.iterator(); calIt.hasNext(); ) {
            MyCalendarType myCalType = calIt.next();
            if (!date.contains(myCalType)) {
                return false;
            }
        }
        return true;
    }
}
