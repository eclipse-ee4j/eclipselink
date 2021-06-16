/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

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
