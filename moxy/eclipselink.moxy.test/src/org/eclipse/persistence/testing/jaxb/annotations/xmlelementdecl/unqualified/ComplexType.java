/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.unqualified;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
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