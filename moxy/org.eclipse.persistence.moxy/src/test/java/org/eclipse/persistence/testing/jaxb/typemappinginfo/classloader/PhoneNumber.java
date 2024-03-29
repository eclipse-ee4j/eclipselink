/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     bdoughan - February 5/2010 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.classloader;

public class PhoneNumber {

    public String areaCode;
    public String number;

    public boolean equals(Object theObject){
        if(theObject instanceof PhoneNumber phone){
            if(!areaCode.equals(phone.areaCode)){
               return false;
           }
           if(!number.equals(phone.number)){
               return false;
           }
           return true;
        }else{
           return false;
        }
    }

}
