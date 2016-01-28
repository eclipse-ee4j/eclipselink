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

import java.util.HashMap;
import java.util.Map;

/**
 * Java SE platforms supported by EclipseLink.
 * @author Tomas Kraus, Peter Benedikovic
 */
public enum JavaSEPlatform implements Comparable<JavaSEPlatform> {

    /** Java SE 1.1. */
    v1_1(1,1),
    /** Java SE 1.2. */
    v1_2(1,2),
    /** Java SE 1.3. */
    v1_3(1,3),
    /** Java SE 1.4. */
    v1_4(1,4),
    /** Java SE 1.5. */
    v1_5(1,5),
    /** Java SE 1.6. */
    v1_6(1,6),
    /** Java SE 1.7. */
    v1_7(1,7),
    /** Java SE 1.8. */
    v1_8(1,8),
    /** Java SE 9. */
    v1_9(1,9),
    /** Java SE 9. */
    v9_0(9, 0);

    /**
     * Stored <code>String</code> values for backward <code>String</code>
     * conversion.
     */
    private static final Map<String, JavaSEPlatform> stringValuesMap
            = new HashMap<String, JavaSEPlatform>(values().length);

    // Initialize backward String conversion Map.
    static {
        for (JavaSEPlatform platform : JavaSEPlatform.values()) {
            stringValuesMap.put(platform.versionString(), platform);
        }
    }

    /** GlassFish Java SE platform enumeration length. */
    public static final int LENGTH = JavaSEPlatform.values().length;

    /** Current Java SE platform. */
    public static final JavaSEPlatform CURRENT
            = JavaVersion.vmVersion().toPlatform();

    /** Lowest supported Java SE platform. Currently it's Java SE 1.7. */
    public static final JavaSEPlatform MIN_SUPPORTED = v1_7;

    /**
     * Check whether current Java SE is exactly matching provided platform.
     * @param platform Java SE platform to compare with.
     */
    public static boolean is(JavaSEPlatform platform) {
        return CURRENT.equals(platform);
    }

    /**
     * Returns a <code>JavaSEPlatform</code> with a value represented by the
     * specified <code>String</code>. The <code>JavaSEPlatform</code> returned
     * represents existing value only if specified <code>String</code>
     * matches any <code>String</code> returned by <code>versionString()</code>
     * method.
     * Otherwise <code>null</code> value is returned.
     * @param platformName Value containing <code>JavaSEPlatform</code>
     *                     <code>versionString</code> representation.
     * @return <code>JavaSEPlatform</code> value represented
     *         by <code>String</code> or <code>null</code> if value
     *         was not recognized.
     */
    public static JavaSEPlatform toValue(final String platformName) {
        if (platformName != null) {
            return (stringValuesMap.get(platformName));
        } else {
            return null;
        }
    }

    // There are not too many versions yet so direct mapping in code is simple.
    /**
     * Returns a <code>JavaSEPlatform</code> matching provided
     * <code>major</code> and <code>minor</code> version numbers.
     * @param major Major version number.
     * @param minor Minor version number.
     * @return <code>JavaSEPlatform</code> value matching provided
     *         <code>major</code> and <code>minor</code> version numbers
     *         or <code>null</code> when such a value does not exist.
     */
    public static JavaSEPlatform toValue(final int major, final int minor) {
        switch (major) {
        case 1:
            switch (minor) {
                case 1: return v1_1;
                case 2: return v1_2;
                case 3: return v1_3;
                case 4: return v1_4;
                case 5: return v1_5;
                case 6: return v1_6;
                case 7: return v1_7;
                case 8: return v1_8;
                case 9: return v1_9;
                default: return null;
            }
        case 9:
            return v9_0;
        default: return null;
        }
    }

    /**
     * Generate {@link String} containing minor and major version numbers
     * in {@code <major> '.' <minor>} format.
     * @param major Major version number.
     * @param minor Minor version number.
     * @return Generated {@link String}
     */
    public static final String versionString(final int major, final int minor) {
        StringBuilder sb = new StringBuilder(4);
        sb.append(Integer.toString(major));
        sb.append(JavaVersion.SEPARATOR);
        sb.append(Integer.toString(minor));
        return sb.toString();
    }

    /**
     * Constructs an instance of Java SE platform.
     * @param major Major version number.
     * @param minor Minor version number.
     */
    JavaSEPlatform(final int major, final int minor) {
        this.major = major;
        this.minor = minor;
    }

    /** Major version number. */
    private final int major;

    /** Minor version number. */
    private final int minor;

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
     * Check if this platform is equal or greater to specified platform.
     * @param platform Platform to compare with.
     * @return Value of <code>true</code> if this platform is equal
     *         or greater to specified platform or <code>false</code> otherwise.
     */
    public boolean atLeast(final JavaSEPlatform platform) {
        return compareTo(platform) >= 0;
    }

    /**
     * Check whether this platform is supported platform.
     * @return Value of <code>true</code> when this platform is supported
     *         platform or <code>false</code> otherwise.
     */
    public boolean isSupported() {
        return compareTo(MIN_SUPPORTED) >= 0;
    }

    /**
     * Generate {@link String} containing minor and major version numbers
     * in {@code <major> '.' <minor>} format.
     * @return Generated {@link String}
     */
    public String versionString() {
        return versionString(major, minor);
    }

    // Currently this is identical with versionString() method.
    /**
     * Convert Java SE platform version value to human readable
     * <code>String</code>.
     * @return A <code>String</code> representation of the value of this object.
     */
    @Override
    public String toString() {
        return versionString(major, minor);
    }

}
