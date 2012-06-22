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
 *     mmacivor - 2010-03-09 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="bean-b")
public class BeanB {
    @XmlElementRef(name="value")
    public JAXBElement<Integer> value;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof BeanB)) {
            return false;
        }
        
        BeanB bean = (BeanB)obj;
        JAXBElement<Integer> objValue = bean.value;
        
        if(objValue == null && this.value == null) {
            return true;
        }
        if(objValue != null ^ this.value != null) {
            return false;
        }
        
        return (objValue.getValue().equals(this.value.getValue()) && objValue.getName().equals(this.value.getName()));
    }    
}
