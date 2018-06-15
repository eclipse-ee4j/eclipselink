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
 * Constrained externally through validation.xml.
 */
@XmlRootElement
public class ExternallyConstrainedEmployee {

    @XmlAttribute
    private Integer id;

    @XmlAttribute
    private Integer age;

    public ExternallyConstrainedEmployee(){
    }

    public ExternallyConstrainedEmployee withId(Integer id){
        this.id = id;
        return this;
    }

    public ExternallyConstrainedEmployee withAge(Integer age){
        this.age = age;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExternallyConstrainedEmployee that = (ExternallyConstrainedEmployee) o;

        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (age != null ? age.hashCode() : 0);
        return result;
    }
}
