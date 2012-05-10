/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.datetime;

import java.sql.Time;

import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

public class DateTimePopulator {
    public DateTimePopulator() {
    }

    public void persistExample(Session session)
    {        
        Vector allObjects = new Vector();   
        allObjects.add(example1());
        allObjects.add(example2());
        allObjects.add(example3());
        allObjects.add(example4());
        
        UnitOfWork unitOfWork = session.acquireUnitOfWork();        
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();        
    }
    
    public DateTime example1() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(2001, 6, 1, 3, 45, 32);
        cal.set(Calendar.MILLISECOND, 87);
        
        return buildAttributes(cal);
    }

    public DateTime example2() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        
        return buildAttributes(cal);
    }

    public DateTime example3() {
        Calendar cal = Calendar.getInstance(); 
        
        return buildAttributes(cal);
    }

    public DateTime example4() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1999, 0, 1, 23, 45, 32);
        cal.set(Calendar.MILLISECOND, 234);
        
        return buildAttributes(cal);
    }

    @SuppressWarnings("deprecation")
    public DateTime buildAttributes(Calendar cal) {
        DateTime dateTime = new DateTime();
        long time = cal.getTime().getTime();;
        
        dateTime.setDate(new java.sql.Date(time));        
        dateTime.setTime(new Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)));        
        dateTime.setTimestamp(new Timestamp(time));
        dateTime.setUtilDate(new Date(time));
        dateTime.setCalendar(cal);
        
        return dateTime;        
    }
}
