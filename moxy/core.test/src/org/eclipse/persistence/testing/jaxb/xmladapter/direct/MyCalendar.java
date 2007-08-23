/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="calendar")
public class MyCalendar {
    @XmlElement(name="date")
    @XmlJavaTypeAdapter(MyCalendarAdapter.class)
    public MyCalendarType date;
    
    public boolean equals(Object obj) {
        if (!(obj instanceof MyCalendar)) {
            return false;
        }
        MyCalendarType mcType = ((MyCalendar) obj).date;
        return mcType.equals(date);
    }
}
