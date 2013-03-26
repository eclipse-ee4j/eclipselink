/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.jpa.rs.config.ConfigDefaults;

/**
 * This class is used to wrap collection of homogeneous simple java type attributes, such as 
 * Strings, Integers, etc..., and MUST NOT be used to wrap collection of attributes with the type 
 * that is assignable from PersistenceWeavedRest.
 * 
 * @author gonural
 *
 */
@XmlRootElement(name = ConfigDefaults.JPARS_LIST_GROUPING_NAME)
public class SimpleHomogeneousList {
    @SuppressWarnings("rawtypes")
    private List<JAXBElement> items;

    /**
     * Instantiates a new simple list.
     */
    public SimpleHomogeneousList() {
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    @SuppressWarnings("rawtypes")
    @XmlAnyElement(lax = true)
    public List<JAXBElement> getItems() {
        return items;
    }

    /**
     * Sets the items.
     *
     * @param items the new items
     */
    @SuppressWarnings("rawtypes")
    public void setItems(List<JAXBElement> items) {
        this.items = items;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        SimpleHomogeneousList other = (SimpleHomogeneousList) obj;
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
