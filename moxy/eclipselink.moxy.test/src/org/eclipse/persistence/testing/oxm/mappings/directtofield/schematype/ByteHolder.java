/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype;

public class ByteHolder {

	private Byte[] bytes;
  
  public ByteHolder() {
    super();
  }

  public Byte[] getBytes() {
    return bytes;
  }

  public void setBytes(Byte[] newBytes) {
    bytes = newBytes;
  }


  public boolean equals(Object object) {
    try {
      ByteHolder byteHolder = (ByteHolder) object;
			if (this.getBytes().length != byteHolder.getBytes().length){
				return false;
			}
			for(int i=0; i<getBytes().length; i++){				
				if(!(getBytes()[i].equals(byteHolder.getBytes()[i])))	{
					return false;
				}
			}
			
      return true;
    } catch(ClassCastException e) {
      return false;
    }
  }

  public String toString()
  {
    String returnString =  "ByteHolder: ";
			for(int i=0; i<bytes.length; i++){
				returnString += bytes[i];
			}
		return returnString;
  } 
}
