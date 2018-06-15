/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Item {

   @XmlSchemaType(name="string")
   private int id;
   private String[] description;
   @XmlPath(value="money/cost/text()")
   private BigDecimal cost;
   @XmlPath(value="money/price/text()")
   private BigDecimal price;

   @XmlTransient
   public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean equals(Object obj) {
        Item item;
        try {
            item = (Item) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        if(!Arrays.equals(description, item.description)){
            return false;
        }
        return id == item.id && price.equals(item.price) && cost.equals(item.cost);
    }

}
