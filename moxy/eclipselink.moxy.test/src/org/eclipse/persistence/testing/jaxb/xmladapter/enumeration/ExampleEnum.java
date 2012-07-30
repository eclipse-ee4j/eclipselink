/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum()
@XmlType(name = "EXAMLE_ENUM")
public enum ExampleEnum {

    @XmlEnumValue("1")
    VALUE1(1), 
    @XmlEnumValue("2")
    VALUE2(2), 
    @XmlEnumValue("3")
    VALUE3(3);
    
    int value;
    
    private ExampleEnum(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}