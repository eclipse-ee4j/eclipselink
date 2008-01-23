/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import java.util.*;
import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 *  INTERNAL:
 *  JavaPlatform abstracts the version of the JDK we are using.  It allows any operation
 *  which is dependant on JDK version to be called from a single place and then delegates
 *  the call to its JDKPlatform
 *  @see JDPlatform
 *  @author Tom Ware
 */
public class JavaPlatform {
    protected static JDKPlatform platform = null;

    /**
     *  INTERNAL:
     *  Get the version of JDK being used from the Version class.
     *  @return JDKPlatform a platform appropriate for the version of JDK being used.
     */
    protected static JDKPlatform getPlatform() {
        if (platform == null) {
            if (Version.isJDK15()) {
                try {
                    Class platformClass = null;
                    // use class.forName() to avoid loading the JDK 1.5 class unless it is needed.
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            platformClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName("org.eclipse.persistence.internal.helper.JDK15Platform"));
                        } catch (PrivilegedActionException exception) {
                        }
                    } else {
                        platformClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.internal.helper.JDK15Platform");
                    }                  
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            platform = (JDKPlatform)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(platformClass));
                        } catch (PrivilegedActionException exception) {
                        }
                    } else {
                        platform = (JDKPlatform)PrivilegedAccessHelper.newInstanceFromClass(platformClass);
                    }      
                } catch (Exception exception) {
                }
            }
            if (platform == null) {
                platform = new JDK15Platform();
            }
        }
        return platform;
    }

    /**
     *  INTERNAL:
     *  Conform an expression which uses the operator "like" for an in-memory query
     *  @return Boolean (TRUE, FALSE, null == unknown)
     */
    public static Boolean conformLike(Object left, Object right) {
        return getPlatform().conformLike(left, right);
    }

    /**
     * INTERNAL:
     * Get the milliseconds from a Calendar.
     * @param calendar the instance of calendar to get the millis from
     * @return long the number of millis
     */
    public static long getTimeInMillis(Calendar calendar) {
        return getPlatform().getTimeInMillis(calendar);
    }

    /**
     * INTERNAL:
     * Get the Map to store the query cache in
     */
    public static Map getConcurrentMap() {
        return getPlatform().getConcurrentMap();
    }

    /**
     * INTERNAL:
     * Set the milliseconds for a Calendar.
     */
    public static void setTimeInMillis(java.util.Calendar calendar, long millis) {
        getPlatform().setTimeInMillis(calendar, millis);
    }

    /**
     *  INTERNAL:
     *  Set the cause of an exception.  This is useful for JDK 1.4 exception chaining
     *  @param java.lang.Throwable the exception to set the cause for
     *  @param java.lang.Throwable the cause of this exception
     */
    public static void setExceptionCause(Throwable exception, Throwable cause) {
        getPlatform().setExceptionCause(exception, cause);
    }

    /**
     * INTERNAL
     * return a boolean which determines where TopLink should include the TopLink-stored
     * Internal exception in it's stack trace.  For JDK 1.4 VMs with exception chaining
     * the Internal exception can be redundant and confusing.
     * @return boolean
     */
    public static boolean shouldPrintInternalException() {
        return getPlatform().shouldPrintInternalException();
    }
}
