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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;
import org.eclipse.persistence.testing.oxm.mappings.choice.Address;

@XmlRootElement(name="employee")
public class EmployeeWithAddress {

    public AddressNoCtor address;
    public String name;
    
    public boolean equals(Object obj) {
    	EmployeeWithAddress emp = (EmployeeWithAddress)obj;
    	if(!name.equals(emp.name)){
    		return false;
    	}
    	if(!address.equals(emp.address)){
    		return false;
    	}
        return true;
    }
}
