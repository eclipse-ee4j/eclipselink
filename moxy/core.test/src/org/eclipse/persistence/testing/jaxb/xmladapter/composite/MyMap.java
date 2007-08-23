/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.HashMap;
import java.util.Iterator;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="map")
public class MyMap {
    @XmlElement(name="hashMap")
    @XmlJavaTypeAdapter(MyHashMapAdapter.class)
    public HashMap hashMap;
    
    public boolean equals(Object obj) {
        if (!(obj instanceof MyMap)) {
            return false;
        }
        HashMap map = ((MyMap) obj).hashMap;
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
