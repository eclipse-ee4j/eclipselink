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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.datetime;

import java.lang.reflect.Method;
import java.sql.Time;

import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
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

        // Bug 387491 - Three JUnitJPQLDateTimeTestSuite tests fail with Oracle jdbc 12.1 driver
        // Starting with Oracle jdbc 12.1 Statement.setDate no longer truncates time component of sql.Date.
        // The following code makes Oracle9Platform to do that by setting shouldTruncateDate flag to "true".
        boolean hasSetTruncateDate = false;
        if (session.getPlatform().isOracle9()) {
            try {
                Class clazz = PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
                Method getDriverVersionMethod = PrivilegedAccessHelper.getMethod(clazz, "getDriverVersion", null, false);
                String driverVersion = (String) PrivilegedAccessHelper.invokeMethod(getDriverVersionMethod, session.getPlatform(), null);
                if (Helper.compareVersions(driverVersion, "12.1") >= 0) {
                    Method shouldTruncateDateMethod = PrivilegedAccessHelper.getMethod(clazz, "shouldTruncateDate", null, false);
                    boolean shouldTruncateDate = (Boolean) PrivilegedAccessHelper.invokeMethod(shouldTruncateDateMethod, session.getPlatform(), null);
                    if (!shouldTruncateDate) {
                        Method setShouldTruncateDateMethod = PrivilegedAccessHelper.getMethod(clazz, "setShouldTruncateDate", new Class[]{boolean.class}, false);
                        PrivilegedAccessHelper.invokeMethod(setShouldTruncateDateMethod, session.getPlatform(), new Object[]{true});
                        hasSetTruncateDate = true;
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("Failed oracle9Platform.setShouldTruncateDate(true)", ex);
            }
        }

        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();

        if (hasSetTruncateDate) {
            // Now setting shouldTruncateDate flag back to its original value "false".
            try {
                Class clazz = PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
                Method setShouldTruncateDateMethod = PrivilegedAccessHelper.getMethod(clazz, "setShouldTruncateDate", new Class[]{boolean.class}, false);
                PrivilegedAccessHelper.invokeMethod(setShouldTruncateDateMethod, session.getPlatform(), new Object[]{false});
            } catch (Exception ex) {
                throw new RuntimeException("Failed oracle9Platform.setShouldTruncateDate(false)", ex);
            }
        }
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
