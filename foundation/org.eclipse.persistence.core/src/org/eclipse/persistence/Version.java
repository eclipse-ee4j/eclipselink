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
//  Oracle - initial API and implementation from Oracle TopLink
//     egwin  - Changed buildNumber to buildDate. Added buildRevision,
//              buildType, getBuildDate(), getBuildRevision(), getBuildType(),
//              getVersionString(), printVersionString(), and main()
//     2 July 2018   Radek Felcman - changed source of build info into version.properties generated during build
package org.eclipse.persistence;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

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

    /**
     * Version numbers separator.
     */
    private static final char SEPARATOR = '.';
    private static final String VERSION_PROPERTIES_FILE = "version.properties";
    private static final String RESOURCE_BUNDLE = "org.eclipse.persistence.internal.localization.i18n.LoggingLocalizationResource";
    private static final String ERROR_MESSAGE_KEY = "eclipselink_version_error";

    private static Properties versionProperties;


    /**
     * Returns version {@link String} containing three part version number
     * and build qualifier.
     *
     * @return Version {@link String}.
     */
    public static String getVersionString() {
        StringBuilder sb = new StringBuilder(getVersion().length() + 1 + getQualifier().length());
        sb.append(getVersion());
        sb.append(SEPARATOR);
        sb.append(getQualifier());
        return sb.toString();
    }

    public static String getProduct() {
        return product;
    }

    public static void setProduct(String ProductName) {
        product = ProductName;
    }

    // A three part version number (major.minor.service)
    public static String getVersion() {
        return getProperty("version");
    }

    //private static final String version = "@VERSION@";
    // A string that describes this build i.e.( vYYYYMMDD-HHMM, etc.)
    public static String getQualifier() {
        return getProperty("qualifier");
    }

    public static String getBuildNumber() {
        return getBuildDate();
    }

    // Should be in the format YYYYMMDD
    public static String getBuildDate() {
        return getProperty("buildDate");
    }

    // Should be in the format HHMM
    public static String getBuildTime() {
        return getProperty("buildTime");
    }

    // revision of source from the repository
    public static String getBuildRevision() {
        return getProperty("buildRevision");
    }

    // Typically SNAPSHOT, Milestone name (M1,M2,etc), or RELEASE
    public static String getBuildType() {
        return getProperty("buildType");
    }

    public static void printVersion() {
        System.out.println(getVersionString());
    }

    public static void main(String[] args) {
        try {
            System.out.println();
            System.out.print(getProduct());
            System.out.println(" (EclipseLink)");
            System.out.print("   Build Version:   ");
            System.out.println(getVersionString());
            System.out.print("   Build Qualifier: ");
            System.out.println(getQualifier());
            System.out.print("   Build Date:      ");
            System.out.println(getBuildDate());
            System.out.print("   Build Time:      ");
            System.out.println(getBuildTime());
            System.out.print("   Build Revision:  ");
            System.out.println(getBuildRevision());
        }catch (Throwable e) {
            AbstractSessionLog.getLog().logThrowable(SessionLog.SEVERE, e);
        }
    }

    private static Properties loadProperties(String propertiesFileName) throws Exception {
        Properties properties = null;
        try (InputStream versionStream = Version.class.getResourceAsStream(propertiesFileName)) {
            properties = new Properties();
            properties.load(versionStream);
        }
        return properties;
    }

    private static String getProperty(String propertyName) {
        try {
            if (versionProperties == null) {
                versionProperties = loadProperties(VERSION_PROPERTIES_FILE);
            }
            return versionProperties.getProperty(propertyName);
        } catch (Exception e) {
            ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, Locale.getDefault());
            throw new RuntimeException(MessageFormat.format(bundle.getString(ERROR_MESSAGE_KEY), VERSION_PROPERTIES_FILE));
        }
    }

}
