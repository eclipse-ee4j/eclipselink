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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute;

import java.util.Map;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class XmlAnyAttributeSubTypeMapModel {
    public Map<QName, String> children;

    public Map<QName, String> getChildren() {
        return children;
    }

    public void setChildren(Map<QName, String> children) {
        this.children = children;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        XmlAnyAttributeSubTypeMapModel modelObj;
        try {
            modelObj = (XmlAnyAttributeSubTypeMapModel) obj;
        } catch (ClassCastException e) {
            return false;
        }

        Map<QName, String> thisChildren = this.children;
        Map<QName, String> otherChildren = modelObj.children;

        if (thisChildren == null) {
            return (otherChildren == null || otherChildren.isEmpty());
        }

        if (otherChildren == null) {
            return (thisChildren.isEmpty());
        }

        if (thisChildren.size() != otherChildren.size()) {
            return false;
        }

        Iterator<QName> values1 = thisChildren.keySet().iterator();
        while(values1.hasNext()) {
            Object key1 = values1.next();
            Object value1 = thisChildren.get(key1);
            Object value2 = otherChildren.get(key1);

            if (!(value1.equals(value2))) {
                return false;
            }
        }
        return true;
    }
}
