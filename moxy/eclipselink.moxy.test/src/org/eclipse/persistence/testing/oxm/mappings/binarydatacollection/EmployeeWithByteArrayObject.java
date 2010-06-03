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
 *     Denise Smith - December 15, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.activation.DataHandler;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.oxm.conversion.Base64;

public class EmployeeWithByteArrayObject {
    public static final int DEFAULT_ID = 123;
    private int id;
    private Vector photos;

    public EmployeeWithByteArrayObject() {
        super();
    }

    public EmployeeWithByteArrayObject(int id) {
        super();
        this.id = id;
    }

    public EmployeeWithByteArrayObject(int id, Vector photos) {
        super();
        this.id = id;
        this.photos = photos;
    }

    public static Employee example1() {
        Vector photos = new Vector();
        byte[] bytes = MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes();
        Byte[] objectBytes = new Byte[bytes.length];
        for (int index = 0; index < bytes.length; index++) {
            objectBytes[index] = new Byte(bytes[index]);
        }
        photos.addElement(objectBytes);
        photos.addElement(objectBytes);
        photos.addElement(objectBytes);
        return new Employee(DEFAULT_ID, photos);
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
    }

    public Vector getPhotos() {
        return photos;
    }

    public void setPhotos(Vector vectorOfPhotos) {
        photos = vectorOfPhotos;
    }

    public String toString() {
        String returnString = "Employee: " + this.getID() + " ";
        if (getPhotos() != null) {
            returnString += "Photos: ";
            for (int i = 0; i < getPhotos().size(); i++) {
                Object next = getPhotos().elementAt(i);
                if (next != null) {
                    returnString += next + " ";
                    if(next instanceof Byte[]) {
                        returnString = "\n" + "-->" + new String(Base64.base64Encode(((byte[])ConversionManager.getDefaultManager().convertObject(next, byte[].class))));
                    }
                } else {
                    returnString += ("null_item" + " ");
                }
            }
        }

        return returnString;
    }

    public boolean equals(Object object) {
        if (!(object instanceof EmployeeWithByteArrayObject)) {
            return false;
        }
        EmployeeWithByteArrayObject employeeObject = (EmployeeWithByteArrayObject)object;

        if ((this.getPhotos() == null) && (employeeObject.getPhotos() != null)) {
            return false;
        }
        if ((employeeObject.getPhotos() == null) && (this.getPhotos() != null)) {
            return false;
        }

        /**
         * Note: do not use Vector.contains() for byte[] arrays since each .getBytes() will return
         * a different hash-value and will not pass the embedded (==) during the .contain check.
         * You must check each base64 byte in sequence
         */
        if ((this.getID() == employeeObject.getID()) && (((this.getPhotos() == null) && (employeeObject.getPhotos() == null)) ||//
                (this.getPhotos().isEmpty() && employeeObject.getPhotos().isEmpty())) ) {
            return true;
        }

        boolean equal = true;

        
        // hash equality changes
        for (int i = 0; i < getPhotos().size(); i++) {
        	try{
                equal = equal && equalByteArrays((Byte[])getPhotos().get(i), (Byte[])employeeObject.getPhotos().get(i));
        	}catch(ClassCastException e){
        		equal = equal && equalDataHandlers((DataHandler)getPhotos().get(i), (DataHandler)employeeObject.getPhotos().get(i));
        	}
        }
        return equal;
    }

    private boolean equalDataHandlers(DataHandler data, DataHandler data2){
    	if(!data.getContentType().equals(data2.getContentType())){
    	    return false;
    	}
	    try {
	    	Object obj1 =  data.getContent();
	    	Object obj2 =  data2.getContent();
	    	if(data.getContent() instanceof ByteArrayInputStream && data2.getContent() instanceof ByteArrayInputStream){
	    		ByteArrayInputStream controlStream = ((ByteArrayInputStream)data.getContent());
	    		ByteArrayInputStream testStream = ((ByteArrayInputStream)data2.getContent());
	    		if(controlStream.available() != testStream.available()){
	    			return false;
	    		}
	    		
	    		Byte[] controlBytes = new Byte[controlStream.available()];
	    		Byte[] testBytes = new Byte[testStream.available()];
	    		
	    		if(!equalByteArrays(controlBytes, testBytes)){
	    			return false;
	    		}    	    		

	    	}else{
				if(!data.getContent().equals(data2.getContent())){
				   return false;
				}
	    	}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}        
		return true;
    }
    
    // override the contains check on a Vector of byte[] arrays - see TypeDirectMappingTestSuite
    private boolean equalByteArrays(Byte[] array1, Byte[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        // check each base64 byte in sequence
        for (int i = 0; i < array1.length; i++) {
            if (!(array1[i].equals(array2[i]))) {
                return false;
            }
        }

        return true;
    }
}
