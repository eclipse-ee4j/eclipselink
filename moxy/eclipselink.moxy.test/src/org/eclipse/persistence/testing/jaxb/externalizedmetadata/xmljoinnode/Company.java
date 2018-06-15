/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - August 26/2010 - 2.2 - Initial implementation
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
