/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.special;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains only method constraint.
 *
 * @author Marcel Valovy
 */
@XmlRootElement
public class MethodAnnotatedEmployee {

    @XmlAttribute
    private Integer id;

    public MethodAnnotatedEmployee(){
    }

    public MethodAnnotatedEmployee withId(Integer id){
        this.id = id;
        return this;
    }

    @NotNull
    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodAnnotatedEmployee that = (MethodAnnotatedEmployee) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
