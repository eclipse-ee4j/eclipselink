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
// dmccann - November 23/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple;

import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class DateAdapter extends XmlAdapter<CustomDateType, Calendar> {
    public DateAdapter() {}

    public CustomDateType marshal(Calendar arg0) throws Exception {
        CustomDateType cType = new CustomDateType();
        Calendar newCal = (Calendar) arg0.clone();

        cType.day = newCal.get(Calendar.DATE);
        cType.month = newCal.get(Calendar.MONTH);
        cType.year = newCal.get(Calendar.YEAR);
        return cType;
    }

    public Calendar unmarshal(CustomDateType arg0) throws Exception {
        return new GregorianCalendar(arg0.year, arg0.month, arg0.day);
    }
}
