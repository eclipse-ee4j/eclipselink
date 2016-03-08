/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Tomas Kraus - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.localization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.internal.localization.i18n.DMSLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.EclipseLinkLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.JAXBLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.LoggingLocalizationResource;
import org.eclipse.persistence.internal.localization.i18n.ToStringLocalizationResource;
import org.eclipse.persistence.testing.framework.ReflectionHelper;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Unit tests for EclipseLink logging messages resource bundles.
 */
public class LocalizationResourcesTest extends TestCase {

    /**
     * Creates an instance of jUnit tests for EclipseLink logging categories enumeration.
     */
    public LocalizationResourcesTest() {
        super();
    }

    /**
     * Creates an instance of jUnit tests for EclipseLink logging categories enumeration.
     * @param name jUnit test name.
     */
    public LocalizationResourcesTest(final String name) {
        super(name);
    }

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
            fail("Could not access " + bundleName + "#getContents()");
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
            fail(buildMessageWithList("Non String key found in bundle: ", nonStringKeys));
        }
        if (duplicateKeys.size() > 0) {
            fail(buildMessageWithList("Duplicate bundle keys found: ", duplicateKeys));
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

}
