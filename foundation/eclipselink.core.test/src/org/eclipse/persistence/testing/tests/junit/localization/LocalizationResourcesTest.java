/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Tomas Kraus - Initial implementation
//      11/07/2017 - Dalia Abo Sheasha
//        - 526957 : Split the logging and trace messages
package org.eclipse.persistence.testing.tests.junit.localization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.internal.localization.i18n.DMSLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.EclipseLinkLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.JAXBLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.LoggingLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.ToStringLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.TraceLocalizationResource;
import org.eclipse.persistence.testing.framework.ReflectionHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for EclipseLink logging messages resource bundles.
 */
public class LocalizationResourcesTest {

    /**
     * Build message with list content.
     * @param message Message to start with.
     * @param list List to be appended after message.
     * @return Message with list content appended.
     */
    private static String buildMessageWithList(final String message, final List<String> list) {
        // Count total size to avoid StringBuilder internal buffer resizing.
        int size = 0;
        for (String item : list) {
            size += item.length();
        }
        StringBuilder sb = new StringBuilder(message.length() + list.size() + size + 3);
        boolean notFirst = false;
        sb.append(message);
        sb.append(" [");
        for (String item : list) {
            if (notFirst) {
                sb.append(',');
            } else {
                notFirst = true;
            }
            sb.append(item);
        }
        sb.append(']');
        return sb.toString();
    }


    /**
     * Test {@code getContents()} method of specified resource bundle class for duplicate keys.
     * @param c Logging resource bundle class being tested.
     */
    private static void verifyBundle(final Class c) {
        final List<String> nonStringKeys = new LinkedList<>();
        final List<String> duplicateKeys = new LinkedList<>();
        final String bundleName = c.getSimpleName();
        final Set<String> keys = new HashSet<>();
        Object[][] bundle;
        try {
            Object instance = ReflectionHelper.getInstance(c, new Class[] {}, new Object[] {});
            bundle = (Object[][])ReflectionHelper.invokeMethod("getContents", instance, new Class[] {}, new Object[] {});
        } catch (ReflectiveOperationException | SecurityException e) {
            Assert.fail("Could not access " + bundleName + "#getContents()");
            bundle = null;
        }
        if (bundle != null) {
            for (Object[] message : bundle) {
                if (message[0] instanceof String) {
                    String key = (String)message[0];
                    //log.log(SessionLog.INFO, "  - Checking key [{0}]", new Object[] {key}, false);
                    if (keys.contains(key)) {
                        StringBuilder sb = new StringBuilder(bundleName.length() + key.length() + 1);
                        sb.append(bundleName);
                        sb.append(':');
                        sb.append(key);
                        duplicateKeys.add(sb.toString());
                    } else {
                        keys.add(key);
                    }
                } else {
                    nonStringKeys.add(bundleName);
                }
            }
        }
        if (nonStringKeys.size() > 0) {
            Assert.fail(buildMessageWithList("Non String key found in bundle: ", nonStringKeys));
        }
        if (duplicateKeys.size() > 0) {
            Assert.fail(buildMessageWithList("Duplicate bundle keys found: ", duplicateKeys));
        }
    }

    /**
     * Test {@code LoggingLocalizationResource#getContents()} for duplicate keys.
     */
    @Test
    public void testLoggingLocalizationResource() {
        verifyBundle(LoggingLocalizationResource.class);
    }

    /**
     * Test {@code DMSLocalizationResource#getContents()} for duplicate keys.
     */
    @Test
    public void testDMSLocalizationResource() {
        verifyBundle(DMSLocalizationResource.class);
    }

    /**
     * Test {@code EclipseLinkLocalizationResource#getContents()} for duplicate keys.
     */
    @Test
    public void testEclipseLinkLocalizationResource() {
        verifyBundle(EclipseLinkLocalizationResource.class);
    }

    /**
     * Test {@code JAXBLocalizationResource#getContents()} for duplicate keys.
     */
    @Test
    public void testJAXBLocalizationResource() {
        verifyBundle(JAXBLocalizationResource.class);
    }

    /**
     * Test {@code ToStringLocalizationResource#getContents()} for duplicate keys.
     */
    @Test
    public void testToStringLocalizationResource() {
        verifyBundle(ToStringLocalizationResource.class);
    }

    /**
     * Test {@code TraceLocalizationResource#getContents()} for duplicate keys.
     */
    @Test
    public void testTraceLocalizationResource() {
        verifyBundle(TraceLocalizationResource.class);
    }

}
