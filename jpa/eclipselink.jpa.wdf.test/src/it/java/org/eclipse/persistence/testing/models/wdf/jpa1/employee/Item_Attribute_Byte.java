/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@IdClass(Item_Attribute_Byte.AttributeId.class)
@Table(name = "BYTE_ITEM_ATT")
public class Item_Attribute_Byte implements Serializable {

    @Id
    @Column(name = "ITEM_ID", insertable = false, updatable = false)
    private byte[] itemId;

    @Id
    @Column(name = "ATT_KEY")
    private String attKey;

    @Column(name = "ATT_VAL")
    private String attVal;

    @SuppressWarnings("unused")
    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item_Byte item;

    private static final long serialVersionUID = 1L;

    protected Item_Attribute_Byte() {
    }

    public Item_Attribute_Byte(Item_Byte item, byte[] itemId, String key, String val) {
        this.item = item;
        this.itemId = itemId;
        this.attKey = key;
        this.attVal = val;
    }

    public byte[] getItemId() {
        return this.itemId;
    }

    public String getAttKey() {
        return this.attKey;
    }

    public String getAttVal() {
        return this.attVal;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("item " + this.getItemId());
        str.append(": " + this.getAttKey());
        str.append(" - " + this.attVal);
        return str.toString();
    }

    // Primary Key Class with wrong equals and hashCode methods (does not use util.Arrays.equals)
    public static class AttributeId implements Serializable {

        private byte[] itemId;
        private String attKey;
        private static final long serialVersionUID = 1L;

        public AttributeId() {
        }

        protected AttributeId(byte[] itemId, String key) {
            this.itemId = itemId;
            this.attKey = key;
        }

        public byte[] getItemId() {
            return this.itemId;
        }

        public void setItemId(byte[] itemId) {
            this.itemId = itemId;
        }

        public String getAttKey() {
            return this.attKey;
        }

        public void setAttKey(String attKey) {
            this.attKey = attKey;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof AttributeId)) {
                return false;
            }
            AttributeId other = (AttributeId) o;
            return this.itemId.equals(other.itemId) && this.attKey.equals(other.attKey);
        }

        @Override
        public int hashCode() {
            return this.itemId.hashCode() ^ this.attKey.hashCode();
        }
    }

}
