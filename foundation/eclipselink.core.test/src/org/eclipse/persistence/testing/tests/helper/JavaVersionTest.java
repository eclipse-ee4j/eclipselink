/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Tomas Kraus - 2017/10/11
 *          Bug 525854 - Fix Java SE platform detection and clean up platform code
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.helper;

import java.lang.reflect.Method;

import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.internal.helper.JavaVersion;
import org.eclipse.persistence.testing.framework.ReflectionHelper;
import org.junit.Test;

import junit.framework.TestCase;

public class JavaVersionTest extends TestCase {

    /**
     * Check whether current Java has {@code Runtime.Version} class.
     * @return Value of {@code true} when current Java has {@code Runtime.Version} class or {@code false} otherwise.
     */
    private static boolean hasRuntimeVersion() {
        final Method[] methods = Runtime.class.getDeclaredMethods();
        // getParameterCount() was added in Java SE 1.8 so using getParameterTypes() to let it work with 1.7.
        for (final Method method : methods) {
            if ("version".equals(method.getName()) && method.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test Java version retrieval from {@code Runtime.Version}. It just calls {@code JavaVersion#runtimeVersion()} method
     * on current Java SE when >9 and checks that no exception is thrown.
     */
    @Test
    public void testRuntimeVersion() {
        if (!hasRuntimeVersion()) {
            return;
        }
        try {
            final JavaVersion version =  ReflectionHelper.<JavaVersion>invokeStaticMethod("runtimeVersion", JavaVersion.class, null, JavaVersion.class);
            assertTrue("Minimal Java 9 required", version.getMajor() >= 9);
        } catch (ReflectiveOperationException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    /**
     * Test Java version retrieval from {@code java.specification.version} system property. It just calls
     * {@code JavaVersion#propertyVersionParser(String)} method with current Java SE {@code java.specification.version}
     * system property value and checks that no exception is thrown.
     */
    @Test
    public void testPropertyVersion() {
        try {
            final String versionString = ReflectionHelper.<String>invokeStaticMethod(
                    "vmVersionString", JavaVersion.class, null, String.class);
            final JavaVersion version =  ReflectionHelper.<JavaVersion>invokeStaticMethod(
                    "propertyVersionParser", JavaVersion.class, new Class[] {String.class}, JavaVersion.class, versionString);
        } catch (ReflectiveOperationException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    /**
     * Verify Java version parser on set of valid and invalid {@code java.specification.version} system property values.
     * Because all version strings are in {@code [0-9]+'.'[0-9]+} format, parser shall always return {@code JavaVersion}
     * instance containing provided version numbers.
     */
    @Test
    public void testPropertyVersionParser() {
        // Verify valid pairs.
        for (final int [] version : JavaUtilTest.VALID) {
            final int major = version[0];
            final int minor = version[1];
            final String versionString = JavaSEPlatform.versionString(major, minor);
            try {
                final JavaVersion javaVersion =  ReflectionHelper.<JavaVersion>invokeStaticMethod(
                        "propertyVersionParser", JavaVersion.class, new Class[] {String.class}, JavaVersion.class, versionString);
                assertNotNull("JavaVersion instance must be returned for valid platform"
                        +" version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]", javaVersion);
                assertTrue("Returned JavaVersion instance numbers do not match provided"
                        + " version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]", major == javaVersion.getMajor()
                        && minor == javaVersion.getMinor());
            } catch (ReflectiveOperationException e) {
                fail("Exception: " + e.getMessage());
            }
        }
        // Verify invalid pairs.
        for (final int [] version : JavaUtilTest.INVALID) {
            final int major = version[0];
            final int minor = version[1];
            final String versionString = JavaSEPlatform.versionString(major, minor);
            try {
                final JavaVersion javaVersion =  ReflectionHelper.<JavaVersion>invokeStaticMethod(
                        "propertyVersionParser", JavaVersion.class, new Class[] {String.class}, JavaVersion.class, versionString);
                assertNotNull("JavaVersion instance must be returned for invalid platform"
                        +" version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]", javaVersion);
                assertTrue("Returned JavaVersion instance numbers do not match provided"
                        + " version number ["+Integer.toString(major)+","+Integer.toString(minor)+"]", major == javaVersion.getMajor()
                        && minor == javaVersion.getMinor());
            } catch (ReflectiveOperationException e) {
                fail("Exception: " + e.getMessage());
            }
        }
    }

}
