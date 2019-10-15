/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NumberHolder {

    public int intTest;
    public Integer integerTest;
    public BigDecimal bigDecimalTest;
    public BigInteger bigIntegerTest;
    public double doubleTest;
    public float floatTest;
    public short shortTest;
    public long longTest;
    public List<Integer> listIntegersTest;
    public List<Number> listNumbersTest;

    public NumberHolder(){
        listIntegersTest = new ArrayList();
        listNumbersTest = new ArrayList();
    }

    public boolean equals(Object obj){
        if(obj instanceof NumberHolder){
            NumberHolder nh = (NumberHolder)obj;
            if(intTest != nh.intTest || doubleTest != nh.doubleTest || floatTest != nh.floatTest || shortTest !=nh.shortTest || longTest != nh.longTest){
                return false;
            }
            if(!(integerTest.equals(nh.integerTest)) || !(bigDecimalTest.equals(nh.bigDecimalTest)) || !(bigIntegerTest.equals(nh.bigIntegerTest))){
                return false;
            }
            return compareLists(listIntegersTest, nh.listIntegersTest) && compareLists(listNumbersTest, nh.listNumbersTest);
        }
        return false;
    }

    private boolean compareLists(List list1, List list2){
        if(list1.size() != list2.size()){
            return false;
        }
        for(int i=0;i<list1.size(); i++){
            if(!list1.get(i).equals(list2.get(i))){
                return false;
            }
        }
        return true;
    }

}
