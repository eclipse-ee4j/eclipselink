/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Tomas Kraus - 2018/01/08
//          Bug 530901 - Prepare Java SE platform detection code for Java 11
package org.eclipse.persistence.testing.tests.junit.helper;

import static org.junit.Assert.assertEquals;

import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.junit.Test;

/**
 * JavaSEPlatform class jUnit tests.
 */
public class JavaSEPlatformTest {

    /** This value must match {@code JavaSEPlatform.LATEST} */
    static final JavaSEPlatform LATEST = JavaUtilTest.initDefault();

    /**
     * Versions data holder.
     */
    private static final class VersionData {
        private final int major;
        private final int minor;
        private final JavaSEPlatform platform;
        private VersionData(final int major, final int minor, final JavaSEPlatform platform) {
            this.major = major;
            this.minor = minor;
            this.platform = platform;
        }
    }

    /** Input data for {@code testToValue()} test method.
     *  Contains input major and minor version numbers with expected JavaSEPlatform value to be returned.
     *  Data must match expected {@link JavaSEPlatform#toValue(int, int)} method returned values. */
    private static final VersionData[] TO_VALUE_DATA = {
        new VersionData( 1,  1, JavaSEPlatform.v1_1),
        new VersionData( 1,  2, JavaSEPlatform.v1_2),
        new VersionData( 1,  3, JavaSEPlatform.v1_3),
        new VersionData( 1,  4, JavaSEPlatform.v1_4),
        new VersionData( 1,  5, JavaSEPlatform.v1_5),
        new VersionData( 1,  6, JavaSEPlatform.v1_6),
        new VersionData( 1,  7, JavaSEPlatform.v1_7),
        new VersionData( 1,  8, JavaSEPlatform.v1_8),
        new VersionData( 1,  9, JavaSEPlatform.v9_0),
        new VersionData( 1, 10, LATEST),
        new VersionData( 9,  0, JavaSEPlatform.v9_0),
        new VersionData(10,  0, JavaSEPlatform.v10_0),
        new VersionData(11,  0, JavaSEPlatform.v11_0),
        new VersionData(12,  0, LATEST)
    };

    /**
     * Test {@code toValue(int,int)} static method.
     */
    @Test
    public void testToValue() {
        for (final VersionData data : TO_VALUE_DATA) {
            final JavaSEPlatform out = JavaSEPlatform.toValue(data.major, data.minor);
            assertEquals("Expected " + data.platform.toString()
                    + " for version number " + Integer.toString(data.major) + "."
                    + Integer.toString(data.minor), data.platform, out);
        }
    }

}
