/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import java.util.HashMap;

public class DataStorage {
    public static final String UNKNOWN_REQUEST_UNIQUE_ID = "unknown";

    // key name in data storage
    public static final String REQUEST_UNIQUE_ID = "eclipselink.jpars.request.unique-id";

    private final static ThreadLocal<HashMap<String, Object>> storage = new ThreadLocal<HashMap<String, Object>>() {
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
        if (REQUEST_UNIQUE_ID.equals(key)) {
            if (value == null) {
                return UNKNOWN_REQUEST_UNIQUE_ID;
            } else {
                return (String) value;
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
        storage.remove();
    }
}
