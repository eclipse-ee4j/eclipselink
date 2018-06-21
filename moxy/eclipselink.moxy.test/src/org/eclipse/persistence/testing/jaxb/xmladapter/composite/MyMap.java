/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.LinkedHashMap;
import java.util.Iterator;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="map")
public class MyMap {
    @XmlElement(name="hashMap")
    @XmlJavaTypeAdapter(MyHashMapAdapter.class)
    public java.util.LinkedHashMap hashMap;

    public boolean equals(Object obj) {
        if (!(obj instanceof MyMap)) {
            return false;
        }
        LinkedHashMap map = ((MyMap) obj).hashMap;
        for (Iterator keyIt = map.keySet().iterator(); keyIt.hasNext(); ) {
            Object key = keyIt.next();
            if (!this.hashMap.containsKey(key)) {
                return false;
            }
            if (!this.hashMap.containsValue(map.get(key))) {
                return false;
            }
        }
        return true;
    }
}
