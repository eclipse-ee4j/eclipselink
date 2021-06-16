/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package jpql.query;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@Entity
@NamedQueries
({
   @NamedQuery(name="order.doubleValue", query="select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue"),
   @NamedQuery(name="order.sum1",        query="SELECT SUM(o.totalPrice) FROM Order o"),
   @NamedQuery(name="order.sum2",        query="SELECT SUM(o.price) FROM Order o"),
   @NamedQuery(name="order.sum3",        query="SELECT SUM(o.realPrice) FROM Order o"),
   @NamedQuery(name="order.sqrt",        query="SELECT SQRT(o.totalPrice) FROM Order o"),
   @NamedQuery(name="order.coalesce1",   query="SELECT COALESCE(o.price, o.price) FROM Order o"),
   @NamedQuery(name="order.coalesce2",   query="SELECT COALESCE(o.totalPrice, SQRT(o.realPrice)) FROM Order o"),
   @NamedQuery(name="order.coalesce3",   query="SELECT COALESCE(o.number, e.name) FROM Order o, Employee e"),
   @NamedQuery(name="order.coalesce4",   query="SELECT COALESCE(o.price, o.number) FROM Order o"),
   @NamedQuery(name="order.abs",         query="SELECT ABS(o.totalPrice) FROM Order o")
})
@SuppressWarnings("unused")
public class Order {

    private BigInteger price;
    private BigDecimal realPrice;
    private double totalPrice;
    @Id private int id;
    private String number;
}
