/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      gonural - initial
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
