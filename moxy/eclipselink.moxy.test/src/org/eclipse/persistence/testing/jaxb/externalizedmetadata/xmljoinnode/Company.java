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
 * dmccann - August 26/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import java.util.List;

public class Company {
    public List<Object> employees;
    public List<Address> buildingAddresses;
    
    public Company() {}
    public Company(List<Object> employees, List<Address> buildingAddresses) {
        this.employees = employees;
        this.buildingAddresses = buildingAddresses;
    }
    
    public boolean equals(Object obj){
    	if(obj instanceof Company){
    		Company compObj = (Company)obj;
    		if(employees.size() != compObj.employees.size() || buildingAddresses.size() != compObj.buildingAddresses.size()){
    			return false;
    		}
    		return employees.containsAll(compObj.employees) 
    		      && compObj.employees.containsAll(employees)
    		      && buildingAddresses. containsAll(compObj.buildingAddresses)
    		      && compObj.buildingAddresses.containsAll(buildingAddresses);
    		//if(employees.containsAll(compObj.employees) && compObj.employees.containsAll()){
    			 
    		//}
    		
    		//return true;
    	}
    	return false;
    }
}
