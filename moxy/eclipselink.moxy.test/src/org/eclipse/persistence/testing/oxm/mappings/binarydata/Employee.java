/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class Employee {
    public static final int DEFAULT_ID = 123;
    private int id;
    private byte[] photo;
    private byte[] extraPhoto;

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