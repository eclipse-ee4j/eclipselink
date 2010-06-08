/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - May 10/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.lang.reflect.Array;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root", namespace="urn:example")
public class MultiDimensionalArrayRoot {

    private int[][][] int3dArray;
    private int[][] int2dArray;
    private char[][] char2dArray;
    private Integer[][][] integer3dArray;
    private Employee[][] employee2dArray;
    private ClassWithInnerClass.MyInner[][] innerClass2dArray;

    public int[][][] getInt3dArray() {
        return int3dArray;
    }

    public void setInt3dArray(int[][][] int3dArray) {
        this.int3dArray = int3dArray;
    }

    public int[][] getInt2dArray() {
        return int2dArray;
    }

    public void setInt2dArray(int[][] int2dArray) {
        this.int2dArray = int2dArray;
    }

    public char[][] getChar2dArray() {
        return char2dArray;
    }

    public void setChar2dArray(char[][] char2dArray) {
        this.char2dArray = char2dArray;
    }

    public Integer[][][] getInteger3dArray() {
        return integer3dArray;
    }

    public void setInteger3dArray(Integer[][][] integer3dArray) {
        this.integer3dArray = integer3dArray;
    }

    public Employee[][] getEmployee2dArray() {
        return employee2dArray;
    }

    public void setEmployee2dArray(Employee[][] employee2dArray) {
        this.employee2dArray = employee2dArray;
    }

    public ClassWithInnerClass.MyInner[][] getInnerClass2dArray() {
        return innerClass2dArray;
    }

    public void setInnerClass2dArray(ClassWithInnerClass.MyInner[][] innerClass2dArray) {
        this.innerClass2dArray = innerClass2dArray;
    }

    public boolean equals(Object object) {
        if(null == object) {
            return false;
        }
        try {
            MultiDimensionalArrayRoot test = (MultiDimensionalArrayRoot) object;
            if(!equals(char2dArray, test.getChar2dArray())) {
                return false;
            }
            if(!equals(employee2dArray, test.getEmployee2dArray())) {
                return false;
            }
            if(!equals(innerClass2dArray, test.getInnerClass2dArray())) {
                return false;
            }
            if(!equals(int2dArray, test.getInt2dArray())) {
                return false;
            }
            if(!equals(int3dArray, test.getInt3dArray())) {
                return false;
            }
            if(!equals(integer3dArray, test.getInteger3dArray())) {
                return false;
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

    private boolean equals(Object controlArray, Object testArray) {
        int controlLength = Array.getLength(controlArray);
        int testLength = Array.getLength(testArray);
        if(controlLength != testLength) {
            return false;
        }
        Object controlItem = Array.get(controlArray, 0);
        if(controlItem.getClass().isArray()) {
            for(int x=0; x<controlLength; x++) {
                controlItem = Array.get(controlArray, x);
                Object testItem = Array.get(testArray, x);
                if(!equals(controlItem, testItem)) {
                    return false;
                }
            }
        } else {
            for(int x=0; x<controlLength; x++) {
                controlItem = Array.get(controlArray, x);
                Object testItem = Array.get(testArray, x);
                if(!controlItem.equals(testItem)) {
                    return false;
                }
            }
        }
        return true;
    }

}