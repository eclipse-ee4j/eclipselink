/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.model.basket;

import org.eclipse.persistence.jpa.rs.annotations.RestPageableQueries;
import org.eclipse.persistence.jpa.rs.annotations.RestPageableQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

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
