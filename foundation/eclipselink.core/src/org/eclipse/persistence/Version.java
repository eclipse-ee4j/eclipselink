/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence;

/**
 * This class stores variables for the version and build numbers that are used in printouts and exceptions.
 *
 * @author Eric Gwin
 * @since 1.0,
 */
public class Version {
    // The current copyright info for TopLink. 
    private static final String CopyrightString = "Copyright (c) 1998, 2007, Oracle.  All rights reserved.";

    // The current version of TopLink. 
    // This will be used by all product components and included in exceptions.
    private static String product = "Eclipse Persistence Services";
    private static final String version = "@VERSION@";
    private static final String buildNumber = "@BUILD_NUMBER@";

    /** Keep track of JDK version in order to make some decisions about datastructures. **/
    public static final int JDK_VERSION_NOT_SET = 0;
    public static final int JDK_1_3 = 1;
    public static final int JDK_1_4 = 2;
    public static final int JDK_1_5 = 3;
    public static int JDK_VERSION = JDK_VERSION_NOT_SET;

    public static String getProduct() {
        return product;
    }

    public static void setProduct(String ProductName) {
        product = ProductName;
    }

    public static String getVersion() {
        return version;
    }

    public static String getBuildNumber() {
        return buildNumber;
    }

    /**
     * INTERNAL:
     * Return the JDK version we are using.
     */
    public static int getJDKVersion() {
        if (JDK_VERSION == JDK_VERSION_NOT_SET) {
            String version = System.getProperty("java.version");
            if ((version != null) && version.startsWith("1.3")) {
                useJDK13();
            } else if ((version != null) && version.startsWith("1.4")) {
                useJDK14();
            } else {
                useJDK15();
            }
        }
        return JDK_VERSION;
    }

    public static void useJDK13() {
        JDK_VERSION = JDK_1_3;
    }

    public static void useJDK14() {
        JDK_VERSION = JDK_1_4;
    }

    public static void useJDK15() {
        JDK_VERSION = JDK_1_5;
    }

    public static boolean isJDK13() {
        return getJDKVersion() == JDK_1_3;
    }

    public static boolean isJDK14() {
        return getJDKVersion() == JDK_1_4;
    }

    public static boolean isJDK15() {
        return getJDKVersion() == JDK_1_5;
    }
}
