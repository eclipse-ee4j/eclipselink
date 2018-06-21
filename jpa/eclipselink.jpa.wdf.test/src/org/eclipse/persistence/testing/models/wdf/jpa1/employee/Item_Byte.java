/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.testing.framework.wdf.customizer.AdjustArrayTypeCustomizer;

@Entity
@Table(name = "BYTE_ITEM")
@Customizer(AdjustArrayTypeCustomizer.class)
public class Item_Byte implements Serializable {
    @Id
    @Column(name = "ITEM_ID", columnDefinition=TravelProfile.BINARY_16_COLUMN_NOT_NULL)
    private byte[] itemId;
    private String namespace;
    private String text;
    private String name;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Item_Attribute_Byte> attr2;
    private static final long serialVersionUID = 1L;

    protected Item_Byte() {
    }

    public Item_Byte(byte[] id, String namespace, String name, String text) {
        this.itemId = id;
        if (namespace == null)
            this.namespace = "SAP";
        else
            this.namespace = namespace;
        this.name = name;
        if (text == null)
            this.text = "text";
        else
            this.text = text;
        this.attr2 = new LinkedList<Item_Attribute_Byte>();
    }

    public byte[] getItemId() {
        return this.itemId;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return this.name;
    }

    public void addAttribute(Item_Attribute_Byte attribute) {
        if (!attr2.contains(attribute))
            attr2.add(attribute);
    }

    public void addAttr(String key, String value) {
        Item_Attribute_Byte itemAtt = new Item_Attribute_Byte(this, this.itemId, key, value);
        addAttribute(itemAtt);
    }

    public void addAttributes(LinkedList<Item_Attribute_Byte> attrList) {
        attr2.addAll(attrList);
    }

    public List<Item_Attribute_Byte> getAttributes() {
        return attr2;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Item: id=" + this.itemId);
        str.append(", name=" + this.name);
        for (Iterator<Item_Attribute_Byte> iter = attr2.iterator(); iter.hasNext();) {
            Item_Attribute_Byte element = iter.next();
            str.append(", " + element.getAttKey() + " - " + element.getAttVal());
        }
        return str.toString();
    }

}
