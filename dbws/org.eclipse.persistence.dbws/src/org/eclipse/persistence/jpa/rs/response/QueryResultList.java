/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.jpa.rs.config.ConfigDefaults;

@XmlRootElement(name = ConfigDefaults.JPARS_LIST_GROUPING_NAME)
public class QueryResultList {

    private List<QueryResultListItem> items;

    /**
     * Instantiates a new query result list.
     */
    public QueryResultList() {
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    @XmlElement(name = ConfigDefaults.JPARS_LIST_ITEM_NAME)
    public List<QueryResultListItem> getItems() {
        return items;
    }

    /**
     * Sets the items.
     *
     * @param items the new items
     */
    public void setItems(List<QueryResultListItem> items) {
        this.items = items;
    }

    /**
     * Adds the item.
     *
     * @param item the item
     */
    public void addItem(QueryResultListItem item) {
        if (items == null) {
            items = new ArrayList<QueryResultListItem>();
        }
        items.add(item);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        QueryResultList other = (QueryResultList) obj;
        if (items == null) {
            if (other.items != null) {
                return false;
            }
        } else if (!items.equals(other.items)) {
            return false;
        }
        return true;
    }
}
