/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="customer")
public class CustomerPackageLevel {

    public String fieldProperty;
    private String property;


    public String getProperty() {
        return property;
    }

    public void setProperty(String value) {
        property = value;
    }

    public boolean equals(Object obj) {
        CustomerPackageLevel cust = (CustomerPackageLevel)obj;


        return this.fieldProperty.equals(cust.fieldProperty) && this.property.equals(cust.getProperty());
    }
}
