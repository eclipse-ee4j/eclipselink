/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.aggregate;

import java.sql.*;
import org.eclipse.persistence.sessions.*;

/**
 * Insert the type's description here.
 * Creation date: (6/7/00 9:56:29 AM)
 * @author: Administrator
 */
public class Job {
    String title;
    Time[] normalHours;

    /**
     * Job constructor comment.
     */
    public Job() {
        super();
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 11:24:58 AM)
     * @return org.eclipse.persistence.testing.models.aggregate.Job
     */
    public static Job example1() {
        Job job1 = new Job();
        job1.setTitle("Crane Operator");

        /*
            Time[] normalHours = new Time[2];
            normalHours[0] = new Time(5,30,15);
            normalHours[1] = new Time(21,9,10);
            job1.setNormalHours(normalHours);
        */
        job1.setNormalHours(new Time[2]);
        return job1;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:09:23 AM)
     * @return java.sql.Time
     */
    public Time getEndTime() {
        return getNormalHours()[1];
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:01:55 AM)
     * @return java.sql.Time[]
     */
    public Time[] getNormalHours() {
        return normalHours;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:05:48 AM)
     * @return java.sql.Time[]
     * @param row org.eclipse.persistence.sessions.DatabaseRecord
     */
    public Time[] getNormalHoursFromRow(Record row) {
        Time[] hours = new Time[2];
        hours[0] = (Time)row.get("START_TIME");
        hours[1] = (Time)row.get("END_TIME");
        return hours;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:08:30 AM)
     * @return java.sql.Time
     */
    public Time getStartTime() {
        return getNormalHours()[0];
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:10:52 AM)
     * @return java.lang.String
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:02:35 AM)
     * @param theNormalHours java.sql.Time[]
     */
    public void setNormalHours(Time[] theNormalHours) {
        this.normalHours = theNormalHours;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:10:17 AM)
     * @param theTitle java.lang.String
     */
    public void setTitle(String theTitle) {
        this.title = theTitle;
    }
}
