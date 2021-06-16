/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.model.basket;

import org.eclipse.persistence.jpa.rs.annotations.RestPageableQueries;
import org.eclipse.persistence.jpa.rs.annotations.RestPageableQuery;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPARS_BASKET_ITEM")
@NamedQueries({
        @NamedQuery(
                name = "BasketItem.findAll",
                query = "SELECT bi FROM BasketItem bi ORDER BY bi.id"),
        @NamedQuery(
                name = "BasketItem.findAllPageable",
                query = "SELECT bi FROM BasketItem bi ORDER BY bi.id"),
        @NamedQuery(
                name = "BasketItem.deleteAll",
                query = "DELETE FROM BasketItem bi")
})
@RestPageableQueries({
        @RestPageableQuery(queryName = "BasketItem.findAllPageable", limit = 20)
})
public class BasketItem {

    @Id
    @Column(name = "ITEM_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BASKET_ID")
    private Basket basket;

    @Column(name = "ITEM_NAME")
    private String name;

    @Column(name = "ITEM_QTY")
    private Integer qty;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "BasketItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", qty=" + qty +
                '}';
    }
}
