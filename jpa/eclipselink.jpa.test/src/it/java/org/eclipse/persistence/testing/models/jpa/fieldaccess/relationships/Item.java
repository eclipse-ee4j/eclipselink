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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.*;

@Entity(name="FieldAccessItem")
@Table(name="CMP3_FIELDACCESS_ITEM")
@NamedQuery(
        name="findAllFieldAccessItemsByName",
        query="SELECT OBJECT(item) FROM FieldAccessItem item WHERE item.name = ?1"
)
public class Item implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy=TABLE, generator="FIELDACCESS_ITEM_TABLE_GENERATOR")
    @TableGenerator(
            name="FIELDACCESS_ITEM_TABLE_GENERATOR",
            table="CMP3_FIELDACCESS_CUSTOMER_SEQ",
            pkColumnName="SEQ_NAME",
            valueColumnName="SEQ_COUNT",
            pkColumnValue="ITEM_SEQ"
    )
    @Column(name="ID")
    private Integer itemId;
    @Version
    @Column(name="ITEM_VERSION")
    private int version;
    private String name;
    private String description;

    public Item() {}

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer id) {
        this.itemId = id;
    }

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
