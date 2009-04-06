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
    private static final String CopyrightString = "Copyright (c) 1998, 2008, Oracle.  All rights reserved.";

    // The current version of EclipseLink.
    // This will be used by all product components and included in exceptions.
    private static String product = "Eclipse Persistence Services";
    private static final String version = "@VERSION@";
    // Should be in the format YYYYMMDD
    private static final String buildDate = "@BUILD_DATE@";
    private static final String buildTime = "@BUILD_TIME@";
    private static final String buildRevision = "@BUILD_REVISION@";
    private static final String buildType = "@BUILD_TYPE@";
 
    /** Keep track of JDK version in order to make some decisions about data structures. **/
    public static final int JDK_VERSION_NOT_SET = 0;
    public static final int JDK_1_5 = 1;
    public static int JDK_VERSION = JDK_VERSION_NOT_SET;

    public static String getVersionString ( ) {
        String verString;
        if( getBuildRevision()=="NA" ) {
            verString = getVersion() + ".qualifier";
        }else {
            verString = getVersion() + ".v" + getBuildDate() + "-r" + getBuildRevision();
        }
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
            useJDK15();
        }
        return JDK_VERSION;
    }


    public static void useJDK15() {
        JDK_VERSION = JDK_1_5;
    }

    public static boolean isJDK15() {
        return getJDKVersion() == JDK_1_5;
    }

    public static void printVersion ( ) {
        System.out.println( getVersionString() );          
    }

    public static void main ( String[] args ) {
        if( getBuildType()=="RELEASE" ) {
            System.out.println( 
                "\n" + getProduct() + " (EclipseLink)"
                + "\n   Release Version: " + getVersionString() 
                + "\n   Build Date:      " + getBuildDate()
                + "\n   SVN Revision:    " + getBuildRevision()
            );
        }else {
            System.out.println(
                "\n" + getProduct() + " (EclipseLink)"
                + "\n   Build Version: " + getVersionString()
                + "\n   Build Date:    " + getBuildDate()
                + "\n   SVN Revision:  " + getBuildRevision()
           );
        }        
    }
}
