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
*     bdoughan - February 5/2010 - 2.0.1 - Initial implementation
******************************************************************************/
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