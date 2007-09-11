/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="phone")
@XmlType(name="phone-number")
public class PhoneNumberList {

    @XmlValue
    @XmlList
    public java.util.ArrayList<String> numbers;    
    
    public boolean equals(Object o) {
        if(!(o instanceof PhoneNumberList) || o == null) {
            return false;
        } else {
            return ((PhoneNumberList)o).numbers.equals(this.numbers);
        }
    }
    
    public String toString() {
        return "EMPLOYEE(" + numbers + ")";
    }    
}