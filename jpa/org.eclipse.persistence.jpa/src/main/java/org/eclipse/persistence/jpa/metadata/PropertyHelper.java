/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.metadata;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.logging.SessionLog;

import java.security.AccessController;
import java.util.Map;

/**
 * Helper class to avoid duplicating code.
 */
final class PropertyHelper {

    /**
     * Check the provided map for an object with the given name.  If that object is not available, check the
     * System properties.  Log the value returned if logging is enabled at the FINEST level
     * @param propertyName property name
     * @param properties properties
     * @param log logger
     * @return object for the given name, null if not found
     */
    static Object getConfigPropertyLogDebug(final String propertyName, Map<String, ?> properties, SessionLog log) {
        Object value = null;
        if (properties != null) {
            value = properties.get(propertyName);
        }
        if (value == null) {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                value = AccessController.doPrivileged(new PrivilegedGetSystemProperty(propertyName));
            } else {
                value = System.getProperty(propertyName);
            }
        }
        if ((value != null) && (log !=  null)) {
            log.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyName, value});
        }
        return value;
    }
}
