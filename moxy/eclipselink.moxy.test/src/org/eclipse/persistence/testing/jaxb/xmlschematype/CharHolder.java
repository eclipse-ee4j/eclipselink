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
//    Denise Smith - March 2013
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement
public class CharHolder {
     public int testIntDefault;

     @XmlSchemaType(name="string")
     public int testIntString;

     @XmlSchemaType(name="unsignedShort")
     public int testIntWithunsignedShort;

     public char testCharDefault;

     @XmlSchemaType(name="string")
     public char testCharWithString;

     @XmlSchemaType(name="unsignedShort")
     public char testCharWithunsignedShort;

     @XmlSchemaType(name="integer")
     public char testCharWithInteger;

     @XmlSchemaType(name="decimal")
     public char testCharWithDecimal;

     @XmlSchemaType(name="float")
     public char testOtherChar;

     @XmlSchemaType(name="byte")
     public char testCharZero;

     public boolean equals(Object obj){
         if(obj instanceof CharHolder){
             CharHolder compare = (CharHolder)obj;
             return testIntString == compare.testIntString &&
             testIntDefault == compare.testIntDefault &&
             testIntWithunsignedShort == compare.testIntWithunsignedShort &&
             testCharDefault == compare.testCharDefault &&
             testCharWithString == compare.testCharWithString &&
             testCharWithunsignedShort == compare.testCharWithunsignedShort &&
             testCharWithInteger == compare.testCharWithInteger &&
             testCharWithDecimal == compare.testCharWithDecimal &&
             testOtherChar == compare.testOtherChar &&
             testCharZero == compare.testCharZero;
         }
         return false;
     }
}
