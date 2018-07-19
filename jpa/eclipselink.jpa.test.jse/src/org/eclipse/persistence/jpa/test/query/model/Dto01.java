/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     19/07/2018 - Jody Grassel 
 *       - 537795 : CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
 ******************************************************************************/

package org.eclipse.persistence.jpa.test.query.model;

public class Dto01 {
    String str1;
    String str2;
    String str3;
    String str4;
    Integer integer1;
    Integer integer2;

    public Dto01(String str1,String str2,String str3,String str4) {
        this.str1 = str1;
        this.str2 = str2;
        this.str3 = str3;
        this.str4 = str4;

    }

    public Dto01(String str1,String str2,Integer integer1,Integer integer2) {
        this.str1 = str1;
        this.str2 = str2;
        this.integer1 = integer1;
        this.integer2 = integer2;
    }
    
    public Dto01(String str1,String str2,Long integer1,Long integer2) {
        this.str1 = str1;
        this.str2 = str2;
        this.integer1 =  new Integer(integer1.intValue());
        this.integer2 = new Integer(integer2.intValue());
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public String getStr3() {
        return str3;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public String getStr4() {
        return str4;
    }

    public void setStr4(String str4) {
        this.str4 = str4;
    }

    public Integer getInteger1() {
        return integer1;
    }

    public void setInteger1(Integer integer1) {
        this.integer1 = integer1;
    }

    public Integer getInteger2() {
        return integer2;
    }

    public void setInteger2(Integer integer2) {
        this.integer2 = integer2;
    }

    @Override
    public String toString() {
        return "Dto01 [str1=" + str1 + ", str2=" + str2 + ", str3=" + str3 + ", str4=" + str4 + ", integer1=" + integer1 + ", integer2=" + integer2 + "]";
    }
    
    
}
