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

public class Dto03 {
    String str1;
    String str2;
    Long long1;
    Long long2;

    public Dto03(String str1,String str2,Long long1,Long long2) {
        this.str1 = str1;
        this.str2 = str2;
        this.long1 = long1;
        this.long2 = long2;

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

    public Long getLong1() {
        return long1;
    }

    public void setLong1(Long long1) {
        this.long1 = long1;
    }

    public Long getLong2() {
        return long2;
    }

    public void setLong2(Long long2) {
        this.long2 = long2;
    }

    @Override
    public String toString() {
        return "Dto03 [str1=" + str1 + ", str2=" + str2 + ", long1=" + long1 + ", long2=" + long2 + "]";
    }
    
    
}
