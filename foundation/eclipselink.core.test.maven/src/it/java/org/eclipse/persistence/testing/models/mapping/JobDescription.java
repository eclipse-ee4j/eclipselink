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
package org.eclipse.persistence.testing.models.mapping;

import java.util.*;
import java.io.*;

public class JobDescription implements Serializable {
    String description;
    String title;
    float salary;
    Date startDate;
    Date endDate;

    public JobDescription() {
        setTitle("Uknown title");
        setDescription("Uknown job description.");
        setSalary((float)0.0);
    }

    public static JobDescription example1() {
        GregorianCalendar calendar = new GregorianCalendar();

        JobDescription newJob = new JobDescription();

        newJob.setTitle("Instructor");
        newJob.setDescription("Deliver course materials to clients and assist with any hands on sessions");
        newJob.setSalary((float)45000.0);
        newJob.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 365);// advance the date by one year
        newJob.setEndDate(calendar.getTime());

        return newJob;
    }

    public static JobDescription example2() {
        GregorianCalendar calendar = new GregorianCalendar();

        JobDescription newJob = new JobDescription();

        newJob.setTitle("Software Developer");
        newJob.setDescription("Participate in the design and development of the TopLink product");
        newJob.setSalary((float)50000.0);
        newJob.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 365);// advance the date by one year
        newJob.setEndDate(calendar.getTime());

        return newJob;
    }

    public static JobDescription example3() {
        GregorianCalendar calendar = new GregorianCalendar();

        JobDescription newJob = new JobDescription();

        newJob.setTitle("Systems Administrator");
        newJob.setDescription("Maintain existing networks and upkeep of workstations");
        newJob.setSalary((float)40000.0);
        newJob.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 365);// advance the date by one year
        newJob.setEndDate(calendar.getTime());

        return newJob;
    }

    public static JobDescription example4() {
        GregorianCalendar calendar = new GregorianCalendar();

        JobDescription newJob = new JobDescription();

        newJob.setTitle("Receptionist");
        newJob.setDescription("Answer any incoming calls and other administrative tasks");
        newJob.setSalary((float)25000.0);
        newJob.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 365);// advance the date by one year
        newJob.setEndDate(calendar.getTime());

        return newJob;
    }

    public static JobDescription example5() {
        GregorianCalendar calendar = new GregorianCalendar();

        JobDescription newJob = new JobDescription();

        newJob.setTitle("Janitor");
        newJob.setDescription("Clean the office");
        newJob.setSalary((float)5000.0);
        newJob.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 365);// advance the date by one year
        newJob.setEndDate(calendar.getTime());

        return newJob;
    }

    public String getDescription() {
        return description;
    }

    public Date getEndDate() {
        return endDate;
    }

    public float getSalary() {
        return salary;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public void setEndDate(Date newEndDate) {
        endDate = newEndDate;
    }

    public void setSalary(float newSalary) {
        salary = newSalary;
    }

    public void setStartDate(Date newStartDate) {
        startDate = newStartDate;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String toString() {
        return "JobDescription(" + getTitle() + ")";
    }
}
