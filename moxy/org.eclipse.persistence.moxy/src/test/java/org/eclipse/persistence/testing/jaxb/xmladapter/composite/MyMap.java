/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.LinkedHashMap;
import java.util.Iterator;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
