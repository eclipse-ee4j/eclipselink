/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.attachment;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class Employee {
    
    @XmlElementRef(name="fooA")
    public JAXBElement<Byte[]> ref1;
    
    @XmlElementRefs({@XmlElementRef(name="fooB"), @XmlElementRef(name="fooC")})
    public List<JAXBElement> ref2;

    public boolean equals(Object obj) {
        Employee emp = (Employee)obj;
        boolean equal = true;
        equal = equal && emp.ref1.getName().equals(ref1.getName()) && compareByteArrays(ref1.getValue(), emp.ref1.getValue());
        
        for(int i = 0; i < ref2.size(); i++) {
            JAXBElement next1 = ref2.get(i);
            JAXBElement next2 = emp.ref2.get(i);
            equal = equal && next1.getName().equals(next2.getName());
            if(next1.getDeclaredType() == String.class) {
                equal = equal && next1.getValue().equals(next2.getValue());
            } else {
                equal = equal && compareByteArrays((byte[])next1.getValue(), (byte[])next2.getValue());
            }
        }
        return equal;
    }
    
    private boolean compareByteArrays(byte[] a, byte[] b) {
        for(int i = 0; i < a.length; i++) {
            if(!(a[i] == b[i])) {
                return false;
            }
        }
        return true;
    }
    
    private boolean compareByteArrays(Byte[] a, Byte[] b) {
        for(int i = 0; i < a.length; i++) {
            if(!(a[i].byteValue() == b[i].byteValue())) {
                return false;
            }
        }
        return true;
    }    
}
