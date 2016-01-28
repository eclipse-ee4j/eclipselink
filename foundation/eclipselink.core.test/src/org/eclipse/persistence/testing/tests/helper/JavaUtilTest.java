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
package org.eclipse.persistence.testing.tests.helper;

import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.internal.helper.JavaVersion;
import org.junit.Test;

/**
 * Test Java related utilities.
 * @author Tomas Kraus, Peter Benedikovic
 */
public class JavaUtilTest extends junit.framework.TestCase {

    /**
     * Constructs an instance of Java utilities.
     * @param name java.lang.String
     */
    public JavaUtilTest(String name) {
        super(name);
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
        // Valid version number pairs.
        int[][] valid = {{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7},
                {1, 8}, {1, 9}, {9, 0}};
        int[][] invalid = {{0, 0}, {0, 1}, {0, 3}, {0, 5}, {0, 7}, {0, 9},
                {1, 0}, {2, 0}, {2, 1}, {2, 2}, {3, 0}, {4, 0}, {1, 10}};
        // Verify valid pairs.
        for (int [] version : valid) {
            int major = version[0];
            int minor = version[1];
            String versionString = JavaSEPlatform.versionString(major, minor);
            JavaSEPlatform platform = JavaSEPlatform.toValue(versionString);
            assertNotNull("There should exist platform for valid platform"
                    +" version numbers.", platform);
            assertTrue("Returned platform version numbers do not match provided"
                    + " version numbers", major == platform.getMajor()
                    && minor == platform.getMinor());
        }
        // Invalid version number pairs.
        for (int [] version : invalid) {
            int major = version[0];
            int minor = version[1];
            String versionString = JavaSEPlatform.versionString(major, minor);
            JavaSEPlatform platform = JavaSEPlatform.toValue(major, minor);
            assertNull("Returned platform shall be null for invalid version "
                    + "number", platform);
        }
    }

    /**
     * Test major and minor version numbers pairs to <code>JavaSEPlatform</code>
     * conversion.
     */
    @Test
    public void testMajorMinorToPlatform() {
        // Valid version number pairs.
        int[][] valid = {{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7},
                {1, 8}, {1, 9}, {9, 0}};
        int[][] invalid = {{0, 0}, {0, 1}, {0, 3}, {0, 5}, {0, 7}, {0, 9},
                {1, 0}, {2, 0}, {2, 1}, {2, 2}, {3, 0}, {4, 0}, {1, 10}};
        // Verify valid pairs.
        for (int [] version : valid) {
            int major = version[0];
            int minor = version[1];
            JavaSEPlatform platform = JavaSEPlatform.toValue(major, minor);
            assertNotNull("There should exist platform for valid platform"
                    +" version numbers.", platform);
            assertTrue("Returned platform version numbers do not match provided"
                    + " version numbers", major == platform.getMajor()
                    && minor == platform.getMinor());
        }
        // Invalid version number pairs.
        for (int [] version : invalid) {
            int major = version[0];
            int minor = version[1];
            JavaSEPlatform platform = JavaSEPlatform.toValue(major, minor);
            assertNull("Returned platform shall be null for invalid version "
                    + "number", platform);
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
