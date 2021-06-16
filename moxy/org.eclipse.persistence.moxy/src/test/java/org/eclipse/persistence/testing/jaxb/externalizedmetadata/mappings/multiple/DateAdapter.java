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
// dmccann - November 23/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple;

import java.util.Calendar;
import java.util.GregorianCalendar;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
