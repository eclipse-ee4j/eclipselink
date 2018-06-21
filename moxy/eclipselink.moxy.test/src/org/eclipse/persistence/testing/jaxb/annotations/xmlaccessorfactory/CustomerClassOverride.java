/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory;

import javax.xml.bind.annotation.XmlRootElement;

import com.sun.xml.bind.XmlAccessorFactory;

@XmlRootElement(name="customer")
@XmlAccessorFactory(ClassLevelAccessorFactory.class)
public class CustomerClassOverride {

    public String fieldProperty;
    private String property;


    public String getProperty() {
        return property;
    }

    public void setProperty(String value) {
        property = value;
    }

    public boolean equals(Object obj) {
        CustomerClassOverride cust = (CustomerClassOverride)obj;
        return this.fieldProperty.equals(cust.fieldProperty) && this.property.equals(cust.getProperty());
    }
}
