/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 04 May 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"areaCode", "number"})
@XmlAccessorType(XmlAccessType.FIELD)
public class PhoneNumber {

    private int areaCode;
    private int number;

    public int getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
        PhoneNumber p = (PhoneNumber) obj;
        return this.getAreaCode() == p.getAreaCode() && this.getNumber() == p.getNumber();
    }

    @Override
    public String toString() {
        return "(" + getAreaCode() + ") " + getNumber();
    }

}
