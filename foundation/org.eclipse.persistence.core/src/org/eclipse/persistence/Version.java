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
 *     egwin  - Changed buildNumber to buildDate. Added buildRevision,
 *              buildType, getBuildDate(), getBuildRevision(), getBuildType(),
 *              getVersionString(), printVersionString(), and main()  
 ******************************************************************************/
package org.eclipse.persistence;

/**
 * This class stores variables for the version and build numbers that are used in printouts and exceptions.
 *
 * @author Eric Gwin
 * @since 1.0,
 */
public class Version {
    // The current copyright info for EclipseLink.
    private static final String CopyrightString = "Copyright (c) 1998, 2013 Oracle.  All rights reserved.";

    // The current version of EclipseLink.
    // This will be used by all product components and included in exceptions.
    private static String product = "Eclipse Persistence Services";
    // A three part version number (major.minor.service)
    private static final String version = "@VERSION@";
    // A string that describes this build i.e.( vYYYYMMDD-HHMM, etc.)
    private static final String qualifier = "@QUALIFIER@";
    // Should be in the format YYYYMMDD
    private static final String buildDate = "@BUILD_DATE@";
    // Should be in the format HHMM
    private static final String buildTime = "@BUILD_TIME@";
    // revision of source from the repository
    private static final String buildRevision = "@BUILD_REVISION@";
    // Typically SNAPSHOT, Milestone name (M1,M2,etc), or RELEASE
    private static final String buildType = "@BUILD_TYPE@";
 
    /** Keep track of JDK version in order to make some decisions about data structures. **/
    public static final int JDK_VERSION_NOT_SET = 0;
    public static final int JDK_1_5 = 1;
    public static final int JDK_1_6 = 2;
    public static int JDK_VERSION = JDK_VERSION_NOT_SET;

    public static String getVersionString ( ) {
        String verString;

        verString = getVersion() + "." + getQualifier();
        return( verString );
    }

    public static String getProduct() {
        return product;
    }

    public static void setProduct(String ProductName) {
        product = ProductName;
    }

    public static String getVersion() {
        return version;
    }

    public static String getQualifier() {
        return qualifier;
    }

    public static String getBuildNumber() {
        return getBuildDate();
    }
    
    public static String getBuildDate() {
        return buildDate;
    }

    public static String getBuildTime() {
        return buildTime;
    }

    public static String getBuildRevision() {
        return buildRevision;
    }

    public static String getBuildType() {
        return buildType;
    }

    /**
     * INTERNAL:
     * Return the JDK version we are using.
     */
    public static int getJDKVersion() {
        if (JDK_VERSION == JDK_VERSION_NOT_SET) {
            String version = System.getProperty("java.version");
            if ((version != null) && version.startsWith("1.5")) {
                useJDK15();
            } else {
                useJDK16();
            }
        }
        return JDK_VERSION;
    }


    public static void useJDK15() {
        JDK_VERSION = JDK_1_5;
    }

    public static void useJDK16() {
        JDK_VERSION = JDK_1_6;
    }

    public static boolean isJDK15() {
        return getJDKVersion() == JDK_1_5;
    }

    public static boolean isJDK16() {
        return getJDKVersion() == JDK_1_6;
    }

    public static void printVersion ( ) {
        System.out.println( getVersionString() );          
    }

    public static void main ( String[] args ) {
        System.out.println(
             "\n" + getProduct() + " (EclipseLink)"
             + "\n   Build Version:   " + getVersionString()
             + "\n   Build Qualifier: " + getQualifier()
             + "\n   Build Date:      " + getBuildDate()
             + "\n   Build Time:      " + getBuildTime()
             + "\n   SVN Revision:    " + getBuildRevision()
        );
    }
}
