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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement;

import java.util.HashMap;
import java.util.Map;

/**
 *  @version $Header: Root.java 24-apr-2006.15:08:42 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Root {
    private Map any = new HashMap();

    public Map getAny() {
        return any;
    }
    public void setAny(Map a) {
        any = a;
    }

    public boolean equals(Object object) {
        if(object instanceof Root) {
            Map collection1 = any;
            Map collection2 = ((Root)object).getAny();
            if(collection1 == null && collection2 == null) {
                return true;
            } else if(collection1 == null && collection2.isEmpty()) {
                return true;
            } else if(collection2 == null && collection1.isEmpty()) {
                return true;
            } else if(collection1 == null && !collection2.isEmpty()) {
                return false;
            } else if(collection2 == null && !collection1.isEmpty()) {
                return false;
            } else if(any.size() != ((Root)object).getAny().size()) {
                return false;
            } else {
                for (Object key1 : any.keySet()) {
                    Object value1 = collection1.get(key1);
                    Object value2 = collection2.get(key1);

                    if (!(value1.equals(value2))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    public String toString() {
        StringBuilder value = new StringBuilder("Root:\n ");
        if(any == null) {
            return value.toString();
        }
        for (Object key : any.keySet()) {
            value.append("\tKey:").append(key).append(" --> Value:").append(any.get(key)).append("\n");
        }
        return value.toString();
    }
}
