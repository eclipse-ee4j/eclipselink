/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      gonural - initial
package org.eclipse.persistence.jpa.rs;

import java.util.HashMap;

public class DataStorage {
    // key names in the data storage
    public static final String REQUEST_ID = "requestId";

    private final static InheritableThreadLocal<HashMap<String, Object>> storage = new InheritableThreadLocal<HashMap<String, Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    /**
     * Gets the.
     *
     * @param key the key
     * @return the object
     */
    public static Object get(String key) {
        Object value = storage.get().get(key);
        if (REQUEST_ID.equals(key)) {
            if (value == null) {
                return "unknown";
            }
        }
        return value;
    }

    /**
     * Sets the.
     *
     * @param key the key
     * @param value the value
     */
    public static void set(String key, Object value) {
        storage.get().put(key, value);
    }

    /**
     * Destroy.
     */
    public static void destroy() {
        if (storage != null) {
            if (storage.get() != null) {
                storage.get().clear();
            }
            storage.remove();
        }
    }
}
