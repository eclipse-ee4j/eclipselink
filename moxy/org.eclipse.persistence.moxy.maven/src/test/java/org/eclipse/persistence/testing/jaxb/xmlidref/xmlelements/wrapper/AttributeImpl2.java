/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - February 20, 2013
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.wrapper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

public class AttributeImpl2 implements Attribute{
     private String id;

     /**
       * getter for xsd:string/String id
       * @return id
       */
     @XmlAttribute(name="id")
     @XmlID
     public String getId() {
         return id;
     }

     public void setId(String newId) {
         id = newId;
     }


     public boolean equals(Object obj){
         if(obj instanceof AttributeImpl2){

             return id.equals(((AttributeImpl2)obj).id);
         }
         return false;
     }
}
