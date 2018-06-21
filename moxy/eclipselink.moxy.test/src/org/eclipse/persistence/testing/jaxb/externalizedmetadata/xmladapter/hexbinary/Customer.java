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
// dmccann - December 21/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.hexbinary;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

//@XmlRootElement
public class Customer {
   //@XmlElement
   //@XmlSchemaType(name="hexBinary")
   //@XmlJavaTypeAdapter(HexBinaryAdapter.class)
   public byte[] hexBytes;

   //@XmlElement
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
