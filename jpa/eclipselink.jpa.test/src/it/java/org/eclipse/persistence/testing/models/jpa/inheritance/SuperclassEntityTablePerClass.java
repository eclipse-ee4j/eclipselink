/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial implementation
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name="JPA_TPC_SUPERCLASS")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SuperclassEntityTablePerClass {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable=false)
    protected long id;

    @Column(name="SUPERCLASS_ATTRIBUTE", nullable=false)
    protected String superclassAttribute;

    public SuperclassEntityTablePerClass() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSuperclassAttribute() {
        return this.superclassAttribute;
    }

    public void setSuperclassAttribute(String superclassAttribute) {
        this.superclassAttribute = superclassAttribute;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " id:" + getId() + " hashcode: " + System.identityHashCode(this);
    }

}
