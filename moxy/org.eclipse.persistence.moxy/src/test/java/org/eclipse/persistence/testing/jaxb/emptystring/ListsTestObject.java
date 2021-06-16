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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.emptystring;

import java.math.BigDecimal;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListsTestObject {
    private List<String> strings;
    private List<Integer> integers;
    private List<BigDecimal> bigDecimals;

    public List<String> getStrings() {
        return strings;
    }
    public void setStrings(List<String> strings) {
        this.strings = strings;
    }
    public List<Integer> getIntegers() {
        return integers;
    }
    public void setIntegers(List<Integer> integers) {
        this.integers = integers;
    }
    public List<BigDecimal> getBigDecimals() {
        return bigDecimals;
    }
    public void setBigDecimals(List<BigDecimal> bigDecimals) {
        this.bigDecimals = bigDecimals;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof ListsTestObject)){
            return false;
        }
        ListsTestObject compareObject = (ListsTestObject)obj;
        if(integers.size() != compareObject.integers.size()){
            return false;
        }
        if(!integers.containsAll(compareObject.integers)){
            return false;
        }

        if(bigDecimals.size() != compareObject.bigDecimals.size()){
            return false;
        }
        if(!bigDecimals.containsAll(compareObject.bigDecimals)){
            return false;
        }

        if(strings.size() != compareObject.strings.size()){
            return false;
        }
        if(!strings.containsAll(compareObject.strings)){
            return false;
        }

        return true;
    }

}
