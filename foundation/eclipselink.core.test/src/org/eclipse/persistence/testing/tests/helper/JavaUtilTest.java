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
package org.eclipse.persistence.testing.tests.helper;

import org.junit.Test;
import org.eclipse.persistence.internal.helper.JavaVersion;

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
        JavaVersion version = new JavaVersion(1, 4, 2, 22);
        // Differs on major numbers.
        assertEquals( 1, version.comapreTo(new JavaVersion(0, 4, 2, 22)));
        assertEquals(-1, version.comapreTo(new JavaVersion(2, 4, 2, 22)));
        // Differs on minor numbers.
        assertEquals( 1, version.comapreTo(new JavaVersion(1, 3, 2, 22)));
        assertEquals(-1, version.comapreTo(new JavaVersion(1, 5, 2, 22)));
        // Differs on revision numbers.
        assertEquals( 1, version.comapreTo(new JavaVersion(1, 4, 1, 22)));
        assertEquals(-1, version.comapreTo(new JavaVersion(1, 4, 3, 22)));
        // Differs on patch numbers.
        assertEquals( 1, version.comapreTo(new JavaVersion(1, 4, 2, 21)));
        assertEquals(-1, version.comapreTo(new JavaVersion(1, 4, 2, 23)));
        // Equal values
        assertEquals( 0, version.comapreTo(new JavaVersion(1, 4, 2, 22)));
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

}
