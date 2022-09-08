/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import org.eclipse.persistence.config.QueryHints;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "QUERY_ORDER")
@NamedQueries({
        @NamedQuery(
                name="QueryOrder.findAllOrdersWithEmptyOrderLines",
                query="SELECT o FROM QueryOrder o WHERE o.queryOrderLines IS EMPTY"
        ),
        //PRINT_INNER_JOIN_IN_WHERE true
        @NamedQuery(
                name="QueryOrder.findAllOrdersWithEmptyOrderLinesHintTrue",
                query="SELECT o FROM QueryOrder o WHERE o.queryOrderLines IS EMPTY"
                ,
                hints={
                        @QueryHint(name=QueryHints.INNER_JOIN_IN_WHERE_CLAUSE, value="true")
                }
        ),
        //PRINT_INNER_JOIN_IN_WHERE false
        @NamedQuery(
                name="QueryOrder.findAllOrdersWithEmptyOrderLinesHintFalse",
                query="SELECT o FROM QueryOrder o WHERE o.queryOrderLines IS EMPTY"
                ,
                hints={
                        @QueryHint(name=QueryHints.INNER_JOIN_IN_WHERE_CLAUSE, value="false")
                }
        )
})
public class QueryOrder {

    @Id
    private long orderKey;

    @OneToMany(mappedBy = "queryOrder")
    List<QueryOrderLine> queryOrderLines = new ArrayList<>();

    public long getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(long orderKey) {
        this.orderKey = orderKey;
    }
}