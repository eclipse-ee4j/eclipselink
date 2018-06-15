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
//     egwin  - Changed buildNumber to buildDate. Added buildRevision,
//              buildType, getBuildDate(), getBuildRevision(), getBuildType(),
//              getVersionString(), printVersionString(), and main()
package org.eclipse.persistence;

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

    /**
     * Returns version {@link String} containing three part version number
     * and build qualifier.
     * @return Version {@link String}.
     */
    public static String getVersionString() {
        StringBuilder sb = new StringBuilder(version.length() + 1 + qualifier.length());
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

    public static void printVersion() {
        System.out.println(getVersionString());
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.print(getProduct()); System.out.println(" (EclipseLink)");
        System.out.print("   Build Version:   "); System.out.println(getVersionString());
        System.out.print("   Build Qualifier: "); System.out.println(getQualifier());
        System.out.print("   Build Date:      "); System.out.println(getBuildDate());
        System.out.print("   Build Time:      "); System.out.println(getBuildTime());
        System.out.print("   Build Revision:  "); System.out.println(getBuildRevision());
    }
}
