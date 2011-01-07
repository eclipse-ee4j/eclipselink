/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - July 21/2009 - 2.0 - Initial implementation
 ******************************************************************************/
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
