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
//     12/2/2009-2.1 Guy Pelletier
//       - 296289: Add current annotation metadata support on mapped superclasses to EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.xml.relationships;

public class Company {
    private CEO ceo;
    private String name;

    public CEO getCeo() {
        return ceo;
    }

    public String getName() {
        return name;
    }

    public void setCeo(CEO ceo) {
        this.ceo = ceo;
    }

    public void setName(String name) {
        this.name = name;
    }
}
