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

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;

public class QueryResultListItem {
    @SuppressWarnings("rawtypes")
    List<JAXBElement> fields;

    /**
     * Instantiates a new query result list item.
     */
    public QueryResultListItem() {
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    @SuppressWarnings("rawtypes")
    @XmlAnyElement
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
}