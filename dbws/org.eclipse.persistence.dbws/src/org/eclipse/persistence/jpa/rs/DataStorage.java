/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
