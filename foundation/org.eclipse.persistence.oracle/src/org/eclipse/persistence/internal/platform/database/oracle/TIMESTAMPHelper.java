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
    public static TIMESTAMPTZ buildTIMESTAMPTZ(Calendar cal, Connection con, boolean shouldPrintCalendar) throws SQLException {
        //Bug5614674.  It used to be a driver bug and Helper.printCalendar(cal, false) was used to make it work.  It has been fixed in 11.  Separate the newer version from the old ones.
        if (shouldPrintCalendar) {
            return new TIMESTAMPTZ(con, Helper.printCalendar(cal, false), cal);
        } else {
            return new TIMESTAMPTZ(con, new Timestamp(cal.getTimeInMillis()), cal);
        }
    }

    /**
     * Build a calendar from TIMESTAMPTZWrapper. 
     */
    public static Calendar buildCalendar(TIMESTAMPTZWrapper timestampTZ) throws SQLException{
        Timestamp ts = timestampTZ.getTimestamp();
        TimeZone tz = timestampTZ.getTimeZone();

        Calendar gCal;
        if(timestampTZ.isTimestampInGmt()) {
            gCal = Calendar.getInstance(tz);
            gCal.setTime(ts);
        } else {
            gCal = Calendar.getInstance();
            gCal.setTime(ts);
            gCal.getTimeZone().setID(tz.getID());
            gCal.getTimeZone().setRawOffset(tz.getRawOffset());
        }
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
        if(timestampLTZ.isLtzTimestampInGmt()) {
            gCal.setTimeInMillis(timestampLTZ.getTimestamp().getTime());
        } else {
            Calendar localCalendar = Helper.allocateCalendar();
            localCalendar.setTime(timestampLTZ.getTimestamp());
            gCal.set(localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar.DATE), localCalendar.get(Calendar.HOUR_OF_DAY), localCalendar.get(Calendar.MINUTE), localCalendar.get(Calendar.SECOND));
            Helper.releaseCalendar(localCalendar);
        }
        gCal.set(Calendar.MILLISECOND, timestampLTZ.getTimestamp().getNanos() / 1000000);

        return gCal;
    }


    /**
     * Build a calendar string based on the calendar fields.
     * If the daylight savings time should be printed and the zone is in daylight savings time, 
     * print the short representation of daylight savings from the calendar's timezone data. 
     */
    public static String printCalendar(Calendar calendar) {
        if (calendar == null) {
            return "null";
        }
        StringWriter writer = new StringWriter();
        writer.write(Helper.printCalendar(calendar, false));
        writer.write(" ");
        writer.write(calendar.getTimeZone().getID());
        // If we should print daylight savings and the zone is reported to be using daylight time, 
        // write the short representation of the daylight time in the writer.
        if (shouldAppendDaylightTime(calendar)) {
            writer.write(" ");
            writer.write(calendar.getTimeZone().getDisplayName(true, TimeZone.SHORT));
        }
        return writer.toString();
    }
    
    /**
     * Return true if the calendar supports and is in daylight time
     * (according to its timezone), false otherwise
     */
    public static boolean shouldAppendDaylightTime(Calendar calendar) {
        if (calendar == null) {
            return false;
        }
        TimeZone zone = calendar.getTimeZone();
        return zone.useDaylightTime() && zone.inDaylightTime(calendar.getTime());
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
            String offset = Helper.buildZeroPrefix(hourOffset, 2) + ":" + Helper.buildZeroPrefixWithoutSign(minuteOffset, 2);
            regionName = "GMT" + offset;
        }
        return TimeZone.getTimeZone(regionName);
    }
}
