/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection;

import java.util.List;

public class Employee {
    public List<Object> stuff;
    
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public List<Object> getStuff() {
        wasGetCalled = true;
        return stuff;
    }

    public void setStuff(List<Object> stuff) {
        wasSetCalled = true;
        this.stuff = stuff;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }
        
        if (stuff == null) {
            return empObj.stuff == null;
        }
        
        for (Object s : stuff) {
            if (!stuffExistsInList(s, empObj.stuff)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean stuffExistsInList(Object stuff, List<Object> stuffList) {
        for (Object listStuff : stuffList) {
            if (listStuff.equals(stuff)) {
                return true;
            }
        }
        return false;
    }
}
