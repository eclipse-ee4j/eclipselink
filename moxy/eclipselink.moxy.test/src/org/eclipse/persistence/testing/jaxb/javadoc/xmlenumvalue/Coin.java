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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


    @XmlType(name="coin-enum")
    @XmlEnum(Integer.class)
    public enum Coin {
        @XmlEnumValue("1") PENNY(1),
        @XmlEnumValue("5") NICKEL(5),
        @XmlEnumValue("10") DIME(10),
        @XmlEnumValue("25") QUARTER(25);

        private final int value;


        Coin(int v){
            value = v;
        }

        public Integer value(){
            return value;
        }

     }
