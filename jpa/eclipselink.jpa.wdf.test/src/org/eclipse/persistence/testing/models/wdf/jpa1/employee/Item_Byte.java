/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

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

@Entity
@Table(name = "BYTE_ITEM")
public class Item_Byte implements Serializable {
    @Id
    @Column(name = "ITEM_ID")
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
