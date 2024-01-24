/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-10-06 10:01:09 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.reuse;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class Root {

    private Map<Object, Object> any = new Properties();

    public Map getAny() {
        return any;
    }

    public void setAny(Map a) {
        any = a;
    }

    public boolean equals(Object object) {
        if (object instanceof Root) {
            Map<Object, Object> collection1 = any;
            Map collection2 = ((Root) object).getAny();
            if (collection1 == null && collection2 == null) {
                return true;
            } else if (collection1 == null && collection2.isEmpty()) {
                return true;
            } else if (collection2 == null && collection1.isEmpty()) {
                return true;
            } else if (collection1 == null && !collection2.isEmpty()) {
                return false;
            } else if (collection2 == null && !collection1.isEmpty()) {
                return false;
            } else if (any.size() != ((Root) object).getAny().size()) {
                return false;
            } else if (any.getClass() != ((Root) object).getAny().getClass()) {
                return false;
            } else {
                Iterator<Object> values1 = any.keySet().iterator();
                Iterator values2 = ((Root) object).getAny().keySet().iterator();
                while (values1.hasNext()) {
                    Object key1 = values1.next();
                    Object key2 = values2.next();
                    if (!(key1.equals(key2) && any.get(key1).equals(collection2.get(key2)))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder value = new StringBuilder("Root:\n");
        if (any == null) {
            return value.toString();
        }
        for (Object key : any.keySet()) {
            value.append("\tKey:").append(key).append(" --> Value:").append(any.get(key)).append("\n");
        }
        return value.toString();
    }

}
