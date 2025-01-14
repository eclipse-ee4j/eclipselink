/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     12/02/2010-2.2 Guy Pelletier
//       - 324471: Do not default to VariableOneToOneMapping for interfaces unless a managed class implementing it is found
//     01/25/2011-2.3 Guy Pelletier
//       - 333488: Serializable attribute being defaulted to a variable one to one mapping and causing exception
package org.eclipse.persistence.testing.models.jpa.validation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;
import org.eclipse.persistence.annotations.InstantiationCopyPolicy;

import java.io.Serializable;

import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name="VALIDATION_ITEM")
@NamedQuery(
        name="findAllItemsByName",
        query="SELECT OBJECT(item) FROM Item item WHERE item.name = ?1"
)
@InstantiationCopyPolicy // explicitly exercise the code that sets this (even though it is the default)
public class Item implements Serializable {
    private Integer itemId;
    private int version;
    private String name;
    private String description;
    private Serializable tag;

    public Item() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="VALIDATION_ITEM_TABLE_GENERATOR")
    @TableGenerator(
            name="VALIDATION_ITEM_TABLE_GENERATOR",
            table="VALIDATION_ITEM_SEQ",
            pkColumnName="SEQ_NAME",
            valueColumnName="SEQ_COUNT",
            pkColumnValue="ITEM_SEQ"
    )
    @Column(name="ID")
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer id) {
        this.itemId = id;
    }

    @Version
    @Column(name="ITEM_VERSION")
    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // This should default to a basic mapping (and not a variable 1-1)
    public Serializable getTag() {
        return tag;
    }

    public void setTag(Serializable tag) {
        this.tag = tag;
    }
}
