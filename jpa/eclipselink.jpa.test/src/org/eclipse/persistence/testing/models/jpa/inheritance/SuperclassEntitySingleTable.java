/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial implementation
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name="JPA_STI_SUPERCLASS")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASSTYPE", discriminatorType=DiscriminatorType.STRING, length=3)
@DiscriminatorValue(value="SUP")
public class SuperclassEntitySingleTable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable=false)
    protected long id;

    @Column(name="SUPERCLASS_ATTRIBUTE", nullable=false)
    protected String superclassAttribute;

    public SuperclassEntitySingleTable() {
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
