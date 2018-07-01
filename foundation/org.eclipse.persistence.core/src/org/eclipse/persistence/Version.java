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
//     egwin  - Changed buildNumber to buildDate. Added buildRevision,
//              buildType, getBuildDate(), getBuildRevision(), getBuildType(),
//              getVersionString(), printVersionString(), and main()
package org.eclipse.persistence;

import org.eclipse.persistence.internal.helper.JavaSEPlatform;

/**
 * This class stores variables for the version and build numbers that are used
 * in printouts and exceptions.
 *
 * @author Eric Gwin
 * @since 1.0,
 */
public class Version {
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

    /** Version numbers separator. */
    private static final char SEPARATOR = '.';

    // This is replaced by JavaSEPlatform. It's here just because of backward compatibility.
    /**
     * Keep track of JDK version in order to make some decisions about data structures.
     * @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7.
     */
    @Deprecated
    public static final int JDK_VERSION_NOT_SET = 0;
    /** @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7. */
    @Deprecated
    public static final int JDK_1_5 = 1;
    /** @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7. */
    @Deprecated
    public static final int JDK_1_6 = 2;
    /** @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7. */
    @Deprecated
    public static final int JDK_1_7 = 3;
    /** @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7. */
    @Deprecated
    public static final int JDK_1_8 = 4;
    /** @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7. */
    @Deprecated
    public static final int JDK_1_9 = 5;
    /** @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7. */
    @Deprecated
    public static int JDK_VERSION = JDK_VERSION_NOT_SET;

    /**
     * Returns version {@link String} containing three part version number
     * and build qualifier.
     * @return Version {@link String}.
     */
    public static String getVersionString ( ) {
        StringBuilder sb = new StringBuilder(
                version.length() + 1 + qualifier.length());
        sb.append(version);
        sb.append(SEPARATOR);
        sb.append(qualifier);
        return sb.toString();
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
     * @deprecated Use {@code JavaSEPlatform.CURRENT} instead.
     *             Will be removed in 2.7.
     */
    @Deprecated
    public static int getJDKVersion() {
        switch(JavaSEPlatform.CURRENT) {
            case v1_7:
                JDK_VERSION = JDK_1_7;
                break;
            case v1_8:
                JDK_VERSION = JDK_1_8;
                break;
            case v9_0:
                JDK_VERSION = JDK_1_9;
                break;
            default:
                throw new IllegalStateException("Running on unsupported Java SE: "
                        + JavaSEPlatform.CURRENT.toString());
        }
        return JDK_VERSION;
    }

    /**
     * Set 1.5 as current Java SE version.
     * @throws UnsupportedOperationException when invoked because Java SE 1.5
     *         is not supported by current EclipseLink.
     * @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7.
     */
    @Deprecated
    public static void useJDK15() {
        throw new UnsupportedOperationException(
                "Java SE 1.5 is not supported by current EclipseLink.");
    }

    /**
     * Set 1.6 as current Java SE version.
     * @throws UnsupportedOperationException when invoked because Java SE 1.6
     *         is not supported by current EclipseLink.
     * @deprecated Use {@link JavaSEPlatform} instead. Will be removed in 2.7.
     */
    @Deprecated
    public static void useJDK16() {
        throw new UnsupportedOperationException(
                "Java SE 1.6 is not supported by current EclipseLink.");
    }

    // Public API wrapper, use JavaSEPlatform.is(JavaSEPlatform.v1_5)
    // internally.
    /**
     * Check whether we are running on Java SE 1.5.
     * This will always return {@code false} because Java SE 1.5 is not
     * supported by current EclipseLink.
     * @return Value of {@code true} when we do and value of {@code false}
     *         when we do not run on Java SE 1.5.
     */
    public static boolean isJDK15() {
        return JavaSEPlatform.is(JavaSEPlatform.v1_5);
    }

    // Public API wrapper, use JavaSEPlatform.is(JavaSEPlatform.v1_6)
    // internally.
    /**
     * Check whether we are running on Java SE 1.6.
     * This will always return {@code false} because Java SE 1.6 is not
     * supported by current EclipseLink.
     * @return Value of {@code true} when we do and value of {@code false}
     *         when we do not run on Java SE 1.6.
     */
    public static boolean isJDK16() {
        return JavaSEPlatform.is(JavaSEPlatform.v1_6);
    }

    // Public API wrapper, use JavaSEPlatform.is(JavaSEPlatform.v1_7)
    // internally.
    /**
     * Check whether we are running on Java SE 1.7.
     * @return Value of {@code true} when we do and value of {@code false}
     *         when we do not run on Java SE 1.7.
     */
    public static boolean isJDK17() {
        return JavaSEPlatform.is(JavaSEPlatform.v1_7);
    }

    // Public API wrapper, use JavaSEPlatform.is(JavaSEPlatform.v1_8)
    // internally.
    /**
     * Check whether we are running on Java SE 1.8.
     * @return Value of {@code true} when we do and value of {@code false}
     *         when we do not run on Java SE 1.8.
     */
    public static boolean isJDK18() {
        return JavaSEPlatform.is(JavaSEPlatform.v1_8);
    }

    // Public API wrapper, use JavaSEPlatform.is(JavaSEPlatform.v1_9)
    // internally.
    /**
     * Check whether we are running on Java SE 1.9.
     * @return Value of {@code true} when we do and value of {@code false}
     *         when we do not run on Java SE 1.9.
     */
    public static boolean isJDK19() {
        return JavaSEPlatform.is(JavaSEPlatform.v9_0);
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
