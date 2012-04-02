/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.readonly;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class RootObject {
   String readOnlyString;
   String writeOnlyString;
   
   String[] readOnlyStringArray;
   String[] writeOnlyStringArray;
   
   @XmlElement
   public String getWriteOnlyString() {
       return writeOnlyString;
   }
   
   @XmlElement
   public void setReadOnlyString(String string) {
       this.readOnlyString = string;
   }
   
   @XmlElement
   public String[] getWriteOnlyStringArray() {
       return writeOnlyStringArray;
   }
   
   @XmlElement
   public void setReadOnlyStringArray(String[] array) {
       this.readOnlyStringArray = array;
   }
   
   public boolean equals(Object obj) {
       if(!(obj instanceof RootObject)) {
           return false;
       }
       
       RootObject root = (RootObject)obj;
       
       boolean equal = true;
       equal = equal && (readOnlyString == root.readOnlyString || readOnlyString.equals(root.readOnlyString));
       equal = equal && (writeOnlyString == root.writeOnlyString || writeOnlyString.equals(root.writeOnlyString));
       equal = equal && (readOnlyStringArray == root.readOnlyStringArray || compareArrays(readOnlyStringArray, root.readOnlyStringArray));
       equal = equal && (writeOnlyStringArray == root.writeOnlyStringArray || compareArrays(writeOnlyStringArray, root.writeOnlyStringArray));
       
       return equal;
   }


   private boolean compareArrays(String[] array1, String[] array2) {
       if(!(array1.length == array2.length)) {
           return false;
       }
    
       for(int i = 0; i < array1.length; i++) {
           if(!(array1[i].equals(array2[i]))) {
               return false;
           }
       }
       return true;
   }
}
