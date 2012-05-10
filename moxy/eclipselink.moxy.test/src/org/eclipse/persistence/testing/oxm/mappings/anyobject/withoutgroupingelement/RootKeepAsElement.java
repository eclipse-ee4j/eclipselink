/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RootKeepAsElement {

    protected Object t1;

    public Object getT1() {
        return t1;
    }
 
    public void setT1(Object value) {
        this.t1 = value;
    }
    
    public boolean equals(Object object) {
        if (object instanceof RootKeepAsElement) {
            if (t1 == null && ((RootKeepAsElement) object).getT1() == null) {
                return true;
            } 
            if (t1 == null && ((RootKeepAsElement) object).getT1() != null) {
                return false;
            } 
            Object value1 = t1;
            Object value2 = ((RootKeepAsElement) object).getT1();
            if ((value1 instanceof Element) && (value2 instanceof Element)) {
                Element elem1 = (Element )value1;
                Element elem2 = (Element) value2;
                if(!(elem1.getLocalName().equals(elem2.getLocalName()))) {
                    return false;
                }
                return true;
            }
            return this.t1.equals(((RootKeepAsElement) object).getT1());
        }
        return false;
    }    

}