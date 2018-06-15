/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.json.attribute;

public class MailingAddressNoRoot extends AddressNoRoot {
   public String postalCode;

   public boolean equals(Object obj) {
       MailingAddressNoRoot add;
       try {
           add = (MailingAddressNoRoot) obj;
       } catch (ClassCastException cce) {
           return false;
       }
       if(!super.equals(obj)){
           return false;
       }
       if(postalCode == null){
           if(add.postalCode != null){
               return false;
           }
       }else if(!postalCode.equals(add.postalCode)){
           return false;
       }
       return true;
   }


}
