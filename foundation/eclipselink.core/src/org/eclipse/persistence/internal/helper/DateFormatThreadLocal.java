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

package org.eclipse.persistence.internal.helper;

// Javase imports
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

// Java extension imports

// EclipseLink imports

/**
 * <p><b>INTERNAL:</b> <code>DateFormatThreadLocal<code> is a thread-safe helper class to handle 
 * DateFormat's
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class DateFormatThreadLocal extends ThreadLocal<DateFormat> {

    protected SimpleDateFormat simpleDateFormat;
    protected TimeZoneHolder timeZoneHolder;
    
    public DateFormatThreadLocal(String formatStr, TimeZoneHolder timeZoneHolder) {
        super();
        simpleDateFormat = new SimpleDateFormat(formatStr);
        this.timeZoneHolder = timeZoneHolder;
    }
  
    @Override
    public DateFormat get() {
        // check to see if the (cached-per-thread) simpleDateFormat should have its
        // timezone set from the timeZoneHolder
        if (timeZoneHolder != null) {
          TimeZone tz = timeZoneHolder.getTimeZone();
          if (tz != null && !simpleDateFormat.getTimeZone().equals(tz)) {
              simpleDateFormat.setTimeZone(tz);
          }
        }
        return super.get();
    }
  
    @Override
    protected DateFormat initialValue() {
        return simpleDateFormat;
    }
}
