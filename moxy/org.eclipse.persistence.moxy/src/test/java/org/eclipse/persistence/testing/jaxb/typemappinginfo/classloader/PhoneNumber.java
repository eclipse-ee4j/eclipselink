/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - February 5/2010 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.classloader;

public class PhoneNumber {

    public String areaCode;
    public String number;

    public boolean equals(Object theObject){
        if(theObject instanceof PhoneNumber){
            PhoneNumber phone = (PhoneNumber)theObject;
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
