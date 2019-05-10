/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.models.weaving;

import javax.persistence.*;
import static javax.persistence.GenerationType.*;

@Entity
@Table(name="CMP3_ITEM")
@NamedQuery(
    name="findAllItemsByName",
    query="SELECT OBJECT(i) FROM Item i WHERE i.name = ?1"
)
public class Item implements java.io.Serializable {

    private Integer itemId;
    private int version;
    private String name;
    private String description;

    public Item() {
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="ITEM_TABLE_GENERATOR")
    @TableGenerator(
        name="ITEM_TABLE_GENERATOR",
        table="CMP3_CUSTOMER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ITEM_SEQ"
    )
    @Column(name="ITEM_ID")
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
}
