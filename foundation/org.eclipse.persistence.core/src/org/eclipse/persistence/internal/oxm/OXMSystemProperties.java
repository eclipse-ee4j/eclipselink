/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

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

    public static final Boolean jsonTypeCompatiblity = getBoolean(JSON_TYPE_COMPATIBILITY);

    public static final Boolean jsonUseXsdTypesPrefix = getBoolean(JSON_USE_XSD_TYPES_PREFIX);

    /**
     * Returns value of system property.
     *
     * @param propertyName system property
     * @return value of the system property
     */
    private static Boolean getBoolean(final String propertyName) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                    @Override
                    public Boolean run() throws Exception {
                        return Boolean.getBoolean(propertyName);
                    }});
            } catch (PrivilegedActionException e) {
                throw (RuntimeException) e.getCause();
            }
        } else {
            return Boolean.getBoolean(propertyName);
        }
    }
}
