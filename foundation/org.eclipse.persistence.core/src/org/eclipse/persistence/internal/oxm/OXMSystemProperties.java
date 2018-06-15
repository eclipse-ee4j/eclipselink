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

    private OXMSystemProperties() {};

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     *
     * @since 2.6.0
     */
    public static final String JSON_TYPE_COMPATIBILITY = "org.eclipse.persistence.json.type-compatibility";

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     *
     * @since 2.6.0
     */
    public static final String JSON_USE_XSD_TYPES_PREFIX = "org.eclipse.persistence.json.use-xsd-types-prefix";

    public static final String DISABLE_SECURE_PROCESSING = "eclipselink.disableXmlSecurity";

    public static final Boolean jsonTypeCompatiblity = PrivilegedAccessHelper.getSystemPropertyBoolean(JSON_TYPE_COMPATIBILITY, false);

    public static final Boolean jsonUseXsdTypesPrefix = PrivilegedAccessHelper.getSystemPropertyBoolean(JSON_USE_XSD_TYPES_PREFIX, false);

}
