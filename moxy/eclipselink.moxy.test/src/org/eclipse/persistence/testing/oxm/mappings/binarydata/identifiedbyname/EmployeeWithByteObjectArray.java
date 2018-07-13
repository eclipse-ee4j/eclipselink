/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - December 15, 2009
package org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.activation.DataHandler;

import org.eclipse.persistence.testing.oxm.mappings.binarydata.Employee;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class EmployeeWithByteObjectArray {
    public static final int DEFAULT_ID = 123;
    private int id;
    private Byte[] photo;
    private DataHandler data;

    public EmployeeWithByteObjectArray() {
        super();
    }

    public EmployeeWithByteObjectArray(int id) {
        super();
        this.id = id;
    }

    public EmployeeWithByteObjectArray(int id, Byte[] photo) {
        super();
        this.id = id;
        this.photo = photo;
    }

    public static EmployeeWithByteObjectArray example1() {
        byte[] bytes = MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes();
        Byte[] objectBytes = new Byte[bytes.length];
        for (int index = 0; index < bytes.length; index++) {
            objectBytes[index] = new Byte(bytes[index]);
        }
        return new EmployeeWithByteObjectArray(DEFAULT_ID, objectBytes);
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
    }

    public Byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(Byte[] photo) {
        this.photo = photo;
    }

    public DataHandler getData() {
        return data;
    }

    public void setData(DataHandler theData) {
        this.data = theData;
    }

    public String toString() {
        String returnString = "Employee: " + this.getID() + " ";
        if (getPhoto() != null) {
            returnString += ("Photo: " + photo + " ");
        }

        return returnString;
    }

    public boolean equals(Object object) {
        if (!(object instanceof EmployeeWithByteObjectArray)) {
            return false;
        }
        EmployeeWithByteObjectArray employeeObject = (EmployeeWithByteObjectArray)object;

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

                    Byte[] controlBytes = new Byte[controlStream.available()];
                    Byte[] testBytes = new Byte[testStream.available()];

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
    private boolean equalByteArrays(Byte[] array1, Byte[] array2) {
        //covers the case where both are null
        if(array1 == array2) {
            return true;
        }
        if (array1.length != array2.length) {
            return false;
        }

        // check each base64 byte in sequence
        for (int i = 0; i < array1.length; i++) {
            if (!array1[i].equals(array2[i])) {
                return false;
            }
        }

        return true;
    }
}
