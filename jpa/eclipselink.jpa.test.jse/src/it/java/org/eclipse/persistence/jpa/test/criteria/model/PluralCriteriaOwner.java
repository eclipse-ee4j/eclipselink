/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.criteria.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_PC_OWNER")
public class PluralCriteriaOwner {
    @Id
    public int id;
    @Column(name = "NULLABLE_TEXT")
    public String nullableText;
    @ElementCollection
    @CollectionTable(name = "JPA_PC_TAG", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "TAG_VALUE")
    public Set<String> tags = new LinkedHashSet<>();
    @ElementCollection
    @CollectionTable(name = "JPA_PC_LABEL", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "LABEL_VALUE")
    @OrderColumn(name = "LABEL_POS")
    public List<String> labels = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "JPA_PC_VALUE", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "ITEM_VALUE")
    public Collection<String> values = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "JPA_PC_MAP", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @MapKeyColumn(name = "MAP_KEY")
    @Column(name = "MAP_VALUE")
    public Map<String, String> codes = new LinkedHashMap<>();
    @ManyToMany
    @JoinTable(name = "JPA_PC_OWNER_ELEMENT", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = {@JoinColumn(name = "ELEMENT_FIRST", referencedColumnName = "FIRST_ID"),
                    @JoinColumn(name = "ELEMENT_SECOND", referencedColumnName = "SECOND_ID")})
    public Set<PluralCriteriaElement> elements = new LinkedHashSet<>();
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    public PluralCriteriaOwner parent;
    @Embedded
    public PluralCriteriaDetails details = new PluralCriteriaDetails();

    public PluralCriteriaOwner() {
    }

    public PluralCriteriaOwner(int id) {
        this.id = id;
    }
}
