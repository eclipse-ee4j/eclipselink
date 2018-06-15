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
//      gonural - initial
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.jpa.rs.ReservedWords;

/**
 * This class is used to wrap collection of Link objects
 * @see Link
 *
 * @author gonural
 *
 */
@XmlRootElement(name = ReservedWords.JPARS_LIST_GROUPING_NAME)
public class LinkList {
    private List<Link> list;

    public LinkList() {
    }

    @XmlElement(name = ReservedWords.JPARS_LIST_ITEM_NAME)
    public List<Link> getList() {
        return list;
    }

    public void setList(List<Link> list) {
        this.list = list;
    }
}
