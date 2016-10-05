/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Tomas Kraus, Peter Benedikovic - initial API and implementation
 *     08/29/2016 Jody Grassel
 *       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
 ******************************************************************************/
package org.eclipse.persistence.internal.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * Java VM version storage class. Used for version numbers retrieved from
 * Java VM version string. Stored version is in
 * <code>&lt;major&gt;.&lt;minor&lt;.&lt;revision&lt;_&lt;update&lt;></code>
 * format.
 * @author Tomas Kraus, Peter Benedikovic
 */
public final class JavaVersion {

    /** JavaEE platform version elements separator character. */
    public static final char SEPARATOR = '.';

    /** JavaEE platform patch version element separator character. */
    public static final char PATCH_SEPARATOR = '_';

    /** Java VM version system property name. */
    private static final String VM_VERSION_PROPERTY = "java.version";

    /**
     * Java VM version output regular expression pattern.
     * Regular expression contains tokens to read individual version number
     * components. Expected input is string like
     * <code>java version "1.6.0_30"</code>.
     */
    private static final String VM_VERSION_PATTERN =
            "[^0-9]*([0-9]+)\\.([0-9]+)(?:\\.([0-9]+)(?:[-_\\.]([0-9]+)){0,1}){0,1}[^0-9]*";

    /** Number of <code>Matcher</code> groups (REGEX tokens) expected in Java VM
     *  version output. */
    private static final int VM_MIN_VERSION_TOKENS = 2;

    /**
     * Retrieves Java VM version {@see String} from JDK system property.
     * @return Java VM version {@see String} from JDK system property.
     */
    public static String vmVersionString() {
        return PrivilegedAccessHelper.getSystemProperty(VM_VERSION_PROPERTY);
    }

    /**
     * Java VM version detector.
     * Retrieves Java VM version from JDK system property. Version string should
     * look like:<ul>
     * <li/><code>"MA.MI.RE_PA"</code>
     * </ul>
     * Where<ul>
     * <li/>MA is major version number,
     * <li/>MI is minor version number,
     * <li/>RE is revision number and
     * <li/>PA is patch update number,
     * </ul>
     * Label <code>java version</code> is parsed as non case sensitive.
     */
    public static JavaVersion vmVersion() {
        final String version = vmVersionString();
        final Pattern pattern = Pattern.compile(VM_VERSION_PATTERN);
        final Matcher matcher = pattern.matcher(version);
        int major = 0, minor = 0, revision = 0, patch = 0;
        if (matcher.find()) {
            final int groupCount = matcher.groupCount();
            if (groupCount >= VM_MIN_VERSION_TOKENS) {
                // [0-9]+ REGEX pattern is validating numbers in tokens.
                // NumberFormatException can't be thrown in any way.
                major = Integer.parseInt(matcher.group(1));
                minor = Integer.parseInt(matcher.group(2));
                revision = groupCount > 2 && matcher.group(3) != null
                        ? Integer.parseInt(matcher.group(3)) : 0;
                patch = groupCount > 3 && matcher.group(4) != null
                        ? Integer.parseInt(matcher.group(4)) : 0;
            }
        }
        return new JavaVersion(major, minor, revision, patch);
    }

    /** Major version number. */
    private final int major;

    /** Minor version number. */
    private final int minor;

    /** Revision number. */
    private final int revision;

    /** Patch update number. */
    private final int patch;

    /**
     * Constructs an instance of Java VM version number.
     * @param major    Major version number.
     * @param minor    Minor version number.
     * @param revision Revision number.
     * @param patch    Patch update number.
     */
    public JavaVersion(final int major, final int minor, final int revision,
            final int patch) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.patch = patch;
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
     * Get revision number.
     * @return Revision number.
     */
    public final int getRevision() {
        return revision;
    }

    /**
     * Get patch update number.
     * @return Patch update number.
     */
    public final int getPatch() {
        return patch;
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
                this.minor < version.minor ? -1 :
                this.revision > version.revision ? 1 :
                this.revision < version.revision ? -1 :
                this.patch > version.patch ? 1 :
                this.patch < version.patch ? -1 : 0;
    }

    /**
     * Return <code>String</code> representation of Java VM version object.
     * @return Java VM version string.
     */
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(12);
        sb.append(major);
        sb.append(SEPARATOR);
        sb.append(minor);
        sb.append(SEPARATOR);
        sb.append(revision);
        sb.append(PATCH_SEPARATOR);
        sb.append(patch);
        return sb.toString();
    }

    /**
     * Return {@link JavaSEPlatform} matching this Java SE version.
     * @return {@link JavaSEPlatform} matching this Java SE version.
     */
    public final JavaSEPlatform toPlatform() {
        return JavaSEPlatform.toValue(major, minor);
    }

}
