/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.jaxb;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.eclipse.persistence.internal.oxm.OXMSystemProperties;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

public final class MOXySystemProperties {

    private MOXySystemProperties() {};

    /**
     * User can specify via org.eclipse.persistence.moxy.annotation.xml-id-extension property, that he wants to use extended behavior of XmlId annotation.
     * When extended behavior is used, XmlId can be of different type than java.lang.String.
     */
    public static final String XML_ID_EXTENSION = "org.eclipse.persistence.moxy.annotation.xml-id-extension";

    /**
     * User can specify via org.eclipse.persistence.moxy.annotation.xml-value-extension property, that he wants to use extended behavior of XmlValue annotation.
     * When extended behavior is used, class with field annotated with XmlValue can extend classes different than java.lang.Object.
     */
    public static final String XML_VALUE_EXTENSION = "org.eclipse.persistence.moxy.annotation.xml-value-extension";

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     */
    public static final String JSON_TYPE_COMPATIBILITY = OXMSystemProperties.JSON_TYPE_COMPATIBILITY;

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     */
    public static final String JSON_USE_XSD_TYPES_PREFIX = OXMSystemProperties.JSON_USE_XSD_TYPES_PREFIX;


    public static final Boolean xmlIdExtension = getBoolean(XML_ID_EXTENSION);

    public static final Boolean xmlValueExtension = getBoolean(XML_VALUE_EXTENSION);

    public static final Boolean jsonTypeCompatibility = getBoolean(JSON_TYPE_COMPATIBILITY);

    public static final Boolean jsonUseXsdTypesPrefix = getBoolean(JSON_USE_XSD_TYPES_PREFIX);

    /**
     * Returns value of system property.
     *
     * @param propertyName system property
     * @return value of the system property
     */
    private static Boolean getBoolean(final String propertyName) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return runDoPrivileged(propertyName);
        } else {
            return getSystemPropertyValue(propertyName);
        }
    }

    private static Boolean runDoPrivileged(final String propertyName) {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws Exception {
                    return getSystemPropertyValue(propertyName);
                }});
        } catch (PrivilegedActionException e) {
            throw (RuntimeException) e.getCause();
        }
    }

    private static Boolean getSystemPropertyValue(final String propertyName) {
        return Boolean.getBoolean(propertyName);
    }
}
