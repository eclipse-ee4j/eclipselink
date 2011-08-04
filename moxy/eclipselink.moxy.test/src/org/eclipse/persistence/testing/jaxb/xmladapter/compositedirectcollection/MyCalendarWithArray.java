/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - August 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarAdapter;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarType;

@XmlRootElement(name="mycalendar")
public class MyCalendarWithArray {
    @XmlElement(name="date")
    @XmlJavaTypeAdapter(MyCalendarAdapter.class)
    public MyCalendarType[] date;
    
    public boolean equals(Object obj) {
        if (!(obj instanceof MyCalendarWithArray)) {
            return false;
        }
        MyCalendarWithArray myCal = (MyCalendarWithArray) obj;
        for (int i = 0; i < date.length; i++) {
            if(!(date[i].equals(myCal.date[i]))) {
                return false;
            }
        }
        return true;
    }
}