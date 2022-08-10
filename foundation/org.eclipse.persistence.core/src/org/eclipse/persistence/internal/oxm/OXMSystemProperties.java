/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Martin Vojtek - 2.6 - initial implementation
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * System properties holder.
 * Holds OXM specific properties.
 *
 * @author Martin Vojtek
 *
 */
public final class OXMSystemProperties {

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     *
     * @since 2.6.0
     */
    public static final String JSON_TYPE_COMPATIBILITY = "org.eclipse.persistence.json.type-compatibility";

    /**
     * Override default type property name for JSON as MOXy type discriminator.
     *
     * @since 2.7.4
     */
    public static final String JSON_TYPE_ATTRIBUTE_NAME = "org.eclipse.persistence.json.type-attribute-name";

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     *
     * @since 2.6.0
     */
    public static final String JSON_USE_XSD_TYPES_PREFIX = "org.eclipse.persistence.json.use-xsd-types-prefix";

    /**
     * Disable generation of "item" name in nested arrays in JSON.
     * @since 2.7.5
     */
    public static final String JSON_DISABLE_NESTED_ARRAY_NAME = "org.eclipse.persistence.json.disable-nested-array-name";

    public static final String DISABLE_SECURE_PROCESSING = "eclipselink.disableXmlSecurity";

    /**
     * Add suffix/decimal part to time part/seconds in case of conversion from supported date/time formats into String.
     * It happens only if source time doesn't contain fraction of seconds and for XML, JSON conversion. E.g.:
     * For -Dorg.eclipse.persistence.xml.time.suffix=.0
     * <ul>
     * <li>Source value: 2003-08-29T03:00:00-04:00  -&gt; Output string: 2003-08-29T03:00:00.0-04:00</li>
     * <li>Source value: 1975-02-21T07:47:15  -&gt; Output string: 1975-02-21T07:47:15.0</li>
     * </ul>
     * @since 2.7.11
     */
    public static final String XML_CONVERSION_TIME_SUFFIX = "org.eclipse.persistence.xml.time.suffix";

    public static final String DEFAULT_XML_CONVERSION_TIME_SUFFIX = "";

    public static final Boolean jsonTypeCompatiblity = PrivilegedAccessHelper.getSystemPropertyBoolean(JSON_TYPE_COMPATIBILITY, false);

    public static final Boolean jsonUseXsdTypesPrefix = PrivilegedAccessHelper.getSystemPropertyBoolean(JSON_USE_XSD_TYPES_PREFIX, false);

    public static final String jsonTypeAttributeName = PrivilegedAccessHelper.getSystemProperty(JSON_TYPE_ATTRIBUTE_NAME, Constants.SCHEMA_TYPE_ATTRIBUTE);

    public static final Boolean jsonDisableNestedArrayName = PrivilegedAccessHelper.getSystemPropertyBoolean(JSON_DISABLE_NESTED_ARRAY_NAME, false);

    public static final String xmlConversionTimeSuffix = PrivilegedAccessHelper.getSystemProperty(XML_CONVERSION_TIME_SUFFIX, DEFAULT_XML_CONVERSION_TIME_SUFFIX);

    private OXMSystemProperties() {}

}
