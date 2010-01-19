/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/* $Header: Employee.java 12-dec-2006.16:06:40 mmacivor Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    mmacivor    12/12/06 - 
    mfobrien    10/23/06 - Creation
 */

/**
 *  @version $Header: Employee.java 12-dec-2006.16:06:40 mmacivor Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.activation.DataHandler;

import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class Employee {
    public static final int DEFAULT_ID = 123;
    private int id;
    private byte[] photo;
    private byte[] extraPhoto;
    private DataHandler data;
    private MyImage myImage;

    public Employee() {
        super();
    }

    public Employee(int id) {
        super();
        this.id = id;
    }

    public Employee(int id, byte[] photo) {
        super();
        this.id = id;
        this.photo = photo;
    }

    public static Employee example1() {
        return new Employee(DEFAULT_ID, MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getExtraPhoto() {
        return extraPhoto;
    }

    public void setExtraPhoto(byte[] extraPhoto) {
        this.extraPhoto = extraPhoto;
    }
    
    public DataHandler getData() {
        return data;
    }
    
    public void setData(DataHandler theData) {
        this.data = theData;
    }
    
    public MyImage getMyImage() {
    	return this.myImage;
    }
    
    public void setMyImage(MyImage mi) {
    	this.myImage = mi;
    }

    public String toString() {
        String returnString = "Employee: " + this.getID() + " ";
        if (getPhoto() != null) {
            returnString += ("Photo: " + photo + " ");
        }

        if (getExtraPhoto() != null) {
            returnString += ("extraPhoto: " + extraPhoto + " ");
        }
        return returnString;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employeeObject = (Employee)object;

        if ((this.getPhoto() == null) && (employeeObject.getPhoto() != null)) {
            return false;
        }
        if ((employeeObject.getPhoto() == null) && (this.getPhoto() != null)) {
            return false;
        }

        /**
         * Note: do not use Vector.contains() for byte[] arrays since each .getBytes() will return
         * a different hash-value and will not pass the embedded (==) during the .contain check.
         * You must check each base64 byte in sequence
         */
        boolean equal = true;

        // hash equality changes
        equal = equal && equalByteArrays(getPhoto(), employeeObject.getPhoto());
        
        if(data == null && employeeObject.getData() != null){
        	return false;
        }
        
        if(data != null && employeeObject.getData() == null){
        	return false;
        }
        
        if(this.getMyImage() != null) {
        	if(!(this.getMyImage().equals(employeeObject.getMyImage()))) {
        		return false;
        	}
        } else {
        	if(employeeObject.getMyImage() != null) {
        		return false;
        	}
        }
        
        if(data != null){
        	if(!data.getContentType().equals(employeeObject.getData().getContentType())){
        	    return false;
        	}
    	    try {
    	    	Object obj1 =  data.getContent();
    	    	Object obj2 =  employeeObject.getData().getContent();
    	    	if(data.getContent() instanceof ByteArrayInputStream && employeeObject.getData().getContent() instanceof ByteArrayInputStream){
    	    		ByteArrayInputStream controlStream = ((ByteArrayInputStream)data.getContent());
    	    		ByteArrayInputStream testStream = ((ByteArrayInputStream)employeeObject.getData().getContent());
    	    		if(controlStream.available() != testStream.available()){
    	    			return false;
    	    		}
    	    		
    	    		byte[] controlBytes = new byte[controlStream.available()];
    	    		byte[] testBytes = new byte[testStream.available()];
    	    		
    	    		if(!equalByteArrays(controlBytes, testBytes)){
    	    			return false;
    	    		}    	    		

    	    	}else{
					if(!data.getContent().equals(employeeObject.getData().getContent())){
					   return false;
					}
    	    	}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}        
        }
        
        return equal;
    }

    // override the contains check on a Vector of byte[] arrays - see TypeDirectMappingTestSuite
    private boolean equalByteArrays(byte[] array1, byte[] array2) {
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
}
