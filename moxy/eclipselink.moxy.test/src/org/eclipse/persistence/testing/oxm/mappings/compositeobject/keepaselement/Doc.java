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
 *     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.keepaselement;

import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.RootKeepAsElement;
import org.w3c.dom.Element;

public class Doc {

    protected Elem elem;
    protected Object elem1;

    public Elem getElem() {
        return elem;
    }

    public void setElem(Elem value) {
        this.elem = value;
    }

    public Object getElem1() {
        return elem1;
    }

    public void setElem1(Object value) {
        this.elem1 = value;
    }

    public boolean equals(Object object) {
        if (object instanceof Doc) {
            Doc anotherDoc = (Doc) object;
            if (elem == null && anotherDoc.getElem() != null) {
                return false;
            } 
            if (elem != null && anotherDoc.getElem() == null) {
                return false;
            } 
            if (elem1 == null && anotherDoc.getElem1() != null) {
                return false;
            } 
            if (elem1 != null && anotherDoc.getElem1() == null) {
                return false;
            }
            
            Object obj1 = elem1;
            Object obj2 = anotherDoc.getElem1();
            if ((obj1 instanceof Element) && (obj2 instanceof Element)) {
                Element elem1 = (Element )obj1;
                Element elem2 = (Element) obj2;
                
                if (!(elem1.getLocalName().equals(elem2.getLocalName()))) {
                    return false;
                }
            } else {
                return false;
            }
            
            if (!elem.equals(anotherDoc.getElem())) {
                return false;
            }
            
            // Passed all equality tests
            return true;
        } else {
            return false;
        }
    }
    
}