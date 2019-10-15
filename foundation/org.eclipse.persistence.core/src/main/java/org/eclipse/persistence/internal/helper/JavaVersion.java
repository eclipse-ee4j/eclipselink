/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Tomas Kraus, Peter Benedikovic - initial API and implementation
package org.eclipse.persistence.internal.helper;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

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
    public static final String VM_VERSION_PROPERTY = "java.specification.version";

    /**
     * Compiled regular expression pattern to read individual version number components.
     * Expected input is string like <code>java version "1.6"</code> or <code>9</code>.
     */
    private static final Pattern VM_VERSION_PATTERN = Pattern.compile(
            "[^0-9]*([0-9]+)(\\.([0-9]+))*"
    );

    /** Number of <code>Matcher</code> groups (REGEX tokens) expected in Java VM
     *  version output. */
    private static final int VM_MIN_VERSION_TOKENS = 1;

    /**
     * Retrieves Java specification version String from JDK system property.
     * @return Java specification version String from JDK system property.
     */
    public static String vmVersionString() {
        return PrivilegedAccessHelper.getSystemProperty(VM_VERSION_PROPERTY);
    }

    // EclipseLink still supports JDK <9 so using Runtime.Version to retrieve
    // current JDK version is optional and can only be done trough reflection calls.
    // TODO: Remove reflection after JDK <9 support is dropped.

    /** JDK 9+ java.lang.Runtime.Version class name. */
    private static final String VERSION_CLASS_NAME = "java.lang.Runtime$Version";

    /** JDK 9+ java.lang.Runtime static version() method name. */
    private static final String RUNTIME_VERSION_METHOD_NAME = "version";

    /**
     * Invoke {@code Runtime#version()} method to retrieve {@code Runtime.Version} instance.
     * @return {@code Runtime.Version} instance for JDK 9 and later or {@code null} otherwise.
     */
    private static Object runtimeVersionObject() {
        try {
            final Method m = Runtime.class.getMethod(RUNTIME_VERSION_METHOD_NAME);
            return m.invoke(null);
        // Do not log, because AbstractSessionLog.getLog() causes cyclic dependency on ClassConstants during class initialization.
        // NoSuchMethodException: JDK <9, can't use java.lang.Runtime.Version.
        } catch (NoSuchMethodException e) {
            return null;
        // Other ReflectiveOperationException should not happen here. Otherwise throw it as RuntimeException.
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Invoke {@code Runtime.Version} method with given name ({@code major} or {@code minor}) to retrieve version numbers.
     * @param vClass {@code Runtime.Version} class.
     * @param vObj {@code Runtime.Version} class instance containing JDK version information.
     * @param name name of {@code Runtime.Version} instance method to invoke.
     */
    private static Integer getRuntimeVersionNumber(final Object vObj, final String name) {
        try {
            final Method m = vObj.getClass().getMethod(name);
            return (Integer) m.invoke(vObj);
        // Do not log, because AbstractSessionLog.getLog() causes cyclic dependency on ClassConstants during class initialization.
        // ReflectiveOperationException should not happen here. Otherwise throw it as RuntimeException.
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Retrieve JDK version numbers from {@code Runtime.Version} instance returned by {@code Runtime#version()} method.
     * This works only for JDK 9 and later.
     * @return Current JDK version for JDK 9 and later or {@code null} otherwise or when any problem with version retrieval happened.
     */
    private static JavaVersion runtimeVersion() {
        final Object vObj = runtimeVersionObject();
        if (vObj == null) {
            return null;
        }
        final Integer major = getRuntimeVersionNumber(vObj, "major");
        final Integer minor = getRuntimeVersionNumber(vObj, "minor");
        if (major != null && minor != null) {
            return new JavaVersion(major, minor);
        }
        return null;
    }

    /**
     * Parse Java specification version from JDK system property provided as an argument.
     * Version string should look like:<ul>
     * <li/><code>"MA.MI"</code>
     * </ul>
     * Where<ul>
     * <li/>MA is major version number,
     * <li/>MI is minor version number
     * </ul>
     * Label <code>java version</code> is parsed as non case sensitive.
     * @return Current JDK version for any JDK from system property.
     */
    private static JavaVersion propertyVersionParser(final String version) {
        final Matcher matcher = VM_VERSION_PATTERN.matcher(version);
        int major = 0, minor = 0;
        if (matcher.find()) {
            major = Integer.parseInt(matcher.group(1));
            String min = matcher.group(VM_MIN_VERSION_TOKENS + 2);
            minor = min != null ? Integer.parseInt(min) : 0;
        }
        return new JavaVersion(major, minor);
    }

    /**
     * Retrieve Java specification version from JDK system property.
     * @return Current JDK version for any JDK from system property.
     */
    private static JavaVersion propertyVersion() {
        return propertyVersionParser(vmVersionString());
    }

    /**
     * Java specification version detector.
     */
    public static JavaVersion vmVersion() {
        final JavaVersion version = runtimeVersion();
        return version != null ? version : propertyVersion();
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
    public int getMajor() {
        return major;
    }

    /**
     * Get minor version number.
     * @return Minor version number.
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Compares this <code>JavaVersion</code> object against another one.
     * @param version <code>JavaVersion</code> object to compare with
     *                <code>this</code> object.
     * @return Compare result:<ul>
     *         <li>Value <code>1</code> if <code>this</code> value
     *         is greater than supplied <code>version</code> value.</li>
     *         <li>Value <code>-1</code> if <code>this</code> value
     *         is lesser than supplied <code>version</code> value.</li>
     *         <li>Value <code>0</code> if both <code>this</code> value
     *         and supplied <code>version</code> values are equal.</li>
     *         </ul>
     */
    public int comapreTo(final JavaVersion version) {
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
    public String toString() {
        final StringBuilder sb = new StringBuilder(3);
        sb.append(major);
        sb.append(SEPARATOR);
        sb.append(minor);
        return sb.toString();
    }

    /**
     * Return {@link JavaSEPlatform} matching this Java SE specification version.
     * @return {@link JavaSEPlatform} matching this Java SE specification version
     *         or {@code JavaSEPlatform.DEFAULT} as default when platform matching fails.
     */
    public JavaSEPlatform toPlatform() {
        return JavaSEPlatform.toValue(major, minor);
    }

}
