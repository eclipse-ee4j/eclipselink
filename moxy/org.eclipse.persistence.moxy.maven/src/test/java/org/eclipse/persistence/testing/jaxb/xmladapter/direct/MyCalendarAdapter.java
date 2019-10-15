/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyCalendarAdapter extends XmlAdapter<Calendar, MyCalendarType> {
    public MyCalendarType unmarshal(Calendar arg0) throws Exception {
        MyCalendarType cType = new MyCalendarType();
        cType.day = arg0.get(Calendar.DATE);
        cType.month = arg0.get(Calendar.MONTH);
        cType.year = arg0.get(Calendar.YEAR);
        return cType;
    }

    public Calendar marshal(MyCalendarType arg0) throws Exception {
        return new GregorianCalendar(arg0.year, arg0.month, arg0.day);
    }
}
