/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - December 20/2010 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.hexbinary;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Customer {
   @XmlElement
   @XmlSchemaType(name="hexBinary")
   @XmlJavaTypeAdapter(HexBinaryAdapter.class)
   public byte[] hexBytes;
   
   @XmlElement
   public byte[] base64Bytes;

   public boolean equals(Object o) {
       Customer c;
       try {
           c = (Customer)o;
       } catch (ClassCastException cce) {
           return false;
       }
       if (c.hexBytes == null || c.base64Bytes == null) {
           return false;
       }
       if (hexBytes.length != c.hexBytes.length || base64Bytes.length != c.base64Bytes.length) {
           return false;
       }
       for (int i=0; i<hexBytes.length; i++) {
           if (hexBytes[i] != c.hexBytes[i]) {
               return false;
           }
       }
       for (int i=0; i<base64Bytes.length; i++) {
           if (base64Bytes[i] != c.base64Bytes[i]) {
               return false;
           }
       }
       return true;
   }
}