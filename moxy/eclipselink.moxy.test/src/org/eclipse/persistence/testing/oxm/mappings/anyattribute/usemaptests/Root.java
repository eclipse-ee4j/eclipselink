/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.usemaptests;

import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;

/**
 * 
 */
public class Root {
    private Map any = new Hashtable();
    
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
            } else if(collection1 == null && collection2.size() == 0) {
                return true;
            } else if(collection2 == null && collection1.size() == 0) {
                return true;
            } else if(collection1 == null && collection2.size() > 0) {
                return false;
            } else if(collection2 == null && collection1.size() > 0) {
                return false;
            } else if(any.size() != ((Root)object).getAny().size()) {
                return false;
            } else {
                Iterator values1 = any.keySet().iterator();
                Iterator values2 = ((Root)object).getAny().keySet().iterator();
                while(values1.hasNext()) {
                    Object key1 = values1.next();
                    Object key2 = values2.next();
                    if(!(key1.equals(key2) && any.get(key1).equals(collection2.get(key2)))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    public String toString() {
        String value = "Root:\n ";
        if(any == null) {
            return value;
        }
        Iterator keys = any.keySet().iterator();
        while(keys.hasNext()) {
            Object key = keys.next();
            value += "\tKey:" + key + " --> Value:" + any.get(key) + "\n";
        }
        return value;
    }
}
