/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial implementation
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="JPA_TPC_SUBCLASS")
public class SubclassEntityTablePerClass extends MappedSuperclassTablePerClass {

    @Column(name="SUBCLASS_ATTRIBUTE")
    protected String subclassAttribute;

    public SubclassEntityTablePerClass() {
        super();
    }

    public String getSubclassAttribute() {
        return subclassAttribute;
    }

    public void setSubclassAttribute(String subclassAttribute) {
        this.subclassAttribute = subclassAttribute;
    }

}
