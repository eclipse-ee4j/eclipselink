/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     07/31/2008-1.1 Guy Pelletier
//       - 241388: JPA cache is not valid after a series of EntityManager operations
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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
    private List<Child> children = new ArrayList<>();

    @Column
    private String serialNumber;

    protected Parent() {}

    public Parent(boolean template) {
        Child child = new Child();
        addChild(child);
    }

    @Override
    public void addChild(Child cs) {
        cs.setParent(this);
        this.children.add(cs);
    }

    @Override
    public List<Child> getChildren() {
        return children;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getSerialNumber() {
        return this.serialNumber;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
