/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.special;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains only custom constraint, and as such serves as a good testing class for whether we recognize custom
 * constraints correctly.
 */
@XmlRootElement
public class CustomAnnotatedEmployee {

    @CustomAnnotation
    @XmlAttribute
    private Integer id;

    public CustomAnnotatedEmployee(){
    }

    public CustomAnnotatedEmployee withId(Integer id){
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomAnnotatedEmployee that = (CustomAnnotatedEmployee) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
