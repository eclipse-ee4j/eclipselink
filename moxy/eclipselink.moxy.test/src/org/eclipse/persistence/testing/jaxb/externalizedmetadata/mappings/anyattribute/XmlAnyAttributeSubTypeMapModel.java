/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
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
