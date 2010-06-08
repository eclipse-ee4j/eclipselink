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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
