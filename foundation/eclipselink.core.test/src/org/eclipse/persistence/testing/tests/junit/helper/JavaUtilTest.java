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
//     Tomas Kraus, Peter Benedikovic - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.internal.helper.JavaVersion;
import org.eclipse.persistence.testing.framework.ReflectionHelper;
import org.junit.Test;

/**
 * Test Java related utilities.
 * @author Tomas Kraus, Peter Benedikovic
 */
public class JavaUtilTest {

    // Valid version number pairs.
    static final int[][] VALID = {
            {1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 9},
            {9, 0}, {10, 0}, {11, 0}
    };

    // Invalid version number pairs.
    static final int[][] INVALID = {
            {0, 0}, {0, 1}, {0, 3}, {0, 5}, {0, 7}, {0, 9},
            {1, 0}, {2, 0}, {2, 1}, {2, 2}, {3, 0}, {4, 0}, {1, 10},
            {18, 1}, {18, 2}, {18, 3}, {18, 4}, {18, 5}, {18, 6},
            {18, 7}, {18, 8}, {18, 9}, {18, 10}, {18, 11}, {18, 12}
    };

    // DEFAULT platform value.
    static final JavaSEPlatform LATEST = initDefault();

    /** Version numbers result mapping. Covers exceptions.
     * See also {@code JavaSEPlatform.stringValuesMap} initialization code
     * and {@link JavaSEPlatform#toValue(int, int)}.
     * 1.9 -> 9.0
     * @param version source version numbers
     * @return result version numbers
     */
    static int[] resultMapping(int[] version) {
        switch (version[0]) {
            case 1:
                switch (version[1]) {
                    case 9: return new int[] {9, 0};
                    default: return version;
                }
            default: return version;
        }
    }

    /**
     * Initialize value of JavaSEPlatform.DEFAULT.
     * @return value of JavaSEPlatform.DEFAULT.
     */
    static final JavaSEPlatform initDefault() {
        try {
            return ReflectionHelper.getPrivateStatic(JavaSEPlatform.class, "LATEST");
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Test <code>JavaVersion.comapreTo</code> functionality.
     */
    @Test
    public void testJavaVersionCompareTo() {
        JavaVersion version = new JavaVersion(1, 4);
        // Differs on major numbers.
        assertEquals( 1, version.comapreTo(new JavaVersion(0, 4)));
        assertEquals(-1, version.comapreTo(new JavaVersion(2, 4)));
        // Differs on minor numbers.
        assertEquals( 1, version.comapreTo(new JavaVersion(1, 3)));
        assertEquals(-1, version.comapreTo(new JavaVersion(1, 5)));
        // Equal values
        assertEquals( 0, version.comapreTo(new JavaVersion(1, 4)));
    }

    /**
     * Test that <code>javaVmVersion</code> is able to parse Java version
     * output.
     */
    @Test
    public void testJavaVersion() {
        JavaVersion version = JavaVersion.vmVersion();
        assertTrue(version.getMajor() > 0);
    }

    /**
     * Test {@link String} with major and minor version numbers pairs
     * to <code>JavaSEPlatform</code> conversion.
     */
    @Test
    public void testStringToPlatform() {
        // Verify valid pairs.
        for (int [] version : VALID) {
            int major = version[0];
            int minor = version[1];
            String versionString = JavaSEPlatform.versionString(major, minor);
            JavaSEPlatform platform = JavaSEPlatform.toValue(versionString);
            assertNotNull("There should exist platform for valid platform"
                    +" version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]", platform);
            int[] result = resultMapping(version);
            assertTrue("Returned platform version numbers do not match provided"
                    + " version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]",
                    result[0] == platform.getMajor() && result[1] == platform.getMinor());
        }
        // Invalid version number pairs.
        for (int [] version : INVALID) {
            int major = version[0];
            int minor = version[1];
            String versionString = JavaSEPlatform.versionString(major, minor);
            JavaSEPlatform platform = JavaSEPlatform.toValue(major, minor);
            assertTrue("Returned platform shall be JavaSEPlatform.LATEST for invalid version "
                    + "number ["+Integer.toString(major)+","+Integer.toString(minor)+"]",
                    LATEST.getMajor() == platform.getMajor() && LATEST.getMinor() == platform.getMinor());
        }
    }

    /**
     * Test major and minor version numbers pairs to <code>JavaSEPlatform</code>
     * conversion.
     */
    @Test
    public void testMajorMinorToPlatform() {
        // Verify valid pairs.
        for (int [] version : VALID) {
            int major = version[0];
            int minor = version[1];
            JavaSEPlatform platform = JavaSEPlatform.toValue(major, minor);
            assertNotNull("There should exist platform for valid platform"
                    +" version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]", platform);
            int[] result = resultMapping(version);
            assertTrue("Returned platform version numbers do not match provided"
                    + " version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]",
                    result[0] == platform.getMajor() && result[1] == platform.getMinor());
        }
        // Invalid version number pairs.
        for (int [] version : INVALID) {
            int major = version[0];
            int minor = version[1];
            JavaSEPlatform platform = JavaSEPlatform.toValue(major, minor);
            assertTrue("Returned platform shall be JavaSEPlatform.LATEST for invalid version "
                    + "number ["+Integer.toString(major)+","+Integer.toString(minor)+"]",
                    LATEST.getMajor() == platform.getMajor() && LATEST.getMinor() == platform.getMinor());
        }
    }

    /**
     * Test current platform equals shortcut method.
     */
    @Test
    public void testCurrentIs() {
        // Test method on current platform.
        final JavaSEPlatform current = JavaSEPlatform.toValue(
                JavaSEPlatform.CURRENT.getMajor(),
                JavaSEPlatform.CURRENT.getMinor());
        boolean result = JavaSEPlatform.is(current);
        assertTrue("Current platform check shall return true.", result);
        // Test method on unsupported platforms (up to 1.6).
        for (JavaSEPlatform platform : JavaSEPlatform.values()) {
            if (platform.isSupported()) {
                break;
            }
            result = JavaSEPlatform.is(platform);
            assertFalse("Unsupported platform check shall return false.",
                    result);
        }
    }

}
