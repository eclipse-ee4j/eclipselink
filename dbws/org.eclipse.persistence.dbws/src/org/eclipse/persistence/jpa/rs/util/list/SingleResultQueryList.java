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
 * This class is used to wrap collection of attributes returned by the single-result JPA report query.
 *
 * @author gonural
 *
 */
@XmlRootElement(name = ConfigDefaults.JPARS_LIST_ITEM_NAME)
public class SingleResultQueryList {
    @SuppressWarnings("rawtypes")
    private List<JAXBElement> fields;

    /**
     * Instantiates a new query result list item.
     */
    public SingleResultQueryList() {
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    @SuppressWarnings("rawtypes")
    @XmlAnyElement(lax = true)
    public List<JAXBElement> getFields() {
        return fields;
    }

    /**
     * Sets the fields.
     *
     * @param fields the new fields
     */
    @SuppressWarnings("rawtypes")
    public void setFields(List<JAXBElement> fields) {
        this.fields = fields;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
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
        SingleResultQueryList other = (SingleResultQueryList) obj;
        if (fields == null) {
            if (other.fields != null) {
                return false;
            }
        } else if (!fields.equals(other.fields)) {
            return false;
        }
        return true;
    }
}
