/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Radek Felcman - April 2018 - 2.7.2
package org.eclipse.persistence.testing.jaxb.json.nil;

import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name = "maskFormatEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaskFormatEntry {


    @XmlElementWrapper(name="arrayList")
    @XmlElement(name="item")
    @XmlNullPolicy(xsiNilRepresentsNull = false)
    private ArrayList<String> arrayList;

    @XmlElement(name="field1")
    private String field1;

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public boolean equals(Object obj) {
        MaskFormatEntry maskFormatEntry = (MaskFormatEntry) obj;

        if((arrayList == null && maskFormatEntry.getArrayList() != null) || (arrayList != null && !arrayList.equals(maskFormatEntry.getArrayList()))){
            return false;
        }

        if((field1 == null && maskFormatEntry.getField1() != null) || (field1 != null && !field1.equals(maskFormatEntry.getField1()))){
            return false;
        }
        return true;
    }

}
