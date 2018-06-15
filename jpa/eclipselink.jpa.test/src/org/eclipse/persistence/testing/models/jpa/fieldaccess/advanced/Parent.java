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
//     07/31/2008-1.1 Guy Pelletier
//       - 241388: JPA cache is not valid after a series of EntityManager operations
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="FIELD_PARENT")
public class Parent implements ParentInterface, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Version
    @Column
    private Integer version;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.ALL})
    private List<Child> children = new ArrayList<Child>();

    @Column
    private String serialNumber;

    protected Parent() {}

    public Parent(boolean template) {
        Child child = new Child();
        addChild(child);
    }

    public void addChild(Child cs) {
        cs.setParent(this);
        this.children.add(cs);
    }

    public List<Child> getChildren() {
        return children;
    }

    public Integer getId() {
        return this.id;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public int getVersion() {
        return version;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
