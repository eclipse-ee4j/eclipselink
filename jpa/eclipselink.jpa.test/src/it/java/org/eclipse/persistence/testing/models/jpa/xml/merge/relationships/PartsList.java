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
package org.eclipse.persistence.testing.models.jpa.xml.merge.relationships;

import jakarta.persistence.*;
import java.util.Collection;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.GenerationType.*;

@Entity(name="XMLMergePartsList")
@Table(name="CMP3_XML_MERGE_PARTSLIST")
public class PartsList implements java.io.Serializable {
    private Integer partsListId;
    private int version;
    private Collection<Item> items;

    public PartsList() {}


    @Id
    @GeneratedValue(strategy=TABLE, generator="XML_MERGE_PARTSLIST_TABLE_GENERATOR")
    // This table generator is overridden in the XML, therefore it should
    // not be processed. If it is processed, because the table name is so long
    // it will cause an error. No error means everyone is happy.
    @TableGenerator(
        name="XML_MERGE_PARTSLIST_TABLE_GENERATOR",
        table="CMP3_XML_MERGE_CUSTOMER_SEQ_INCORRECT_LONG_NAME_WILL_CAUSE_ERROR",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PARTSLIST_SEQ"
    )
    @Column(name="ID")
    public Integer getPartsListId() {
        return partsListId;
    }

    public void setPartsListId(Integer id) {
        this.partsListId = id;
    }

    @Version
    @Column(name="VERSION")
    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    @ManyToMany(cascade=PERSIST)
    @JoinTable(
        name="CMP3_XML_MERGE_PARTSLIST_ITEM",
        joinColumns=@JoinColumn(name="PARTSLIST_ID", referencedColumnName="ID"),
        inverseJoinColumns=@JoinColumn(name="ITEM_ID", referencedColumnName="ITEM_ID")
    )
    public Collection<Item> getItems() {
        return items;
    }

    public void setItems(Collection<Item> items) {
        this.items = items;
    }
}
