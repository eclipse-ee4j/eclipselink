/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//      gonural - initial
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Query;
import org.eclipse.persistence.jpa.rs.ReservedWords;

/**
 * This class is used to wrap collection of Query objects
 * @see Query
 *
 * @author gonural
 *
 */
@XmlRootElement(name = ReservedWords.JPARS_LIST_GROUPING_NAME)
public class QueryList {
    private List<Query> list;

    public QueryList() {
    }

    @XmlElement(name = ReservedWords.JPARS_LIST_ITEM_NAME)
    public List<Query> getList() {
        return list;
    }

    public void setList(List<Query> list) {
        this.list = list;
    }
}
