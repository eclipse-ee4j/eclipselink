/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - July 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public final class MyCalendarAdapter extends XmlAdapter<Calendar, MyCalendar> {
    public MyCalendarAdapter() {}

    @Override
    public MyCalendar unmarshal(Calendar arg0) throws Exception {
        MyCalendar cType = new MyCalendar();
        cType.day = arg0.get(Calendar.DATE);
        cType.month = arg0.get(Calendar.MONTH);
        cType.year = arg0.get(Calendar.YEAR);
        return cType;
    }

    @Override
    public Calendar marshal(MyCalendar arg0) throws Exception {
        return new GregorianCalendar(arg0.year, arg0.month, arg0.day);
    }
}
