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
 *     Denise Smith  July 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class TestObject {
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected List<byte[]> byteArrayListTest;

    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] byteArrayTest;

    @XmlElement(type = Boolean.class)
    protected Object booleanTest;

    @XmlElement(type = Boolean.class)
    protected List<Object> booleanListTest;
    
    @XmlElement (defaultValue="1")
    public int fld1;
    
    public boolean equals(Object compareobject){
    	if(!(compareobject instanceof TestObject)){
    		return false;
    	}
    	if(fld1 != ((TestObject)compareobject).fld1){
    		return false;
    	}
    	
    	if(!booleanTest.equals(((TestObject)compareobject).booleanTest)){
    		return false;
    	}
    	if(booleanListTest.size() != ((TestObject)compareobject).booleanListTest.size()){
    		return false;
    	}
    	
    	for(int i=0;i<booleanListTest.size(); i++){
    		if(!booleanListTest.get(i).equals(((TestObject)compareobject).booleanListTest.get(i))){
    			return false;
    		}
    	}
    	
    	if(!equalByteArrays(byteArrayTest, ((TestObject)compareobject).byteArrayTest)){
    		return false;
    	}
    	if(byteArrayListTest.size() != ((TestObject)compareobject).byteArrayListTest.size()){
    		return false;
    	}
    	for(int i=0;i< byteArrayListTest.size(); i++){
    		Object next = byteArrayListTest.get(i);
    		Object nextCompare = ((TestObject)compareobject).byteArrayListTest.get(i);
    		if(!equalByteArrays(next, nextCompare)){
        		return false;
        	}
    	}
    	
    	return true;
    }
    
    private boolean equalByteArrays(Object obj1, Object obj2) {
    	if(obj1 instanceof byte[] && obj2 instanceof byte[]){
    		byte[] array1 = (byte[])obj1;
    		byte[] array2 = (byte[])obj2;
    		//covers the case where both are null
            if(array1 == array2) {
                return true;
            }
            if (array1.length != array2.length) {
                return false;
            }

            // check each base64 byte in sequence
            for (int i = 0; i < array1.length; i++) {
                if (array1[i] != array2[i]) {
                    return false;
                }
            }

            return true;
    	}
        return false;
    }
}
