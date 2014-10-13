/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
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
    /** Java SE 1.9. */
    v1_9(1,9);

    /** GlassFish Java SE platform enumeration length. */
    public static final int length = JavaSEPlatform.values().length;

    /**
     * Stored <code>String</code> values for backward <code>String</code>
     * conversion.
     */
    private static final Map<String, JavaSEPlatform> stringValuesMap
            = new HashMap<String, JavaSEPlatform>(values().length);

    // Initialize backward String conversion Map.
    static {
        for (JavaSEPlatform platform : JavaSEPlatform.values()) {
            stringValuesMap.put(platform.toString().toUpperCase(), platform);
        }
    }

    /**
     * Returns a <code>JavaSEPlatform</code> with a value represented by the
     * specified <code>String</code>. The <code>JavaSEPlatform</code> returned
     * represents existing value only if specified <code>String</code>
     * matches any <code>String</code> returned by <code>toString</code> method.
     * Otherwise <code>null</code> value is returned.
     * @param platformName Value containing <code>JavaSEPlatform</code>
     *                     <code>toString</code> representation.
     * @return <code>JavaSEPlatform</code> value represented
     *         by <code>String</code> or <code>null</code> if value
     *         was not recognized.
     */
    public static JavaSEPlatform toValue(final String platformName) {
        if (platformName != null) {
            return (stringValuesMap.get(platformName.toUpperCase()));
        } else {
            return null;
        }
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
     * Check if current platform is equal or greater to specified platform.
     * @param platform Platform to compare with.
     * @return Value of <code>true</code> if current platform is equal
     *         or greater to specified platform or <code>false</code> otherwise.
     */
    public boolean atLeast(final JavaSEPlatform platform) {
        return compareTo(platform) >= 0;
    }

    /**
     * Convert Java SE platform version value to <code>String</code>.
     * <p/>
     * @return A <code>String</code> representation of the value of this object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(4);
        sb.append(Integer.toString(major));
        sb.append(JavaVersion.SEPARATOR);
        sb.append(Integer.toString(minor));
        return sb.toString();
    }

}
