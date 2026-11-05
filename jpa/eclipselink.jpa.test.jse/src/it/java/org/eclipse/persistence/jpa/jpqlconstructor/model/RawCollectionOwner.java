/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.jpqlconstructor.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.CollectionTable;

/** The raw property type deliberately leaves the collection element classification unresolved. */
@Entity(name = "ICRRawCollectionOwner")
@Table(name = "JPA_ICR_RAW_OWNER")
@Access(AccessType.PROPERTY)
@NamedQuery(name = "ICR.rawElements", query = "select e from ICRRawCollectionOwner o join o.elements e where o.id = :id order by e")
@SuppressWarnings({"rawtypes", "deprecation"})
public class RawCollectionOwner {
    private Integer id;
    private Collection<String> elements = new ArrayList<>();

    public RawCollectionOwner() {
    }

    public RawCollectionOwner(Integer id) {
        this.id = id;
    }

    @Id
    @Column(name = "OWNER_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @BasicCollection(valueColumn = @Column(name = "ELEMENT_VALUE"))
    @CollectionTable(name = "JPA_ICR_RAW_ELEMENT",
            primaryKeyJoinColumns = @PrimaryKeyJoinColumn(name = "OWNER_ID", referencedColumnName = "OWNER_ID"))
    public Collection getElements() {
        return elements;
    }

    public void setElements(Collection<String> elements) {
        this.elements = elements;
    }

}
