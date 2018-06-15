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
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MyEnumModel {

    private MyEnum e;

    public void setMyEnum(MyEnum myEnum) {
        e = myEnum;

    }

    public MyEnum getMyEnum() {
        return e;
    }

    public boolean equals(Object object) {
        MyEnumModel example = ((MyEnumModel) object);
        return example.e.equals(this.e);
    }
}
