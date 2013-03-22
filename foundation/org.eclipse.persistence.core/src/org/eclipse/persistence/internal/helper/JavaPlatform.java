/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.sql.SQLException;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 *  INTERNAL:
 *  JavaPlatform abstracts the version of the JDK we are using.  It allows any operation
 *  which is dependent on JDK version to be called from a single place and then delegates
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
            if (Version.isJDK16()) {
                try {
                    Class platformClass = null;
                    // use class.forName() to avoid loading the JDK 1.6 class unless it is needed.
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            platformClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName("org.eclipse.persistence.internal.helper.JDK16Platform"));
                        } catch (PrivilegedActionException exception) {
                        }
                    } else {
                        platformClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.internal.helper.JDK16Platform");
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
     *  INTERNAL:
     *  Conform an expression which uses the operator "regexp" for an in-memory query
     *  @return Boolean (TRUE, FALSE, null == unknown)
     */
    public static Boolean conformRegexp(Object left, Object right) {
        return getPlatform().conformRegexp(left, right);
    }

    /**
     * INTERNAL:
     * Indicates whether the passed object implements java.sql.SQLXML introduced in jdk 1.6
     */
    public static boolean isSQLXML(Object object) {
        return getPlatform().isSQLXML(object);
    }

    /**
     * INTERNAL:
     * Casts the passed object to SQLXML and calls getString and free methods
     */
    public static String getStringAndFreeSQLXML(Object sqlXml) throws SQLException { 
        return getPlatform().getStringAndFreeSQLXML(sqlXml);
    }
}
