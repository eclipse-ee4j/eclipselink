/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.model.basket;

import org.eclipse.persistence.jpa.rs.annotations.RestPageable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "JPARS_BASKET")
@NamedQueries({
        @NamedQuery(
                name = "Basket.deleteAll",
                query = "DELETE FROM Basket b")
})
public class Basket {

    @Id
    @Column(name = "BASKET_ID")
    private Integer id;

    @Column(name = "BASKET_NAME")
    private String name;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL)
    @RestPageable(limit = 2)
    private List<BasketItem> basketItems = new ArrayList<>();

    public Basket() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BasketItem> getBasketItems() {
        return basketItems;
    }

    public void setBasketItems(List<BasketItem> basketItems) {
        this.basketItems = basketItems;
    }

    @Override
    public String toString() {
        return "Basket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", basketItems=" + basketItems +
                '}';
    }
}
