/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - February 5/2010 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.classloader;

public class Address {

    public String street;
    public String city;

    public boolean equals(Object theObject){
        if(theObject instanceof Address){
        Address addr = (Address)theObject;
           if(!street.equals(addr.street)){
               return false;
           }
           if(!city.equals(addr.city)){
               return false;
           }
           return true;
        }else{
           return false;
        }
    }

}
