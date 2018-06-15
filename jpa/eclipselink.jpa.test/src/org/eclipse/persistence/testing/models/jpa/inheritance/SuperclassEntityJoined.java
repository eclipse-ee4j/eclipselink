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
@Table(name="JPA_JOINED_SUPERCLASS")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="CLASSTYPE", discriminatorType=DiscriminatorType.STRING, length=3)
@DiscriminatorValue(value="SUP")
public class SuperclassEntityJoined {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable=false)
    protected long id;

    @Column(name="SUPERCLASS_ATTRIBUTE", nullable=false)
    protected String superclassAttribute;

    public SuperclassEntityJoined() {
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
