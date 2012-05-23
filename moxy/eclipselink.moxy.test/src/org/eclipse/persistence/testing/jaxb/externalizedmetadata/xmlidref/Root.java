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
 * dmccann - August 31/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {
    @XmlElement(name="employee")
    public List<Employee> employees;
    @XmlElement(name="address")
    public List<Address> addresses;
    
    public boolean equals(Object compareObj){
    	if (compareObj instanceof Root){
    		Root rootObj = (Root)compareObj;
    		return (employees == null && rootObj.employees ==null || employees.equals(rootObj.employees) )&& 
    		(addresses == null && rootObj.addresses ==null || addresses.equals(rootObj.addresses) );
    	}
    	return false;
    }
    
}
