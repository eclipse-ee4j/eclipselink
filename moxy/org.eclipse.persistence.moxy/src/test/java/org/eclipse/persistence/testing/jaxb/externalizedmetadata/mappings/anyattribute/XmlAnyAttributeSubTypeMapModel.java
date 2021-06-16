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

        Map thisChildren = this.children;
        Map otherChildren = modelObj.children;

        if (thisChildren == null) {
            return (otherChildren == null || otherChildren.size() == 0);
        }

        if (otherChildren == null) {
            return (thisChildren.size() == 0);
        }

        if (thisChildren.size() != otherChildren.size()) {
            return false;
        }

        Iterator values1 = thisChildren.keySet().iterator();
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
