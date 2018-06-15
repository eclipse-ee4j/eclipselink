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
// dmccann - July 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class DateAdapter extends XmlAdapter<CustomDateType, Calendar> {
    public DateAdapter() {}

    public CustomDateType marshal(Calendar arg0) throws Exception {
        CustomDateType cType = new CustomDateType();
        cType.day = arg0.get(Calendar.DATE);
        cType.month = arg0.get(Calendar.MONTH);
        cType.year = arg0.get(Calendar.YEAR);
        return cType;
    }

    public Calendar unmarshal(CustomDateType arg0) throws Exception {
        return new GregorianCalendar(arg0.year, arg0.month, arg0.day);
    }
}
