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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

/* $Header: Employee.java 19-oct-2006.17:22:32 mfobrien Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    mfobrien    10/19/06 - Creation
 */

/**
 *  @version $Header: Employee.java 19-oct-2006.17:22:32 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection;

import java.util.Vector;
import java.awt.Image;

public class Employee {
    public static final int DEFAULT_ID = 123;
    private int id;
    private Vector photos;
    private Vector extraPhotos;

    public Employee() {
        super();
    }

    public Employee(int id) {
        super();
        this.id = id;
    }

    public Employee(int id, Vector photos) {
        super();
        this.id = id;
        this.photos = photos;
    }

    public static Employee example1() {
        Vector photos = new Vector();
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        return new Employee(DEFAULT_ID, photos);
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
    }

    /*
      public Image getPhoto(int index) {
          return (Image) photos.get(index);
      }

      public void setPhoto(Image anImage, int index) {
          photos.set(index, anImage);
      }
    */
    public Vector getPhotos() {
        return photos;
    }

    public void setPhotos(Vector vectorOfPhotos) {
        photos = vectorOfPhotos;
    }

    public Vector getExtraPhotos() {
        return extraPhotos;
    }

    public void setExtraPhotos(Vector vectorOfPhotos) {
        extraPhotos = vectorOfPhotos;
    }

    public String toString() {
        String returnString = "Employee: " + this.getID() + " ";
        if (getPhotos() != null) {
            returnString += "Photos: ";
            for (int i = 0; i < getPhotos().size(); i++) {
                Object next = getPhotos().elementAt(i);
                if (next != null) {
                    returnString += (next + " ");
                } else {
                    returnString += ("null_item" + " ");
                }
            }
        }

        if (getExtraPhotos() != null) {
            returnString += "Extra Photos: ";
            for (int i = 0; i < getExtraPhotos().size(); i++) {
                Object next = getExtraPhotos().elementAt(i);
                returnString += (next.toString() + " ");
            }
        }
        return returnString;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employeeObject = (Employee)object;

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
                (this.getPhotos().isEmpty() && employeeObject.getPhotos().isEmpty())) && (((this.getExtraPhotos() == null) && (employeeObject.getExtraPhotos() == null)) ||//
                (this.getExtraPhotos().isEmpty() && employeeObject.getExtraPhotos().isEmpty()))    ) {
            return true;
        }

        boolean equal = true;

        // hash equality changes
        for (int i = 0; i < getPhotos().size(); i++) {
            equal = equal && equalByteArrays((byte[])getPhotos().get(i), (byte[])employeeObject.getPhotos().get(i));
        }
        return equal;
    }

    // override the contains check on a Vector of byte[] arrays - see TypeDirectMappingTestSuite
    private boolean equalByteArrays(byte[] array1, byte[] array2) {
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
