/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.platform.database.oracle;

import java.io.StringWriter;
import java.sql.*;
import java.util.*;
import oracle.sql.*;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * Used as a helper class for TIMESTAMP, TIMESTAMPTZ and TIMESTAMPLTZ in oracle9.
 */
public class TIMESTAMPHelper {

    /**
     * This conversion required the use of the literal string to get the same
     * functionality as the native SQL to_timestamp() approach.
     */
    public static TIMESTAMPTZ buildTIMESTAMPTZ(Calendar cal, Connection con) {
        try {
            //Bug5614674.  It used to be a driver bug and Helper.printCalendar(cal, false) was used to make it work.  It has been fixed in 11.  Separate the newer version from the old ones.
            if (con.getMetaData().getDriverVersion().startsWith("9.") || con.getMetaData().getDriverVersion().startsWith("10.")) {
                return new TIMESTAMPTZ(con, Helper.printCalendar(cal, false), cal);
            } else {
                return new TIMESTAMPTZ(con, new Timestamp(cal.getTimeInMillis()), cal);
            }
        } catch (SQLException sqle) {
            return null;
        }
    }

    /**
   * Build a calendar from TIMESTAMPTZWrapper. 
     */
  public static Calendar buildCalendar(TIMESTAMPTZWrapper timestampTZ) throws SQLException{
    Timestamp ts = timestampTZ.getTimestamp();
    TimeZone tz = timestampTZ.getTimeZone();

        Calendar gCal = Calendar.getInstance();
        gCal.setTime(ts);
        gCal.getTimeZone().setID(tz.getID());
        gCal.getTimeZone().setRawOffset(tz.getRawOffset());

        return gCal;
    }

    /**
   * Build a calendar from TIMESTAMPLTZWrapper. 
     */
  public static Calendar buildCalendar(TIMESTAMPLTZWrapper timestampLTZ) throws SQLException{
        Calendar gCal;
        if (timestampLTZ.getZoneId() != null) {
            gCal = Calendar.getInstance(TimeZone.getTimeZone(timestampLTZ.getZoneId()));
        } else {
            gCal = Calendar.getInstance();
        }

        //This is the only way to set time in Calendar.  Passing Timestamp directly to the new 
        //calendar does not work because the GMT time is wrong.
        Calendar localCalendar = Helper.allocateCalendar();
        localCalendar.setTime(timestampLTZ.getTimestamp());
        gCal.set(localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar.DATE), localCalendar.get(Calendar.HOUR_OF_DAY), localCalendar.get(Calendar.MINUTE), localCalendar.get(Calendar.SECOND));
        Helper.releaseCalendar(localCalendar);
        gCal.set(Calendar.MILLISECOND, timestampLTZ.getTimestamp().getNanos() / 1000000);

        return gCal;
    }


    /**
     * Build a calendar string based on the calendar fields.  includeTimeZone indicates
     * if the TimeZone part of the string is required.
     */
    public static String printCalendar(Calendar calendar) {
        if (calendar == null) {
            return "null";
        }
        StringWriter writer = new StringWriter();
        writer.write(Helper.printCalendar(calendar, false));
        writer.write(" ");
        writer.write(calendar.getTimeZone().getID());
        return writer.toString();
    }

    /**
     * Extract TimeZone from TIMESTAMPTZ.
     */
    public static TimeZone extractTimeZone(byte[] bytes) {
        String regionName = null;
        if ((bytes[11] & -128) != 0) {
            int regionCode = (bytes[11] & 127) << 6;
            regionCode += ((bytes[12] & 252) >> 2);
            regionName = new String(ZONEIDMAP.getRegion(regionCode));
        } else {
            int hourOffset = bytes[11] - 20;
            int minuteOffset = bytes[12] - 60;
            String offset = Helper.buildZeroPrefix(hourOffset, 2) + ":" + Helper.buildZeroPrefix(minuteOffset, 2);
            regionName = "GMT" + offset;
        }
        return TimeZone.getTimeZone(regionName);
    }
}
