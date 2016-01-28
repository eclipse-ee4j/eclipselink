/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Tomas Kraus, Peter Benedikovic - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.helper;

import java.security.AccessController;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;

/**
 * Java version storage class. Used for version numbers retrieved from
 * Java specification version string. Stored version is in
 * <code>&lt;major&gt;.&lt;minor&gt;</code> format.
 * @author Tomas Kraus, Peter Benedikovic
 */
public final class JavaVersion {

    /** JavaEE platform version elements separator character. */
    public static final char SEPARATOR = '.';

    /** JavaEE platform patch version element separator character. */
    public static final char PATCH_SEPARATOR = '_';

    /** Java VM version system property name. */
    private static final String VM_VERSION_PROPERTY = "java.specification.version";

    /**
     * Java specification version output regular expression pattern.
     * Regular expression contains tokens to read individual version number
     * components. Expected input is string like
     * <code>java version "1.6"</code> or <code>9</code>.
     */
    private static final String VM_VERSION_PATTERN =
            "[^0-9]*([0-9]+)(\\.([0-9]+))*";

    /** Number of <code>Matcher</code> groups (REGEX tokens) expected in Java VM
     *  version output. */
    private static final int VM_MIN_VERSION_TOKENS = 1;

    /**
     * Retrieves Java specification version {@see String} from JDK system property.
     * @return Java specification version {@see String} from JDK system property.
     */
    public static String vmVersionString() {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedGetSystemProperty(VM_VERSION_PROPERTY));
        }
        return System.getProperty(VM_VERSION_PROPERTY);
    }

    /**
     * Java specification version detector.
     * Retrieves Java specification version from JDK system property. Version string should
     * look like:<ul>
     * <li/><code>"MA.MI"</code>
     * </ul>
     * Where<ul>
     * <li/>MA is major version number,
     * <li/>MI is minor version number
     * </ul>
     * Label <code>java version</code> is parsed as non case sensitive.
     */
    public static JavaVersion vmVersion() {
        final String version = vmVersionString();
        final Pattern pattern = Pattern.compile(VM_VERSION_PATTERN);
        final Matcher matcher = pattern.matcher(version);
        int major = 0, minor = 0;
        if (matcher.find()) {
            major = Integer.parseInt(matcher.group(1));
            String min = matcher.group(VM_MIN_VERSION_TOKENS + 2);
            minor = min != null ? Integer.parseInt(min) : 0;
        }
        return new JavaVersion(major, minor);
    }

    /** Major version number. */
    private final int major;

    /** Minor version number. */
    private final int minor;

    /**
     * Constructs an instance of Java specification version number.
     * @param major    Major version number.
     * @param minor    Minor version number.
     */
    public JavaVersion(final int major, final int minor) {
        this.major = major;
        this.minor = minor;
    }

    /**
     * Get major version number.
     * @return Major version number.
     */
    public final int getMajor() {
        return major;
    }

    /**
     * Get minor version number.
     * @return Minor version number.
     */
    public final int getMinor() {
        return minor;
    }

    /**
     * Compares this <code>JavaVersion</code> object against another one.
     * @param version <code>JavaVersion</code> object to compare with
     *                <code>this</code> object.
     * @return Compare result:<ul>
     *         <li/>Value <code>1</code> if <code>this</code> value
     *         is greater than supplied <code>version</code> value.
     *         <li/>Value <code>-1</code> if <code>this</code> value
     *         is lesser than supplied <code>version</code> value.
     *         <li/>Value <code>0</code> if both <code>this</code> value
     *         and supplied <code>version</code> values are equal.
     *         </ul>
     */
    public final int comapreTo(final JavaVersion version) {
        return this.major > version.major ? 1 :
                this.major < version.major ? -1 :
                this.minor > version.minor ? 1 :
                this.minor < version.minor ? -1 : 0;
    }

    /**
     * Return <code>String</code> representation of Java VM version object.
     * @return Java VM version string.
     */
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(3);
        sb.append(major);
        sb.append(SEPARATOR);
        sb.append(minor);
        return sb.toString();
    }

    /**
     * Return {@link JavaSEPlatform} matching this Java SE specification version.
     * @return {@link JavaSEPlatform} matching this Java SE specification version.
     */
    public final JavaSEPlatform toPlatform() {
        return JavaSEPlatform.toValue(major, minor);
    }

}
