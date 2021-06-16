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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.unqualified;

import java.math.BigDecimal;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexType", propOrder = {"global","local"})
public class ComplexType {

    @XmlElement(name = "Global")
    protected boolean global;

    @XmlElementRef(name = "Local", type = ComplexType.TestLocal.class)
    protected ComplexType.TestLocal local;

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean value) {
        this.global = value;
    }

    public ComplexType.TestLocal getLocal() {
        return local;
    }

    public void setLocal(ComplexType.TestLocal value) {
        this.local = value;
    }

    @Override
    public boolean equals(Object o) {
        if(null == o || o.getClass() != this.getClass()) {
            return false;
        }
        ComplexType test = (ComplexType) o;
        if(global != test.isGlobal()) {
            return false;
        }
        if(null == local) {
            return null == test.getLocal();
        } else {
            return local.equals(test.getLocal());
        }
    }

    public static class TestLocal extends JAXBElement<String> {

        protected final static QName NAME = new QName("", "Local");

        public TestLocal(String value) {
            super(NAME, ((Class) String.class), ComplexType.class, value);
        }

        public TestLocal() {
            super(NAME, ((Class) String.class), ComplexType.class, null);
        }

        @Override
        public boolean equals(Object o) {
            if(null == o || o.getClass() != this.getClass()) {
                return false;
            }
            TestLocal test = (TestLocal) o;
            return this.getName().equals(test.getName()) && this.getValue().equals(test.getValue());
        }

    }

}
